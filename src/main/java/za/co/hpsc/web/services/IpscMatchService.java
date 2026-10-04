package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;

import java.util.List;

/**
 * The {@code IpscMatchService} interface provides methods for creating, updating, retrieving
 * and deleting IPSC matches. Implementations are responsible for validating
 * input data, resolving the hosting club and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscMatchService {
    /**
     * Creates a new IPSC match.
     *
     * @param request the match to create. Must not be null and must carry a match name, date,
     *                club and firearm type/category.
     * @return the created match, including its generated ID.
     * @throws ValidationException if a required field is missing, or the firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if the named club cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    MatchResponse createMatch(MatchRequest request)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Creates a batch of new IPSC matches from CSV data.
     *
     * <p>
     * Each row is validated and built by the same rules as {@link #createMatch(MatchRequest)}
     * (validation, firearm type/category resolution and club resolution). Every row is checked
     * before any is saved, and all are then saved in a single transaction, so either every row is
     * created or none is.
     * </p>
     *
     * @param csvData the CSV data containing match information, one match per row. Must not be
     *                null or blank.
     * @return a {@link MatchResponseHolder} containing the created matches, in the same order as
     * the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if a row's named club cannot be found.
     * @throws FatalException      if an I/O error occurs while reading the CSV data, or a row names
     *                             no club and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    MatchResponseHolder createMatches(String csvData)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Fully replaces an existing IPSC match's fields with those on the request.
     *
     * @param matchId the identifier of the match to replace.
     * @param request the match's replacement fields. Must not be null and must carry a match
     *                name, date, club and firearm type/category.
     * @return the updated match.
     * @throws ValidationException if a required field is missing, or the firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    MatchResponse updateMatch(Long matchId, MatchRequest request)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Partially updates an existing IPSC match, applying only the non-null fields on the
     * request.
     *
     * @param matchId the identifier of the match to update.
     * @param request the fields to change. Must not be null; any field left {@code null} is
     *                left unchanged. A supplied but blank {@code club} resets the match's club
     *                to {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                rather than being rejected. A supplied {@code matchDate} sets the match's
     *                scheduled date to the start of that day.
     * @return the updated match.
     * @throws ValidationException if the firearm type/category doesn't match a known
     *                             {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if no match with {@code matchId} exists, the named club
     *                             cannot be found, or the request's {@code club} is blank and no
     *                             club exists for
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @throws FatalException      if the request's {@code club} is blank and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    MatchResponse patchMatch(Long matchId, MatchPatchRequest request)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Retrieves an existing IPSC match.
     *
     * @param matchId the identifier of the match to retrieve.
     * @return the match.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    MatchResponse getMatch(Long matchId)
            throws NonFatalException;

    /**
     * Retrieves every IPSC match.
     *
     * @return all persisted matches.
     */
    List<MatchResponse> getAllMatches();

    /**
     * Deletes an existing IPSC match.
     *
     * <p>
     * A match is only deleted while nothing else references it: one with recorded competitor
     * results or shooter-log entries is refused rather than deleted along with
     * that history. This also holds if another request adds such a reference while the delete
     * is in progress.
     * </p>
     *
     * @param matchId the identifier of the match to delete.
     * @throws ValidationException if the match still has competitor results or shooter-log
     *                             entries, or is otherwise still referenced when the
     *                             delete is flushed.
     * @throws NonFatalException   if no match with {@code matchId} exists.
     */
    void deleteMatch(Long matchId)
            throws ValidationException, NonFatalException;
}
