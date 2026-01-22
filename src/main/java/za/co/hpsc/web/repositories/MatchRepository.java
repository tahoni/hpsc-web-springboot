package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
