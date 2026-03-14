package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.Competitor;

import java.util.List;
import java.util.Optional;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    @Query("SELECT cc FROM Competitor cc " +
            "JOIN FETCH cc.competitorMatches cm JOIN FETCH cm.match m  " +
            "WHERE cc.id = :id")
    Optional<Competitor> findByIdWithMatchCompetitors(Long id);

    List<Competitor> findAllBySapsaNumber(Integer icsAlias);

    List<Competitor> findAllByFirstNameAndLastName(String firstName, String lastName);
}
