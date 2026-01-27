package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchStage;

public interface MatchStageRepository extends JpaRepository<MatchStage, Long> {
}
