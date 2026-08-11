package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

public interface ShooterLogRepository extends JpaRepository<ShooterLog, Long> {
    List<ShooterLog> findAllByCompetitorIdAndFirearmType(Long competitorId, FirearmType firearmType);
}
