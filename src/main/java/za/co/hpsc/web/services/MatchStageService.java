package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchStage;

import java.util.Optional;

/**
 * The {@code MatchStageService} interface defines the contract for operations related to
 * match stage information within the system.
 * It provides methods for searching and retrieving details about match stages based on
 * specific criteria.
 */
public interface MatchStageService {
    /**
     * Searches for a specific match stage within a match based on the provided match ID
     * and stage number.
     *
     * @param matchId     the unique identifier of the match to which the stage belongs.
     *                    Must not be null.
     * @param stageNumber the number representing the stage within the match.
     *                    Must not be null.
     * @return an {@code Optional} containing the {@link MatchStage} if a matching stage is found,
     * or an empty {@code Optional} if no matching stage is found.
     */
    Optional<MatchStage> findMatchStage(Long matchId, Integer stageNumber);
}
