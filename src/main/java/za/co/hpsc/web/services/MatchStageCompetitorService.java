package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.Optional;

// TODO: Javadoc
public interface MatchStageCompetitorService {
    Optional<MatchStageCompetitor> findMatchStageCompetitor(Long matchId, Long matchStageId,
                                                            Long competitorId);
}
