package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorVerifyRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorVerifyResponseHolder;

import java.util.List;

/**
 * The {@code IpscCompetitorService} interface provides methods for creating, updating,
 * retrieving and deleting IPSC competitors. Implementations are responsible for validating input data,
 * resolving the competitor's home club and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscCompetitorService {
    /**
     * Creates a new IPSC competitor.
     *
     * @param request the competitor to create. Must not be null and must carry a first name and
     *                last name; a club number is required only when {@code homeClub} is
     *                {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION} and
     *                is otherwise ignored (forced to {@code null}).
     * @return the created competitor, including its generated ID.
     * @throws ValidationException if a required field is missing, the gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or the home club is
     *                             {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             without a club number.
     * @throws NonFatalException   if the named home club cannot be found.
     * @since 8.0.0
     */
    CompetitorResponse createCompetitor(CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Creates a batch of new IPSC competitors from CSV data, updating and verifying any that already exist.
     *
     * <p>
     * A row refers to an existing competitor when {@link IpscEntityCompetitorService#findCompetitorByIdentifierAndFullName}
     * finds one from its club number or, when it has no club number, its ID number, and from its full name. That
     * competitor takes the row's data, as in a full update, and is marked verified rather than a second one being
     * created. A row that matches a competitor on its full name alone is skipped with a logged warning, as the name
     * cannot tell two people apart. A row that matches several competitors is refused.
     * </p>
     *
     * <p>
     * Each row is validated and built by the same rules as
     * {@link #createCompetitor(CompetitorRequest)} (validation, gender resolution and home club
     * resolution). Every row is checked before any is saved, and all are then saved in a single
     * transaction, so either every row is created or none is.
     * </p>
     *
     * @param csvData the CSV data containing competitor information, one competitor per row.
     *                Must not be null or blank.
     * @return a {@link CompetitorResponseHolder} containing the created or updated competitors, in the same
     * order as the CSV rows, and the result of every row, including one that was skipped.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or if a row's home
     *                             club is {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             without a club number, or if a row matches several existing competitors.
     * @throws NonFatalException   if a row's named home club cannot be found.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 8.0.0
     */
    CompetitorResponseHolder createCompetitors(String csvData)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Marks several IPSC competitors as verified in one call.
     *
     * <p>
     * Every competitor that exists is verified in a single transaction, and one that is already verified is left as it
     * is. A competitor ID that matches nobody does not fail the request: it is reported in its own result, so the
     * others are still verified. A repeated ID is handled once.
     * </p>
     *
     * @param request the competitors to verify. Must not be null and must name at least one competitor ID, none of
     *                them null.
     * @return a {@link CompetitorVerifyResponseHolder} with one result per distinct competitor ID, in the order first
     * given, each saying whether the competitor was found and now verified.
     * @throws ValidationException if the request is null, names no competitor ID, or contains a null one.
     * @since 16.2.0
     */
    CompetitorVerifyResponseHolder verifyCompetitors(CompetitorVerifyRequest request)
            throws ValidationException;

    /**
     * Fully replaces an existing IPSC competitor's fields with those on the request.
     *
     * @param competitorId the identifier of the competitor to replace.
     * @param request      the competitor's replacement fields. Must not be null and must carry
     *                     a first name and last name; a club number is required only when
     *                     {@code homeClub} is {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}
     *                     and is otherwise ignored (forced to {@code null}).
     * @return the updated competitor.
     * @throws ValidationException if a required field is missing, the gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or the home club is
     *                             {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             without a club number.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     * @since 8.0.0
     */
    CompetitorResponse updateCompetitor(Long competitorId, CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing IPSC competitor, applying only the non-null fields on the
     * request.
     *
     * @param competitorId the identifier of the competitor to update.
     * @param request      the fields to change. Must not be null; any field left {@code null}
     *                     is left unchanged. A supplied but blank {@code gender} or
     *                     {@code homeClub} clears that field, and a supplied
     *                     {@code emailAddresses} list replaces the competitor's existing email
     *                     addresses rather than being merged into them. Touching either
     *                     {@code homeClub} or {@code clubNumber} re-applies the club number rule
     *                     (falling back to the competitor's existing club number if the request
     *                     has none): required when the resulting home club is
     *                     {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION},
     *                     forced to {@code null} otherwise.
     * @return the updated competitor.
     * @throws ValidationException if the resulting home club is
     *                             {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             without a (non-blank) club number, or the gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     * @since 8.0.0
     */
    CompetitorResponse patchCompetitor(Long competitorId, CompetitorPatchRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing IPSC competitor.
     *
     * @param competitorId the identifier of the competitor to retrieve.
     * @return the competitor.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     * @since 8.0.0
     */
    CompetitorResponse getCompetitor(Long competitorId)
            throws NonFatalException;

    /**
     * Retrieves every IPSC competitor.
     *
     * @return all persisted competitors; empty if there are none.
     * @since 8.0.0
     */
    List<CompetitorResponse> getAllCompetitors();

    /**
     * Deletes an existing IPSC competitor, together with their email addresses.
     *
     * <p>
     * A competitor is only deleted while nothing else references them: one with recorded
     * match results or shooter logs is refused rather than deleted along with that history.
     * This also holds if another request adds such a reference while the delete is in progress.
     * </p>
     *
     * @param competitorId the identifier of the competitor to delete.
     * @throws ValidationException if the competitor still has match results or shooter logs, or
     *                             is otherwise still referenced when the delete is flushed.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists.
     * @since 8.0.0
     */
    void deleteCompetitor(Long competitorId)
            throws ValidationException, NonFatalException;
}
