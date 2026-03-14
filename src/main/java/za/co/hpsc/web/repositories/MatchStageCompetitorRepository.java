package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.Optional;

public interface MatchStageCompetitorRepository extends JpaRepository<MatchStageCompetitor, Long> {
    Optional<MatchStageCompetitor> findByMatchStageIdAndCompetitorId(Long matchStageId, Long competitorId);
}
