package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Competitor;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
}
