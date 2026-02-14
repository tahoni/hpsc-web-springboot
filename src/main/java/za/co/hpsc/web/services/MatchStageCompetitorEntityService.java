package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;

import java.util.Optional;

/**
 * The {@code MatchStageCompetitorEntityService} interface defines the contract for operations
 * involving relationships between competitors and match stages in the system.
 * It provides methods for searching and retrieving details about match stage competitors
 * based on specific criteria.
 */
public interface MatchStageCompetitorEntityService {
    /**
     * Finds and retrieves a {@link MatchStageCompetitor} entity based on the provided match stage ID,
     * and competitor ID.
     *
     * @param matchStageDto
     * @param competitorDto
     * @return an {@code Optional} containing the {@link MatchStageCompetitor} if a matching entity
     * is found, or an empty {@code Optional} if no match is found.
     */
    Optional<MatchStageCompetitor> findMatchStageCompetitor(MatchStageDto matchStageDto, CompetitorDto competitorDto);
}
