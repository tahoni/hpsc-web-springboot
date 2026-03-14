package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.MatchStageCompetitor;

import java.util.Optional;

public interface MatchStageCompetitorRepository extends JpaRepository<MatchStageCompetitor, Long> {
    @Query("SELECT msc FROM MatchStageCompetitor msc " +
            "JOIN FETCH msc.matchStage ms JOIN FETCH ms.match m JOIN FETCH ms.matchStageCompetitors mscc " +
            "WHERE msc.matchStage.id = :matchStageId AND msc.competitor.id = :competitorId")
    Optional<MatchStageCompetitor> findByMatchStageIdAndCompetitorIdWithMatchStageAndCompetitor(Long matchStageId, Long competitorId);

    @Query("SELECT DISTINCT msc FROM MatchStageCompetitor msc " +
            "JOIN FETCH msc.competitor mscc " +
            "WHERE msc.id = :id")
    Optional<MatchStageCompetitor> findByIdWithCompetitors(Long id);
}
