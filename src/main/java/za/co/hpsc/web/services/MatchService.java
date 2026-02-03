package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.util.Optional;

// TODO: Javadoc
public interface MatchService {
    Optional<Match> findMatch(MatchResponse matchResponse);
}
