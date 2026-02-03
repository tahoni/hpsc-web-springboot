package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Competitor;

import java.util.List;
import java.util.Optional;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    Optional<Competitor> findBySapsaNumber(Integer icsAlias);

    List<Competitor> findAllByFirstNameAndLastName(String firstName, String lastName);

    Optional<Competitor> findByCompetitorNumber(String icsAlias);
}
