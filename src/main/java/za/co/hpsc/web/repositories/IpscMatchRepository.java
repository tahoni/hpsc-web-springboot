package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.Optional;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    @Query("SELECT m FROM IpscMatch m JOIN FETCH m.club c WHERE m.name = :name")
    Optional<IpscMatch> findByNameWithClub(@Param("name") String name);

    @Query("SELECT m FROM IpscMatch m JOIN FETCH m.club c WHERE m.id = :id")
    Optional<IpscMatch> findByIdWithClubStages(@Param("id") Long id);
}
