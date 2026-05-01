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
    // TODO: add Javadoc
    // TODO: add tests
    Optional<MatchCompetitor> findMatchCompetitorById(Long matchCompetitorId);


    /**
     * Retrieves a list of {@link MatchCompetitor} entities matching the specified match
     * and competitor identifiers.
     *
     * <p>
     * Either or both parameters may be {@code null}. When a parameter is {@code null},
     * it is not used as a filter criterion, and all records matching the remaining
     * non-null parameters are returned.
     * </p>
     *
     * @param matchId      the unique identifier of the match to filter by, or {@code null}
     *                     to include all matches.
     * @param competitorId the unique identifier of the competitor to filter by, or
     *                     {@code null} to include all competitors.
     * @return a {@link List} of {@link MatchCompetitor} entities matching the given
     * criteria; never {@code null}, but may be empty if no matches are found.
     */
    List<MatchCompetitor> findMatchCompetitors(Long matchId, Long competitorId);
}
