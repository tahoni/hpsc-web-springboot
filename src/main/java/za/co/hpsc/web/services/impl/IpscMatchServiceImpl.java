package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    protected final TransactionService transactionService;

    public IpscMatchServiceImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException {

        // Validate input
        if (ipscRequestHolder == null) {
            log.error("IPSC request holder is null while mapping match results.");
            throw new ValidationException("IPSC request holder can not be null");
        }

        List<IpscResponse> ipscResponses = new ArrayList<>();
        // Maps IPSC requests to responses by match ID
        ipscRequestHolder.getMatches().forEach(match ->
                ipscResponses.add(createBasicMatch(ipscRequestHolder, match)));

        // Add members to each match
        ipscResponses.forEach(ipscResponse -> addMembersToMatch(ipscResponse, ipscRequestHolder));
        // Add a club to each match
        ipscResponses.forEach(ipscResponse -> addClubToMatch(ipscResponse, ipscRequestHolder));

        return new IpscResponseHolder(ipscResponses);
    }

    /**
     * Creates a basic match representation by filtering relevant data from the provided
     * request holder based on the given match details.
     *
     * @param ipscRequestHolder the container for all IPSC-related input requests, including
     *                          stages, tags, scores, enrolled members, and more.
     * @param match             the match data used to identify and extract the corresponding subsets
     *                          of stages, tags, scores, and enrolled members.
     * @return an {@link IpscResponse} object containing the filtered data associated with the
     * provided match.
     */
    protected IpscResponse createBasicMatch(IpscRequestHolder ipscRequestHolder, MatchRequest match) {
        // Create the match response
        MatchResponse matchResponse = new MatchResponse(match);
        Integer matchId = matchResponse.getMatchId();

        // Add all tags to the response
        List<TagRequest> tagRequests = ipscRequestHolder.getTags();

        // Add stages to the response, filtered by match ID
        List<StageRequest> stageRequests = ipscRequestHolder.getStages().stream()
                .filter(stage -> stage.getMatchId()
                        .equals(matchId)).toList();
        // Add enrolled members to the response, filtered by match ID
        List<EnrolledRequest> enrolledRequests = ipscRequestHolder.getEnrolledMembers().stream()
                .filter(enrolled -> enrolled.getMatchId()
                        .equals(matchId)).toList();
        // Add scores to the response, filtered by match ID
        List<ScoreRequest> scoreRequests = ipscRequestHolder.getScores().stream()
                .filter(score -> score.getMatchId()
                        .equals(matchId)).toList();

        return new IpscResponse(tagRequests, matchResponse, stageRequests, enrolledRequests, scoreRequests);
    }

    /**
     * Filters and assigns members with corresponding scores to the given IPSC response.
     *
     * <p>
     * The method iterates through the list of scores in the request holder, filters members
     * matching the score's member ID, and sets these members in the provided IPSC response.
     * </p>
     *
     * @param ipscResponse      the IPSC response object to which the filtered members
     *                          will be assigned.
     * @param ipscRequestHolder the request holder containing scores and members to be
     *                          filtered and matched.
     */
    protected void addMembersToMatch(IpscResponse ipscResponse, IpscRequestHolder ipscRequestHolder) {
        // Filters members by members with scores
        ipscRequestHolder.getScores().forEach(scoreRequest -> {
            List<MemberRequest> memberRequests = ipscRequestHolder.getMembers().stream()
                    .filter(memberRequest ->
                            scoreRequest.getMemberId().equals(memberRequest.getMemberId()))
                    .toList();

            // Sets members on the response
            ipscResponse.setMembers(memberRequests);
        });
    }

    /**
     * Associates a club with the given match by matching the club ID from the provided
     * IPSC response with the list of clubs in the request holder.
     *
     * <p>
     * If a matching club is found, it is set on the response. Otherwise, a default
     * club response is created using the club ID from the match.
     * </p>
     *
     * @param ipscResponse      the IPSC response containing the match details, including the club ID
     *                          to match against the provided club data.
     * @param ipscRequestHolder the request holder that contains the list of clubs to be searched
     *                          for a match.
     */
    protected void addClubToMatch(IpscResponse ipscResponse, IpscRequestHolder ipscRequestHolder) {
        Integer clubId = ipscResponse.getMatch().getClubId();
        // Finds club matching ID or provides default
        ClubRequest club = ipscRequestHolder.getClubs().stream()
                .filter(clubRequest -> clubRequest.getClubId()
                        .equals(clubId)).findFirst().orElse(null);
        ipscResponse.setClub((club != null) ? new ClubResponse(club) : new ClubResponse(clubId));
    }
}
