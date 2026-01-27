package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.MatchCompetitor;

public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitor, Long> {
}
