package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponseHolder;

import java.util.List;

/**
 * The {@code MatchCompetitorService} interface provides methods for creating, updating, retrieving and deleting
 * match competitors: one competitor's entry in one match, in one firearm type. Implementations are responsible for
 * validating input data, resolving the entry's competitor, match and enumerated values, and mapping to and from the
 * persisted domain model.
 *
 * <p>
 * A competitor can only have one entry per match and firearm type; creating, replacing or patching an entry so that
 * it duplicates another is refused.
 * </p>
 *
 * @since 9.1.0
 */
public interface IpscMatchCompetitorService {
    /**
     * Creates a new match competitor.
     *
     * @param request the match competitor to create. Must not be null and must carry a competitor, match,
     *                competitor category, firearm type and division.
     * @return the created match competitor, including its generated ID.
     * @throws ValidationException if a required field is missing, an enumerated value is unrecognised, or the
     *                             competitor already has an entry for the match and firearm type.
     * @throws NonFatalException   if the competitor or match cannot be found.
     * @since 9.1.0
     */
    MatchCompetitorResponse createMatchCompetitor(MatchCompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Creates a batch of new match competitors from CSV data.
     *
     * <p>
     * Each row is validated and built by the same rules as
     * {@link #createMatchCompetitor(MatchCompetitorRequest)}, but each row is saved on its own, so a row that
     * fails is reported and skipped while the rest are still created. A row fails when it is missing a required
     * field, has an unrecognised enumerated value, refers to a competitor or match that cannot be found, or
     * duplicates another row, or an existing entry, for the competitor, match and firearm type. The import only
     * ever creates entries, so a {@code MatchCompetitorId} column is read but ignored.
     * </p>
     *
     * <p>
     * Only rows for HPSC's own club, {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}, are
     * created; see {@link #createMatchCompetitors(String, String)} for how a row is matched to a club.
     * </p>
     *
     * @param csvData the CSV data containing match competitor information, one match competitor per row.
     *                Must not be null or blank.
     * @return a {@link MatchCompetitorBulkResponseHolder} with one {@link MatchCompetitorBulkResponse} per CSV
     * row, in the same order, each recording whether the row was created and, when it was not, why.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, including when a required
     *                             column is missing from the header.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 9.1.0
     */
    default MatchCompetitorBulkResponseHolder createMatchCompetitors(String csvData)
            throws ValidationException, NonFatalException, FatalException {
        return createMatchCompetitors(csvData, null);
    }

    /**
     * Creates a batch of new match competitors from CSV data, limited to the rows for one club.
     *
     * <p>
     * Works as {@link #createMatchCompetitors(String)}, except that a row is only created if it is for the club
     * being imported. A row is for the club when:
     * </p>
     * <ol>
     *     <li>its {@code matchClub}, the club the competitor represented at the match, given by name or
     *     abbreviation, is that club; or, failing that,</li>
     *     <li>the home club of the competitor it identifies is that club. The competitor is found by competitor
     *     ID when the row has one, otherwise by competitor number and name, as when the row is created.</li>
     * </ol>
     * <p>
     * Any other row is not created but reported as skipped, so there is still one result per CSV row. That includes a
     * row with no {@code matchClub} whose competitor has no home club, and one whose competitor cannot be found, or
     * is ambiguous, since the club cannot then be established; the row's own competitor and match errors are
     * reported only for rows that are for the club. A row whose {@code matchClub} is supplied but is not a known club
     * is not skipped but reported as failed.
     * </p>
     *
     * <p>
     * When {@code club} is null or blank the club imported is HPSC's own club
     * ({@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}), so the one-argument method imports
     * HPSC's rows.
     * </p>
     *
     * @param csvData the CSV data containing match competitor information, one match competitor per row.
     *                Must not be null or blank.
     * @param club    the name or abbreviation of the club to import rows for; may be null or blank, in which case
     *                HPSC's own club is imported.
     * @return a {@link MatchCompetitorBulkResponseHolder} with one {@link MatchCompetitorBulkResponse} per CSV
     * row, in the same order, each recording whether the row was created and, when it was not, why.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, including when a required
     *                             column is missing from the header, or if {@code club} is not a known club.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 12.0.0
     */
    MatchCompetitorBulkResponseHolder createMatchCompetitors(String csvData, String club)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Fully replaces an existing match competitor's fields with those on the request.
     *
     * @param matchCompetitorId the identifier of the match competitor to replace.
     * @param request           the replacement fields. Must not be null and must carry a competitor, match,
     *                          competitor category, firearm type and division.
     * @return the updated match competitor.
     * @throws ValidationException if a required field is missing, an enumerated value is unrecognised, or the
     *                             replacement would duplicate another entry for the competitor, match and firearm
     *                             type.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists, or the competitor
     *                             or match cannot be found.
     * @since 9.1.0
     */
    MatchCompetitorResponse updateMatchCompetitor(Long matchCompetitorId, MatchCompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing match competitor, applying only the fields the request supplies.
     *
     * <p>
     * No field on the request is required. A {@code null} field is left unchanged, and so is a required field
     * (competitor category, firearm type or division) that is blank or empty — a patch never clears one.
     * </p>
     *
     * @param matchCompetitorId the identifier of the match competitor to update.
     * @param request           the fields to change. Must not be null; any field left {@code null} is left
     *                          unchanged.
     * @return the updated match competitor.
     * @throws ValidationException if an enumerated value is unrecognised, or the result would duplicate another
     *                             entry for the competitor, match and firearm type.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists, or a changed
     *                             competitor or match cannot be found.
     * @since 9.1.0
     */
    MatchCompetitorResponse patchMatchCompetitor(Long matchCompetitorId, MatchCompetitorPatchRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing match competitor.
     *
     * @param matchCompetitorId the identifier of the match competitor to retrieve.
     * @return the match competitor.
     * @throws NonFatalException if no match competitor with {@code matchCompetitorId} exists.
     * @since 9.1.0
     */
    MatchCompetitorResponse getMatchCompetitor(Long matchCompetitorId)
            throws NonFatalException;

    /**
     * Retrieves every match competitor.
     *
     * @return all persisted match competitors; empty if there are none.
     * @since 9.1.0
     */
    List<MatchCompetitorResponse> getAllMatchCompetitors();

    /**
     * Deletes an existing match competitor.
     *
     * <p>
     * A match competitor still referenced by other records, such as its stage scores, is refused rather than
     * deleted along with them. This also holds if another request adds such a reference while the delete is in
     * progress.
     * </p>
     *
     * @param matchCompetitorId the identifier of the match competitor to delete.
     * @throws ValidationException if the match competitor is still referenced when the delete is flushed.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists.
     * @since 9.1.0
     */
    void deleteMatchCompetitor(Long matchCompetitorId)
            throws ValidationException, NonFatalException;
}
