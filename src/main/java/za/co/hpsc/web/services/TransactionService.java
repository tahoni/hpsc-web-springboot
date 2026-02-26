package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;

import java.util.Optional;

/**
 * Provides operations for managing and persisting transaction-related data within the system.
 *
 * <p>
 * This interface is responsible for defining the contract for saving match results
 * and other transaction-based activities that interact with the underlying persistence layer.
 * </p>
 */
public interface TransactionService {
    /**
     * Saves the results of a match into the system.
     *
     * @param matchResults an instance of {@code MatchResultsDto} containing all the relevant
     *                     information about the match, including competitors, scores, stages,
     *                     and related metadata.
     * @return an {@code Optional<IpscMatch>} containing the saved match object if the operation
     * is successful, or an empty {@code Optional} if the save operation fails.
     * @throws FatalException if an unrecoverable error occurs during the operation.
     */
    Optional<IpscMatch> saveMatchResults(DtoToEntityMapping matchResults)
            throws FatalException;
}
