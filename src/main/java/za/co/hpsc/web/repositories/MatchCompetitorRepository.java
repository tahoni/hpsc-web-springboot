package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
    List<MatchCompetitor> findAllByCompetitorIdAndMatchId(Long competitorId, Long matchId);

    List<MatchCompetitor> findAllByMatchIdAndFirearmType(Long matchId, FirearmType firearmType);

    List<MatchCompetitor> findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse(Long competitorId, FirearmType firearmType);
}
