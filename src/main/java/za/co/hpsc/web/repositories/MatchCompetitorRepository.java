package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.Optional;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
    @Query("SELECT DISTINCT mc FROM MatchCompetitor mc " +
            "JOIN FETCH mc.competitor cc JOIN FETCH mc.match m " +
            "WHERE mc.id = :id")
    Optional<MatchCompetitor> findByIdWithCompetitorAndMatch(Long id);

    @Query("SELECT DISTINCT mc FROM MatchCompetitor mc " +
            "JOIN FETCH mc.competitor cc JOIN FETCH mc.match m " +
            "WHERE mc.competitor.id = :competitorId AND mc.match.id = :matchId")
    Optional<MatchCompetitor> findByCompetitorIdAndMatchIdWithCompetitorAndMatch(Long competitorId, Long matchId);
}
