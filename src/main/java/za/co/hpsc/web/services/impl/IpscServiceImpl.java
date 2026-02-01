package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.IpscService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IpscServiceImpl implements IpscService {
    @Override
    public IpscResponseHolder importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException {

        if ((cabFileContent == null) || (cabFileContent.isBlank())) {
            log.error("The provided cab file is null or empty.");
            throw new ValidationException("The provided CAB file is null or empty.");
        }

        // Imports CAB file; maps, saves, and returns responses
        IpscRequestHolder ipscRequestHolder = readIpscRequests(cabFileContent);
        List<IpscResponse> ipscResponses = mapIpscRequests(ipscRequestHolder);
        saveIpscResponse(ipscResponses);

        return new IpscResponseHolder(ipscResponses);
    }

    /**
     * Deserializes the provided CAB file content into an {@link IpscRequest} object and populates
     * an {@link IpscRequestHolder} with various request data, such as clubs, matches, tags, members,
     * enrolled members, squads, and scores.
     *
     * @param cabFileContent The CAB file content as a string to be deserialized.
     * @return An {@link IpscRequest} object containing the deserialized request data.
     * @throws ValidationException If the provided CAB file content is invalid or missing.
     * @throws FatalException      If an I/O error occurs while reading the CAB file.
     */
    protected IpscRequestHolder readIpscRequests(String cabFileContent)
            throws ValidationException, FatalException {

        if ((cabFileContent == null) || (cabFileContent.isBlank())) {
            log.error("The provided CAB file is null or empty.");
            throw new ValidationException("The provided CAB file is null or empty.");
        }

        try {
            // Deserializes CAB file content into typed IPSC requests
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            IpscRequest ipscRequest = objectMapper.readValue(cabFileContent, IpscRequest.class);

            IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
            ipscRequestHolder.setClubs(readRequests(ipscRequest.getClub(), ClubRequest.class));
            ipscRequestHolder.setMatches(readRequests(ipscRequest.getMatch(), MatchRequest.class));
            ipscRequestHolder.setStages(readRequests(ipscRequest.getStage(), StageRequest.class));
            ipscRequestHolder.setTags(readRequests(ipscRequest.getTag(), TagRequest.class));
            ipscRequestHolder.setMembers(readRequests(ipscRequest.getMember(), MemberRequest.class));
            ipscRequestHolder.setClassifications(readRequests(ipscRequest.getClassification(), ClassificationRequest.class));
            ipscRequestHolder.setEnrolledMembers(readRequests(ipscRequest.getEnrolled(), EnrolledRequest.class));
            ipscRequestHolder.setSquads(readRequests(ipscRequest.getSquad(), SquadRequest.class));
            ipscRequestHolder.setTeams(readRequests(ipscRequest.getTeam(), TeamRequest.class));
            ipscRequestHolder.setScores(readRequests(ipscRequest.getScore(), ScoreRequest.class));

            return ipscRequestHolder;
        } catch (MismatchedInputException | IllegalArgumentException e) {
            log.error("Error parsing JSON data: {}", e.getMessage(), e);
            throw new ValidationException("Invalid JSON data format.", e);
        } catch (IOException e) {
            log.error("Error reading JSON data: {}", e.getMessage(), e);
            throw new FatalException("Error reading JSON data.", e);
        }
    }

    /**
     * Maps IPSC requests to a list of IPSC responses. This method processes the matches, stages,
     * enrolled members, scores, tags, and clubs from the given {@link IpscRequestHolder}, groups
     * them based on match IDs, and constructs corresponding {@link IpscResponse} objects.
     *
     * @param ipscRequestHolder A holder object that contains lists of matches, stages, enrolled
     *                          members, scores, tags, members, and clubs to be processed.
     * @return A list of {@link IpscResponse} objects that encapsulate the mapped data, including
     * match details, associated tags, stages, enrolled members, scores, members, and club
     * information.
     */
    protected List<IpscResponse> mapIpscRequests(IpscRequestHolder ipscRequestHolder)
            throws ValidationException {

        if (ipscRequestHolder == null) {
            log.error("IPSC request holder is null.");
            throw new ValidationException("IPSC request holder can not be null");
        }

/*
        if ((ipscRequestHolder.getClubs() == null) || (ipscRequestHolder.getMatches() == null) ||
                (ipscRequestHolder.getStages() == null) || (ipscRequestHolder.getTags() == null) ||
                (ipscRequestHolder.getMembers() == null) || (ipscRequestHolder.getEnrolledMembers() == null) ||
                (ipscRequestHolder.getScores() == null)) {

            log.error("IPSC request data is null.");
            throw new ValidationException("IPSC request data can not be null.");
        }
*/

        List<IpscResponse> ipscResponses = new ArrayList<>();
        // Maps IPSC requests to responses by match ID
        ipscRequestHolder.getMatches().forEach(match -> {
            MatchResponse matchResponse = new MatchResponse(match);
            Integer matchId = matchResponse.getMatchId();

            List<TagRequest> tagRequests = ipscRequestHolder.getTags();

            List<StageRequest> stageRequests = ipscRequestHolder.getStages().stream()
                    .filter(stage -> stage.getMatchId()
                            .equals(matchId)).toList();
            List<EnrolledRequest> enrolledRequests = ipscRequestHolder.getEnrolledMembers().stream()
                    .filter(enrolled -> enrolled.getMatchId()
                            .equals(matchId)).toList();
            List<ScoreRequest> scoreRequests = ipscRequestHolder.getScores().stream()
                    .filter(score -> score.getMatchId()
                            .equals(matchId)).toList();

            ipscResponses.add(new IpscResponse(tagRequests, matchResponse, stageRequests,
                    enrolledRequests, scoreRequests));
        });

        ipscResponses.forEach(ipscResponse -> {
            // Filters members by score request member ID
            ipscRequestHolder.getScores().forEach(scoreRequest -> {
                List<MemberRequest> memberRequests =
                        ipscRequestHolder.getMembers().stream().filter(memberRequest ->
                                        scoreRequest.getMemberId().equals(memberRequest.getMemberId()))
                                .toList();

                ipscResponse.setMembers(memberRequests);
            });
        });

        ipscResponses.forEach(ipscResponse -> {
            Integer clubId = ipscResponse.getMatch().getClubId();
            // Finds club matching ID or provides default
            ClubRequest club = ipscRequestHolder.getClubs().stream()
                    .filter(clubRequest -> clubRequest.getClubId()
                            .equals(clubId)).findFirst().orElse(null);
            ipscResponse.setClub((club != null) ? new ClubResponse(club) : new ClubResponse(clubId));
        });

        return ipscResponses;
    }

    protected void saveIpscResponse(List<IpscResponse> ipscResponses) {
    }

    /**
     * Generic method to parse XML content into a list of request objects.
     *
     * @param xmlContent The XML content to parse
     * @param clazz      The class type of the request objects
     * @param <T>        The type of request objects
     * @return List of parsed request objects
     */
    protected <T> List<T> readRequests(String xmlContent, Class<T> clazz) {
        // Returns an empty list when the content is null or blank
        if ((xmlContent == null) || xmlContent.isBlank()) {
            return new ArrayList<>();
        }

        // Parses XML content into a list of typed requests
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JavaTimeModule());
            var wrapperType = xmlMapper.getTypeFactory()
                    .constructParametricType(XmlDataWrapper.class, clazz);
            XmlDataWrapper<T> wrapper = xmlMapper.readValue(xmlContent, wrapperType);

            // Returns an empty list when the XML wrapper is invalid
            if ((wrapper == null) || (wrapper.getData() == null) || (wrapper.getData().getRow() == null)) {
                return new ArrayList<>();
            }
            return wrapper.getData().getRow();

        } catch (JsonProcessingException e) {
            log.error("Error parsing XML content: {}", e.getMessage(), e);
        }

        return new ArrayList<>();
    }
}
