package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLogCompetitor;

import java.util.List;

public interface ShooterLogCompetitorRepository extends JpaRepository<ShooterLogCompetitor, Long> {
    List<ShooterLogCompetitor> findAllByShooterLogId(Long shooterLogId);
}
