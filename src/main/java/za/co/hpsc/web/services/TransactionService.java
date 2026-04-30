package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.common.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;

import java.util.Optional;

/**
 * Defines transactional persistence operations for IPSC match-related workflows.
 * <p>
 * Implementations of this interface coordinate writing match data and related
 * entities to the persistence layer, typically within a transactional boundary
 * to maintain consistency.
 * </p>
 * <p>
 * Methods return {@link Optional} wrappers to indicate that a persistence action
 * may legitimately produce no saved aggregate (for example, when an operation is
 * short-circuited by validation/business rules in the implementation).
 * </p>
 */
public interface TransactionService {

    /**
     * Persists a complete match result aggregate built from mapped DTO data.
     * <p>
     * The supplied {@link DtoMapping} is expected to contain all structures required
     * to save a match and its related graph (for example, stages, competitors, and scores).
     * </p>
     *
     * @param dtoMapping mapped input containing all data required to persist match results
     * @return an {@link Optional} containing the saved {@link MatchHolder} when persistence succeeds;
     * {@link Optional#empty()} if no aggregate is persisted
     * @throws FatalException if an unrecoverable persistence error or transaction error occurs
     */
    Optional<MatchHolder> saveMatchResults(DtoMapping dtoMapping)
            throws FatalException;

    // TODO: add Javadoc
    // TODO: add tests
    void saveMatch(MatchOnlyResultsDto matchOnlyResultsDto)
            throws FatalException;
}
