package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.Optional;

/**
 * The {@code MatchCompetitorEntityService} interface defines the contract related to
 * relationships between competitors and matches in the system.
 * It provides methods for searching and retrieving details about the match competitors
 * based on specific criteria.
 */
public interface MatchCompetitorEntityService {
    /**
     * Searches for a {@link MatchCompetitor} by its match ID and competitor ID.
     *
     * @param matchId      the unique identifier of the match.
     * @param competitorId the unique identifier of the competitor.
     * @return an {@code Optional} containing the {@link MatchCompetitor} if a match is found,
     * or an empty {@code Optional} if no matching record exists.
     */
    Optional<MatchCompetitor> findMatchCompetitor(Long matchId, Long competitorId);
}
