package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Club;

public interface ClubRepository extends JpaRepository<Club, Long> {
}
