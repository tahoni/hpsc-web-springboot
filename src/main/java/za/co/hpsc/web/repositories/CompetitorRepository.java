package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Competitor;

import java.util.List;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    List<Competitor> findAllBySapsaNumber(Integer icsAlias);

    List<Competitor> findAllByFirstNameAndLastName(String firstName, String lastName);
}
