package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.shared.MatchWithStages;

import java.util.List;
import java.util.Optional;

/**
 * Defines CRUD-style operations for IPSC match aggregates represented by
 * {@link MatchWithStages}.
 * <p>
 * Implementations are responsible for:
 * </p>
 * <ul>
 *   <li>Creating new matches</li>
 *   <li>Applying full or partial updates to existing matches</li>
 *   <li>Returning filtered match lists and individual matches</li>
 * </ul>
 * <p>
 * A {@link FatalException} indicates a non-recoverable failure during processing
 * (for example, transformation, validation, or persistence errors that cannot be
 * handled as a normal business outcome).
 * </p>
 */
public interface IpscMatchService {

    /**
     * Inserts a new IPSC match aggregate.
     *
     * @param matchWithStages the incoming match payload, including match metadata
     *                        and optional stage details
     * @throws FatalException when the insert operation cannot be completed due to
     *                        a non-recoverable error
     */
    void insertMatch(MatchWithStages matchWithStages)
            throws FatalException;

    /**
     * Performs a full update of an existing match identified by {@code matchId}.
     * <p>
     * Full update semantics typically mean the submitted payload is treated as the
     * authoritative state for updatable fields.
     * </p>
     *
     * @param matchId         the identifier of the match to update
     * @param matchWithStages the new match state to apply
     */
    void updateMatch(Long matchId, MatchWithStages matchWithStages);

    /**
     * Performs a partial update (patch-style) of an existing match identified by
     * {@code matchId}.
     * <p>
     * Partial update semantics typically mean only provided fields are changed and
     * omitted fields remain unchanged.
     * </p>
     *
     * @param matchId         the identifier of the match to modify
     * @param matchWithStages the partial match payload to merge
     */
    void modifyMatch(Long matchId, MatchWithStages matchWithStages);

    /**
     * Retrieves matches that satisfy the provided filter criteria.
     *
     * @param matchWithStages filter fields used to constrain the search; implementations
     *                        may treat null/empty fields as wildcards
     * @return a non-null list of matching {@link MatchWithStages} records; empty when
     * no matches satisfy the filter
     */
    List<MatchWithStages> getMatches(MatchWithStages matchWithStages);

    /**
     * Retrieves a single match by its identifier.
     *
     * @param matchId the unique identifier of the match to load
     * @return {@link Optional#of(Object)} containing the requested match when found;
     * otherwise {@link Optional#empty()}
     */
    Optional<MatchWithStages> getMatch(Long matchId);
}