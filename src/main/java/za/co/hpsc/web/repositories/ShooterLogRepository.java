package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.ShooterLog;

import java.util.List;
import java.util.Optional;

public interface ShooterLogRepository extends JpaRepository<ShooterLog, Long> {
    boolean existsByMatchesId(Long matchId);

    // Fetch-joins the lazy matches read when mapping a shooter log to its response, so they're usable
    // outside a transaction (open-in-view is disabled).
    @Query("select distinct sl from ShooterLog sl left join fetch sl.matches where sl.id = :id")
    Optional<ShooterLog> findByIdWithMatches(@Param("id") Long id);

    @Query("select distinct sl from ShooterLog sl left join fetch sl.matches order by sl.startDate, sl.id")
    List<ShooterLog> findAllWithMatches();
}
