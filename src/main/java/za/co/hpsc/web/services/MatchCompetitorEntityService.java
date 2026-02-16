package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.List;
import java.util.Optional;

/**
 * The {@code MatchCompetitorEntityService} interface defines the contract related to
 * relationships between competitors and matches in the system.
 * It provides methods for searching and retrieving details about the match competitors
 * based on specific criteria.
 */
public interface MatchCompetitorEntityService {
    /**
     * Retrieves a {@link MatchCompetitor} based on the provided details.
     *
     * @param matchId      the unique identifier of the match.
     * @param competitorId the unique identifier of the competitor.
     * @return an {@code Optional} containing the {@link MatchCompetitor} if a match is found,
     * or an empty {@code Optional} if no matching record exists.
     */
    Optional<MatchCompetitor> findMatchCompetitor(Long matchId, Long competitorId);

    // TODO: Javadoc
    List<MatchCompetitor> findAllMatchCompetitors(Long matchId, String clubName);
}
