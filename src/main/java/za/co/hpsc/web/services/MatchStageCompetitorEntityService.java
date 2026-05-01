package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.List;
import java.util.Optional;

/**
 * The {@code MatchStageCompetitorEntityService} interface defines the contract for operations
 * involving relationships between competitors and match stages in the system.
 * It provides methods for searching and retrieving details about match stage competitors
 * based on specific criteria.
 */
public interface MatchStageCompetitorEntityService {

    // TODO: add Javadoc
    // TODO: add tests
    Optional<MatchStageCompetitor> findMatchStageCompetitorById(Long matchStageCompetitorId);

    /**
     * Retrieves a list of {@link MatchStageCompetitor} entities matching the specified match
     * stage and competitor identifiers.
     *
     * <p>
     * Either or both parameters may be {@code null}. When a parameter is {@code null},
     * it is not used as a filter criterion, and all records matching the remaining
     * non-null parameters are returned.
     * </p>
     *
     * @param matchStageId the unique identifier of the match stage to filter by, or
     *                     {@code null} to include all match stages.
     * @param competitorId the unique identifier of the competitor to filter by, or
     *                     {@code null} to include all competitors.
     * @return a {@link List} of {@link MatchStageCompetitor} entities matching the given
     * criteria; never {@code null}, but may be empty if no matches are found.
     */
    List<MatchStageCompetitor> findMatchStageCompetitors(Long matchStageId, Long competitorId);
}
