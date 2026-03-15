package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.Optional;

/**
 * The {@code MatchStageService} interface defines the contract for operations related to
 * match stage information within the system.
 * It provides methods for searching and retrieving details about match stages based on
 * specific criteria.
 */
public interface MatchStageEntityService {
    /**
     * Searches for a specific match stage within a match based on the provided match ID
     * and stage number.
     *
     * @param matchId     the unique identifier of the match to which the stage belongs.
     * @param stageNumber the number representing the stage within the match.
     * @return an {@code Optional} containing the {@link IpscMatchStage} if a matching stage is found,
     * or an empty {@code Optional} if no matching stage is found.
     */
    Optional<IpscMatchStage> findMatchStage(Long matchId, Integer stageNumber);
}
