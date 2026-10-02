package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLog;

public interface ShooterLogRepository extends JpaRepository<ShooterLog, Long> {
    boolean existsByMatchesId(Long matchId);
}
