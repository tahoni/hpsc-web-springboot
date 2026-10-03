package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;
import java.util.Optional;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
    List<MatchCompetitor> findAllByCompetitorIdAndMatchId(Long competitorId, Long matchId);

    List<MatchCompetitor> findAllByMatchIdAndFirearmType(Long matchId, FirearmType firearmType);

    List<MatchCompetitor> findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse(Long competitorId, FirearmType firearmType);

    Optional<MatchCompetitor> findByCompetitorIdAndMatchIdAndFirearmType(Long competitorId, Long matchId,
                                                                         FirearmType firearmType);

    // Fetch-joins the lazy competitor and match read when mapping a match competitor to its
    // response, so they're usable outside a transaction (open-in-view is disabled).
    @Query("select mc from MatchCompetitor mc join fetch mc.competitor join fetch mc.match where mc.id = :id")
    Optional<MatchCompetitor> findByIdWithCompetitorAndMatch(@Param("id") Long id);

    @Query("select mc from MatchCompetitor mc join fetch mc.competitor join fetch mc.match")
    List<MatchCompetitor> findAllWithCompetitorAndMatch();

    boolean existsByCompetitorId(Long competitorId);

    boolean existsByMatchId(Long matchId);
}
