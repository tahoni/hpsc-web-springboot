package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;

import java.util.Optional;

/**
 * The {@code TransactionService} interface defines operations for managing transactional
 * processes within the system. It encapsulates methods for handling match results
 * and logs, ensuring proper coordination and persistence of relevant data.
 */
public interface TransactionService {
    /**
     * Persists the results of a match into the system's database.
     *
     * <p>
     * This method processes the provided {@code MatchResultsDto}, which contains comprehensive
     * information about the match, including its stages, competitors, and associated club.
     * The data is then stored in the persistence layer while ensuring validation and integrity.
     * If successful, the stored representation of the match is returned as an {@code Optional<IpscMatch>}.
     * </p>
     *
     * @param matchResults the {@link MatchResultsDto} containing the match details and related data
     *                     to be saved into the system. Must not be null and should include valid
     *                     match, competitor, and stage information.
     * @return an {@code Optional<IpscMatch>} containing the saved match entity if the operation is
     * successful, or an empty {@code Optional} if the save operation fails.
     */
    Optional<IpscMatch> saveMatchResults(MatchResultsDto matchResults);

    void saveMatchLogs();
}
