package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchStage;

import java.util.Optional;

public interface MatchStageRepository extends JpaRepository<MatchStage, Long> {
    Optional<MatchStage> findByMatchIdAndStageNumber(Long matchId, Integer stageNumber);
}
