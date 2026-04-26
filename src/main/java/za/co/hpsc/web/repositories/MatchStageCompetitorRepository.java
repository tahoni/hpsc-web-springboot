package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.List;

public interface MatchStageCompetitorRepository extends JpaRepository<MatchStageCompetitor, Long> {
    List<MatchStageCompetitor> findAllByMatchStageIdAndCompetitorId(Long matchStageId, Long competitorId);
}
