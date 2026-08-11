package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.List;

public interface IpscMatchStageRepository extends JpaRepository<IpscMatchStage, Long> {
    List<IpscMatchStage> findAllByMatchIdOrderByStageNumber(Long matchId);
}
