package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.Optional;

/**
 * The {@code MatchStageCompetitorService} interface defines the contract for operations
 * involving relationships between competitors and match stages in the system.
 * It provides methods for searching and retrieving details about match stage competitors
 * based on specific criteria.
 */
public interface MatchStageCompetitorService {
    /**
     * Finds and retrieves a {@link MatchStageCompetitor} entity based on the provided match stage ID,
     * and competitor ID.
     *
     * @param matchStageId the unique identifier of the match stage in which the competitor is participating.
     * @param competitorId the unique identifier of the competitor associated with the match stage.
     * @return an {@code Optional} containing the {@link MatchStageCompetitor} if a matching entity
     * is found, or an empty {@code Optional} if no match is found.
     */
    Optional<MatchStageCompetitor> findMatchStageCompetitor(Long matchStageId, Long competitorId);
}
