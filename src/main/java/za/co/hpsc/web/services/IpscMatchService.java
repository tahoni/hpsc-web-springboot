package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.util.Optional;

/**
 * Service contract for IPSC matches CRUD-style operations and retrieval queries.
 * <p>
 * Implementations of this interface are responsible for creating, updating, and
 * retrieving match records represented by {@link MatchResponse} DTOs, and for
 * converting unrecoverable processing failures into {@link FatalException}.
 * </p>
 */
public interface IpscMatchService {

    /**
     * Persists a new match using the supplied payload.
     *
     * @param matchResponse the match data to insert
     * @return an {@link Optional} containing the persisted {@link MatchResponse} when successful;
     * {@link Optional#empty()} if no result is produced by the implementation
     * @throws FatalException if insertion fails due to a non-recoverable error
     */
    Optional<MatchResponse> insertMatch(MatchResponse matchResponse)
            throws FatalException;

    /**
     * Fully updates an existing match identified by {@code matchId}.
     * <p>
     * Full update semantics typically treat omitted fields as replacement values,
     * depending on implementation rules.
     * </p>
     *
     * @param matchId       the identifier of the match to update
     * @param matchResponse the new match state to apply
     * @return an {@link Optional} containing the updated {@link MatchResponse} when successful;
     * {@link Optional#empty()} if the target is not updated or no result is produced
     * @throws FatalException if the update fails due to a non-recoverable error
     */
    Optional<MatchResponse> updateMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException;

    /**
     * Partially updates an existing match identified by {@code matchId}.
     * <p>
     * Partial update semantics typically apply only provided fields while retaining
     * existing values for omitted fields.
     * </p>
     *
     * @param matchId       the identifier of the match to modify
     * @param matchResponse the partial match data to apply
     * @return an {@link Optional} containing the modified {@link MatchResponse} when successful;
     * {@link Optional#empty()} if the target is not modified or no result is produced
     * @throws FatalException if modification fails due to a non-recoverable error
     */
    Optional<MatchResponse> modifyMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException;

    /**
     * Retrieves a single match by its identifier.
     *
     * @param matchId the identifier of the match to retrieve
     * @return an {@link Optional} containing the matching {@link MatchResponse} if found;
     * otherwise {@link Optional#empty()}
     */
    Optional<MatchResponse> getMatch(Long matchId);
}