package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.Optional;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    Optional<IpscMatch> findByName(String name);
}
