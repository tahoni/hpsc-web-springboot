package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.Optional;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    @Query("SELECT m FROM IpscMatch m " +
            "JOIN FETCH m.matchStages ms " +
            "WHERE m.id = :id")
    Optional<IpscMatch> findByIdWithStages(Long id);

    @Query("SELECT m FROM IpscMatch m " +
            "JOIN FETCH m.matchCompetitors mcc " +
            "WHERE m.id = :id")
    Optional<IpscMatch> findByIdWithCompetitors(Long id);

    @Query("SELECT m FROM IpscMatch m " +
            "LEFT JOIN FETCH m.club c JOIN FETCH m.matchStages " +
            "WHERE m.name = :name")
    Optional<IpscMatch> findByNameWithClubAndStages(String name);

}
