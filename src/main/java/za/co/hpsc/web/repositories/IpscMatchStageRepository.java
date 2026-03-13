package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.Optional;

public interface IpscMatchStageRepository extends JpaRepository<IpscMatchStage, Long> {
    @Query("SELECT ms FROM IpscMatchStage ms " +
            "JOIN FETCH ms.matchStageCompetitors mscc " +
            "WHERE ms.id = :id")
    Optional<IpscMatchStage> findByIdWithCompetitorAndMatch(Long id);

    @Query("SELECT ms FROM IpscMatchStage ms " +
            "JOIN FETCH ms.match m JOIN FETCH ms.matchStageCompetitors cc " +
            "WHERE m.id = :matchId AND ms.stageNumber = :stageNumber")
    Optional<IpscMatchStage> findByMatchIdAndStageNumberWithMatch(Long matchId, Integer stageNumber);
}
