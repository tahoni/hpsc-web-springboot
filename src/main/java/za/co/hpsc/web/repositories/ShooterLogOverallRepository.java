package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLogOverall;

import java.util.List;

public interface ShooterLogOverallRepository extends JpaRepository<ShooterLogOverall, Long> {
    List<ShooterLogOverall> findAllByShooterLogId(Long shooterLogId);

    boolean existsByCompetitorId(Long competitorId);
}
