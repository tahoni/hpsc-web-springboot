package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.util.List;
import java.util.Optional;

// TODO: Javadoc
public interface IpscMatchService {
    void insertMatch(MatchResponse matchResponse)
            throws FatalException;

    void updateMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException;

    void modifyMatch(Long matchId, MatchResponse matchResponse) throws FatalException;

    List<MatchResponse> getMatches(MatchSearchRequest matchSearchRequest);

    Optional<MatchResponse> getMatch(Long matchId);
}
