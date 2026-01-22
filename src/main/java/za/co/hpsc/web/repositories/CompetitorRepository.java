package za.co.hpsc.web.repositories;

import org.springframework.data.repository.CrudRepository;
import za.co.hpsc.web.domain.Competitor;

public interface CompetitorRepository extends CrudRepository<Competitor, Long> {
}
