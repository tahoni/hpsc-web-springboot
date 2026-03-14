package za.co.hpsc.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.Optional;

public interface IpscMatchStageRepository extends JpaRepository<IpscMatchStage, Long> {
    @Query("SELECT ms FROM IpscMatchStage ms " +
            "JOIN FETCH ms.matchStageCompetitors msc " +
            "WHERE ms.id = :id")
    Optional<IpscMatchStage> findByIdWithCompetitors(Long id);

    Optional<IpscMatchStage> findByMatchIdAndStageNumber(Long matchId, Integer stageNumber);
}
