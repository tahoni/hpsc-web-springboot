package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;

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
     * @param dtoMapping an instance of {@link DtoMapping} containing all the relevant
     *                   information about the match, including competitors, scores, stages,
     *                   and related metadata.
     * @return an {@link IpscMatch} containing the saved match object.
     * @throws FatalException if an unrecoverable error occurs during the operation.
     */
    Optional<IpscMatch> saveMatchResults(DtoMapping dtoMapping)
            throws FatalException;
}
