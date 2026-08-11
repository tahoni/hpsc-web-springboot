package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByName(String name);

    Optional<Club> findByAbbreviation(String abbreviation);

    Optional<Club> findByIdentifier(ClubIdentifier identifier);
}
