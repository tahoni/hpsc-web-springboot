package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.ShooterLogOverall;

import java.util.List;

public interface ShooterLogOverallRepository extends JpaRepository<ShooterLogOverall, Long> {
    List<ShooterLogOverall> findAllByShooterLogId(Long shooterLogId);

    boolean existsByCompetitorId(Long competitorId);

    // Fetch-joins the lazy shooter log and competitor read when mapping an overall to its response, so
    // they're usable outside a transaction (open-in-view is disabled).
    @Query("select slo from ShooterLogOverall slo join fetch slo.shooterLog join fetch slo.competitor "
            + "where slo.shooterLog.id = :shooterLogId order by slo.id")
    List<ShooterLogOverall> findAllByShooterLogIdWithDetails(@Param("shooterLogId") Long shooterLogId);

    @Query("select slo from ShooterLogOverall slo join fetch slo.shooterLog join fetch slo.competitor "
            + "order by slo.id")
    List<ShooterLogOverall> findAllWithDetails();
}
