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
    // TODO: Javadoc
    Optional<MatchStageCompetitor> findMatchStageCompetitor(MatchStageDto matchStageDto, CompetitorDto competitorDto);
}
