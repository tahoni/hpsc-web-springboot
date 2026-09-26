package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.Competitor;

import java.util.List;
import java.util.Optional;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    Optional<Competitor> findByClubNumber(String clubNumber);

    // Fetch-joins the lazy homeClub and emailAddresses read when mapping a competitor to its
    // response, so they're usable outside a transaction (open-in-view is disabled).
    @Query("select c from Competitor c left join fetch c.homeClub left join fetch c.emailAddresses where c.id = :id")
    Optional<Competitor> findByIdWithHomeClubAndEmailAddresses(@Param("id") Long id);

    @Query("select c from Competitor c left join fetch c.homeClub left join fetch c.emailAddresses")
    List<Competitor> findAllWithHomeClubAndEmailAddresses();
}
