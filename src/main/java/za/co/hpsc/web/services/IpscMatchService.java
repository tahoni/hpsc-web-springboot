package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.util.List;
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
     * Persists a new match based on the supplied match payload.
     *
     * @param matchResponse the match data to insert
     * @throws FatalException if the match cannot be inserted due to a non-recoverable error
     */
    void insertMatch(MatchResponse matchResponse)
            throws FatalException;

    /**
     * Performs a full update of an existing match identified by {@code matchId}.
     * <p>
     * In a typical full update flow, omitted fields may be treated as replacements.
     * Exact behaviour depends on implementation.
     * </p>
     *
     * @param matchId       the identifier of the match to update
     * @param matchResponse the new match data to apply
     * @throws FatalException if the update fails due to a non-recoverable error
     */
    void updateMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException;

    /**
     * Performs a partial update (patch-like modification) on an existing match.
     * <p>
     * In a typical partial update flow, only provided fields are changed while
     * other existing values are retained. Exact behaviour depends on implementation.
     * </p>
     *
     * @param matchId       the identifier of the match to modify
     * @param matchResponse the partial match data to apply
     * @throws FatalException if the modification fails due to a non-recoverable error
     */
    void modifyMatch(Long matchId, MatchResponse matchResponse) throws FatalException;

    /**
     * Retrieves matches that satisfy the given search/filter criteria.
     *
     * @param matchSearchRequest search parameters used to filter returned matches
     * @return a list of matching {@link MatchResponse} objects; may be empty if no matches are found
     */
    List<MatchResponse> getMatches(MatchSearchRequest matchSearchRequest);

    /**
     * Retrieves a single match by its identifier.
     *
     * @param matchId the identifier of the match to retrieve
     * @return an {@link Optional} containing the matching {@link MatchResponse} if found,
     * otherwise {@link Optional#empty()}
     */
    Optional<MatchResponse> getMatch(Long matchId);
}