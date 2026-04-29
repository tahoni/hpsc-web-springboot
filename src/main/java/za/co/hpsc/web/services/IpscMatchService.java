package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.util.List;

public interface IpscMatchService {
    boolean insertMatch(MatchResponse matchResponse)
            throws FatalException;

    boolean updateMatch(String matchId, MatchResponse matchResponse)
            throws FatalException;

    boolean modifyMatch(String matchId, MatchResponse matchResponse) throws FatalException;

    List<MatchResponse> getMatches(MatchSearchRequest matchSearchRequest);

    MatchResponse getMatch(Integer matchId);
}
