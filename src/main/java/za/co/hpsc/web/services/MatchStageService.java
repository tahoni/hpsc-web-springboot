package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStage;

import java.util.Optional;

// TODO: Javadoc
public interface MatchStageService {
    Optional<MatchStage> findMatchStage(Long matchId, Integer stageNumber);
}
