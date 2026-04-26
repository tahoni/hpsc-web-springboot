package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.List;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
    List<MatchCompetitor> findAllByCompetitorIdAndMatchId(Long competitorId, Long matchId);
}
