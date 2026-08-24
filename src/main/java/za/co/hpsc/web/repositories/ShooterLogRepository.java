package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;

import java.util.List;

public interface ShooterLogRepository extends JpaRepository<ShooterLog, Long> {
    List<ShooterLog> findAllByCompetitorIdAndFirearmTypeAndPowerFactor(
            Long competitorId, FirearmType firearmType, PowerFactor powerFactor);
}
