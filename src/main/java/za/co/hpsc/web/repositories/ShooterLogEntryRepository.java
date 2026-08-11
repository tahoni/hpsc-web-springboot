package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLogEntry;

import java.util.List;

public interface ShooterLogEntryRepository extends JpaRepository<ShooterLogEntry, Long> {
    List<ShooterLogEntry> findAllByShooterLogId(Long shooterLogId);
}
