package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Competitor;

import java.util.Optional;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    Optional<Competitor> findByClubNumber(String clubNumber);
}
