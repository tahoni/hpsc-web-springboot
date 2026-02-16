package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;

import java.util.List;
import java.util.Optional;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
    Optional<MatchCompetitor> findByCompetitorIdAndMatchId(Long competitorId, Long matchId);

    List<MatchCompetitor> findAllByMatchIdAndClubName(Long matchId, ClubIdentifier clubName);
}
