package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.ShooterLogOverall;

import java.util.List;

public interface ShooterLogOverallRepository extends JpaRepository<ShooterLogOverall, Long> {
    List<ShooterLogOverall> findAllByShooterLogId(Long shooterLogId);

    boolean existsByCompetitorId(Long competitorId);

    boolean existsByShooterLogId(Long shooterLogId);

    // Fetch-joins the lazy shooter log and competitor read when mapping an overall to its response, so
    // they're usable outside a transaction (open-in-view is disabled).
    // Also fetches the matches the overall was calculated from, so they can be compared outside a transaction.
    @Query("select distinct slo from ShooterLogOverall slo join fetch slo.shooterLog join fetch slo.competitor "
            + "left join fetch slo.matches where slo.shooterLog.id = :shooterLogId order by slo.id")
    List<ShooterLogOverall> findAllByShooterLogIdWithDetails(@Param("shooterLogId") Long shooterLogId);

    @Query("select distinct slo from ShooterLogOverall slo join fetch slo.shooterLog join fetch slo.competitor "
            + "left join fetch slo.matches order by slo.id")
    List<ShooterLogOverall> findAllWithDetails();

    // The shooter logs that have an overall result which was never calculated, or was marked for recalculation.
    @Query("select distinct slo.shooterLog.id from ShooterLogOverall slo where slo.dateCalculated is null "
            + "order by slo.shooterLog.id")
    List<Long> findShooterLogIdsWithoutDateCalculated();
}
