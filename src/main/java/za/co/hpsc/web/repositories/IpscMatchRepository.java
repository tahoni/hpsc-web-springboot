package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.List;
import java.util.Optional;

public interface IpscMatchRepository extends JpaRepository<IpscMatch, Long> {
    List<IpscMatch> findAllByClubId(Long clubId);

    // Fetch-joins the lazy club read when mapping a match to its response, so it's usable
    // outside a transaction (open-in-view is disabled).
    @Query("select m from IpscMatch m left join fetch m.club where m.id = :id")
    Optional<IpscMatch> findByIdWithClub(@Param("id") Long id);

    @Query("select m from IpscMatch m left join fetch m.club")
    List<IpscMatch> findAllWithClub();
}
