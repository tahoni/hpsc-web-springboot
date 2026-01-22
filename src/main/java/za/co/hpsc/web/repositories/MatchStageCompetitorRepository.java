package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchStageCompetitor;

public interface MatchStageCompetitorRepository extends JpaRepository<MatchStageCompetitor, Long> {
}
