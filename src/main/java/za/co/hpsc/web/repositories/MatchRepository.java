package za.co.hpsc.web.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.IpscMatch;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<IpscMatch, Long> {
    List<IpscMatch> findAllByScheduledDate(@NotNull LocalDate matchDate);
}
