package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;

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

    /**
     * Persists a match entity along with an optional associated club within a transactional boundary.
     *
     * <p>This method coordinates persistence of match metadata (and optional club reference)
     * directly from API-level DTOs, bypassing the full import pipeline. It is useful for
     * workflows that create or update match records without complete match results or competition data.</p>
     *
     * <p><b>Persistence scope:</b></p>
     * <ul>
     *   <li>Always persists the match entity if {@code matchDto} is valid.</li>
     *   <li>Optionally persists the club entity if {@code clubDto} is provided and contains a non-null name.</li>
     *   <li>Both entities are saved within a single transaction; either all succeed or all are rolled back.</li>
     * </ul>
     *
     * <p><b>Input validation:</b></p>
     * <ul>
     *   <li>If {@code matchDto} is null, returns {@link Optional#empty()} without initiating a transaction.</li>
     *   <li>If {@code clubDto} is null or has a null name, the club is not persisted (no error raised).</li>
     *   <li>Both parameters are validated by their respective {@code init()} or conversion methods.</li>
     * </ul>
     *
     * <p><b>Transaction lifecycle:</b></p>
     * <ol>
     *   <li>Begins an explicit transaction via {@link org.springframework.transaction.PlatformTransactionManager}.</li>
     *   <li>Converts DTOs to JPA entities via helper methods ({@code getClub}, {@code getIpscMatch}).</li>
     *   <li>Persists club (if valid) and match entities to their respective repositories.</li>
     *   <li>Commits the transaction on success.</li>
     *   <li>Rolls back the entire transaction on any exception and rethrows as {@link FatalException}.</li>
     * </ol>
     *
     * <p><b>Return value:</b> The returned {@link MatchHolder} aggregates the persisted match
     * and (if applicable) club entities, allowing callers to inspect the written state.</p>
     *
     * @param matchDto the API-level match payload containing name, scheduled date, and optional
     *                 firearm type and category; may be null (in which case no transaction occurs)
     * @param clubDto  the club data to associate with the match; may be null or have null name
     *                 (in which case only the match is persisted)
     * @return an {@link Optional} containing the persisted {@link MatchHolder}
     * (with match and optionally club) when persistence succeeds;
     * {@link Optional#empty()} if {@code matchDto} is null or persistence is skipped
     * @throws FatalException if a repository operation fails, the transaction fails to commit,
     *                        or any other unrecoverable error occurs; wraps the root cause
     *                        with context describing the operation ("Unable to save the match: ...")
     * @see #saveMatchResults(DtoMapping) for full match import with stages and competitors
     * @see FatalException
     * @see MatchHolder
     */
    Optional<MatchHolder> saveMatch(MatchDto matchDto, ClubDto clubDto)
            throws FatalException;
}
