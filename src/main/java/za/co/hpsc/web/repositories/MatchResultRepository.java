package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.MatchResult;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    // Fetch-joins the lazy match, competitor and match competitor read when mapping a result to its
    // response, so they're usable outside a transaction (open-in-view is disabled).
    @Query("select mr from MatchResult mr join fetch mr.match join fetch mr.competitor "
            + "join fetch mr.matchCompetitor where mr.match.id = :matchId order by mr.id")
    List<MatchResult> findAllByMatchIdWithDetails(@Param("matchId") Long matchId);

    @Query("select mr from MatchResult mr join fetch mr.match join fetch mr.competitor "
            + "join fetch mr.matchCompetitor order by mr.id")
    List<MatchResult> findAllWithDetails();
}
