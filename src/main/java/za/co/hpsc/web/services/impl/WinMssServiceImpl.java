package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDtoHolder;
import za.co.hpsc.web.models.ipsc.records.IpscMatchResponseHolder;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.MatchResultService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.WinMssService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WinMssServiceImpl implements WinMssService {

    protected final IpscMatchService ipscMatchService;
    protected final MatchResultService matchResultService;
    protected final TransactionService transactionService;

    public WinMssServiceImpl(IpscMatchService ipscMatchService, MatchResultService matchResultService,
                             TransactionService transactionService) {
        this.ipscMatchService = ipscMatchService;
        this.matchResultService = matchResultService;
        this.transactionService = transactionService;
    }

    @Override
    public IpscMatchResponseHolder importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException {

        importWinMssCabFileContent(cabFileContent);
        return null;
    }

    /**
     * Imports and processes the content of a WinMSS.cab file.
     *
     * @param cabFileContent the content of the WinMSS.cab file to be imported. It must be provided
     *                       as a non-null string containing the data in a valid JSON format.
     * @return a {@link IpscMatchResponseHolder} object containing the parsed match results
     * extracted from the CAB file.
     * @throws ValidationException if the provided CAB file content fails validation, indicating
     *                             that the input data is incomplete, malformed, or otherwise invalid.
     * @throws FatalException      if a critical error occurs during the processing of the CAB file,
     *                             rendering the operation unable to complete.
     */
    protected MatchResultsDtoHolder importWinMssCabFileContent(String cabFileContent)
            throws ValidationException, FatalException {

        if ((cabFileContent == null) || (cabFileContent.isBlank())) {
            log.error("The provided cab file is null or empty.");
            throw new ValidationException("The provided CAB file can not be null or empty.");
        }

        // Imports WinMSS cab file content
        IpscRequestHolder ipscRequestHolder = readIpscRequests(cabFileContent);
        if (ipscRequestHolder == null) {
            log.error("IPSC request holder is null.");
            throw new ValidationException("IPSC request holder can not be null.");
        }

        // Maps the results of each match
        IpscResponseHolder ipscResponseHolder = ipscMatchService.mapMatchResults(ipscRequestHolder);
        if (ipscResponseHolder == null) {
            log.error("IPSC response holder is null.");
            throw new ValidationException("IPSC response holder can not be null.");
        }

        // Persists the match results
        List<MatchResultsDto> matchResultsList = new ArrayList<>();
        ipscResponseHolder.getIpscList().forEach(ipscResponse -> {
            Optional<MatchResultsDto> matchResults = matchResultService.initMatchResults(ipscResponse);
            matchResults.ifPresent(transactionService::saveMatchResults);
            matchResults.ifPresent(matchResultsList::add);
        });

        return new MatchResultsDtoHolder(matchResultsList);
    }

    /**
     * Deserializes the provided CAB file content into an {@link IpscRequest} object
     * and populates an {@link IpscRequestHolder} with various request data, such as
     * clubs, matches, tags, members, enrolled members, squads, and scores.
     *
     * @param cabFileContent The CAB file content as a string to be deserialized.
     * @return An {@link IpscRequest} object containing the deserialized request data.
     * @throws ValidationException If the provided CAB file content is invalid or missing.
     * @throws FatalException      If an I/O error occurs while reading the CAB file.
     */
    protected IpscRequestHolder readIpscRequests(@NotNull @NotBlank String cabFileContent)
            throws ValidationException, FatalException {

        try {
            // Deserializes CAB file content into typed IPSC requests
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            IpscRequest ipscRequest = objectMapper.readValue(cabFileContent, IpscRequest.class);

            // Populates an IPSC request holder with the deserialized requests
            IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
            ipscRequestHolder.setClubs(readRequests(ipscRequest.getClub(),
                    ClubRequest.class));
            ipscRequestHolder.setMatches(readRequests(ipscRequest.getMatch(),
                    MatchRequest.class));
            ipscRequestHolder.setStages(readRequests(ipscRequest.getStage(),
                    StageRequest.class));
            ipscRequestHolder.setTags(readRequests(ipscRequest.getTag(),
                    TagRequest.class));
            ipscRequestHolder.setMembers(readRequests(ipscRequest.getMember(),
                    MemberRequest.class));
            ipscRequestHolder.setClassifications(readRequests(ipscRequest.getClassify(),
                    ClassificationRequest.class));
            ipscRequestHolder.setEnrolledMembers(readRequests(ipscRequest.getEnrolled(),
                    EnrolledRequest.class));
            ipscRequestHolder.setSquads(readRequests(ipscRequest.getSquad(),
                    SquadRequest.class));
            ipscRequestHolder.setTeams(readRequests(ipscRequest.getTeam(),
                    TeamRequest.class));
            ipscRequestHolder.setScores(readRequests(ipscRequest.getScore(),
                    ScoreRequest.class));

            return ipscRequestHolder;

        } catch (ValidationException e) {
            throw e;
        } catch (MismatchedInputException | IllegalArgumentException e) {
            log.error("Error parsing JSON data: {}", e.getMessage(), e);
            throw new ValidationException("Invalid JSON data format.", e);
        } catch (IOException e) {
            log.error("Error reading JSON data: {}", e.getMessage(), e);
            throw new FatalException("Error reading JSON data.", e);
        }
    }

    /**
     * Generic method to parse XML content into a list of request objects.
     *
     * @param xmlContent The XML content to parse.
     * @param clazz      The class type of the request objects.
     * @param <T>        The type of request objects.
     * @return List of parsed request objects.
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
            if ((wrapper == null) || (wrapper.getData() == null) ||
                    (wrapper.getData().getRow() == null)) {
                return new ArrayList<>();
            }
            return wrapper.getData().getRow();

        } catch (JsonProcessingException e) {
            log.error("Error parsing XML content: {}", e.getMessage(), e);
            throw new ValidationException("Invalid XML data format.", e);
        }
    }
}
