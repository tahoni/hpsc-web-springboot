package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;

/**
 * The {@code TransactionService} interface defines operations for managing transactional
 * processes within the system. It encapsulates methods for handling match results
 * and logs, ensuring proper coordination and persistence of relevant data.
 */
public interface TransactionService {
    /**
     * Persists the provided match results into the system.
     *
     * @param matchResults a {@link MatchResultsDto} object containing the results of a match,
     *                     including associated competitors, stages, and other related details.
     *                     Must not be null.
     */
    void saveMatchResults(MatchResultsDto matchResults);

    void saveMatchLogs();
}
