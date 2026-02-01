package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.MatchResultsService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MatchResultsServiceImpl implements MatchResultsService {
    @Override
    public IpscResponseHolder calculateMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException {
        if (ipscRequestHolder == null) {
            log.error("IPSC request holder is null.");
            throw new ValidationException("IPSC request holder can not be null");
        }

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

        return new IpscResponseHolder(ipscResponses);
    }

    @Override
    public void saveMatchResults(IpscResponseHolder ipscResponseHolder) {

    }
}
