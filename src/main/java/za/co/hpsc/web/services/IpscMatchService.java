package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;

import java.util.Optional;

// TODO: add Javadoc
// TODO: add tests
public interface IpscMatchService {

    Optional<MatchOnlyResponse> insertMatch(MatchOnlyRequest matchOnlyRequest)
            throws FatalException;

    Optional<MatchOnlyResponse> updateMatch(Long matchId, MatchOnlyRequest MatchWithStagesResponse)
            throws FatalException;

    Optional<MatchOnlyResponse> modifyMatch(Long matchId, MatchOnlyRequest matchOnlyRequest)
            throws FatalException;

    Optional<MatchOnlyResponse> getMatch(Long matchId);
}