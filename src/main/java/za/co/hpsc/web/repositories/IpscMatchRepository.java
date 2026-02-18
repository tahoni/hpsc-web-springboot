package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.Optional;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    @Query("FROM IpscMatch m JOIN FETCH m.club JOIN FETCH m.matchStages " +
            "WHERE m.id = :id")
    Optional<IpscMatch> findByIdWithClub(Long id);

    @Query("FROM IpscMatch m JOIN FETCH m.club JOIN FETCH m.matchStages " +
            "WHERE m.name = :name")
    Optional<IpscMatch> findByNameWithClub(String name);
}
