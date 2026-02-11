package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.Optional;

public interface IpscMatchStageRepository extends JpaRepository<IpscMatchStage, Long> {
    Optional<IpscMatchStage> findByMatchIdAndStageNumber(Long matchId, Integer stageNumber);
}
