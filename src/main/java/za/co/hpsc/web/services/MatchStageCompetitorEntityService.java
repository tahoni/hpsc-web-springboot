package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.List;

/**
 * The {@code MatchStageCompetitorEntityService} interface defines the contract for operations
 * involving relationships between competitors and match stages in the system.
 * It provides methods for searching and retrieving details about match stage competitors
 * based on specific criteria.
 */
public interface MatchStageCompetitorEntityService {
    // TODO: Javadoc
    List<MatchStageCompetitor> findMatchStageCompetitors(Long matchStageId, Long competitorId);
}
