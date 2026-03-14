package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.List;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    List<IpscMatch> findAllByName(String name);
}
