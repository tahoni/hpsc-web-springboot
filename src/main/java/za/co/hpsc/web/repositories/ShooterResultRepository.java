package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.ShooterResult;

import java.util.List;

public interface ShooterResultRepository extends JpaRepository<ShooterResult, Long> {
    // Fetch-joins the lazy match, competitor and match competitor read when mapping a result to its
    // response, so they're usable outside a transaction (open-in-view is disabled).
    @Query("select sr from ShooterResult sr join fetch sr.match join fetch sr.competitor "
            + "join fetch sr.matchCompetitor where sr.match.id = :matchId order by sr.id")
    List<ShooterResult> findAllByMatchIdWithDetails(@Param("matchId") Long matchId);

    @Query("select sr from ShooterResult sr join fetch sr.match join fetch sr.competitor "
            + "join fetch sr.matchCompetitor order by sr.id")
    List<ShooterResult> findAllWithDetails();
}
