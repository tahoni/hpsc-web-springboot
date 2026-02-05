package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Match;

import java.time.LocalDateTime;
import java.util.Optional;

// TODO: Javadoc
public interface MatchService {
    Optional<Match> findMatch(String name, LocalDateTime scheduledDateTime);
}
