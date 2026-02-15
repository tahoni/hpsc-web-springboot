package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.ClubMatch;

import java.util.List;

public interface ClubMatchRepository extends JpaRepository<ClubMatch, Long> {
    List<ClubMatch> findAllByClub(Club clubEntity);
}
