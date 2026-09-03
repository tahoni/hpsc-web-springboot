package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;

/**
 * The {@code IpscCompetitorService} interface provides methods for creating, updating and
 * retrieving IPSC competitors. Implementations are responsible for validating input data,
 * resolving the competitor's home club and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscCompetitorService {
    /**
     * Creates a new IPSC competitor.
     *
     * @param request the competitor to create. Must not be null and must carry a first name and
     *                last name; a club number is required only when {@code homeClub} is HPSC and
     *                is otherwise ignored (forced to {@code null}).
     * @return the created competitor, including its generated ID.
     * @throws ValidationException if a required field is missing, the gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or the home club is
     *                             HPSC without a club number.
     * @throws NonFatalException   if the named home club cannot be found.
     */
    CompetitorResponse createCompetitor(CompetitorRequest request) throws ValidationException, NonFatalException;

    /**
     * Creates a batch of new IPSC competitors from CSV data.
     *
     * <p>
     * Each row is created independently via {@link #createCompetitor(CompetitorRequest)}, so the
     * same validation, gender resolution and home club resolution rules apply per row.
     * </p>
     *
     * @param csvData the CSV data containing competitor information, one competitor per row.
     *                Must not be null or blank.
     * @return a {@link CompetitorResponseHolder} containing the created competitors, in the same
     * order as the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or if a row's home
     *                             club is HPSC without a club number.
     * @throws NonFatalException   if a row's named home club cannot be found.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    CompetitorResponseHolder createCompetitors(String csvData)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Fully replaces an existing IPSC competitor's fields with those on the request.
     *
     * @param competitorId the identifier of the competitor to replace.
     * @param request      the competitor's replacement fields. Must not be null and must carry
     *                     a first name and last name; a club number is required only when
     *                     {@code homeClub} is HPSC and is otherwise ignored (forced to
     *                     {@code null}).
     * @return the updated competitor.
     * @throws ValidationException if a required field is missing, the gender doesn't match a
     *                             known {@link za.co.hpsc.web.enums.Gender}, or the home club is
     *                             HPSC without a club number.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    CompetitorResponse updateCompetitor(Long competitorId, CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing IPSC competitor, applying only the non-null fields on the
     * request.
     *
     * @param competitorId the identifier of the competitor to update.
     * @param request      the fields to change. Must not be null; any field left {@code null}
     *                     is left unchanged. Touching either {@code homeClub} or
     *                     {@code clubNumber} re-applies the club number rule: required when the
     *                     resulting home club is HPSC, forced to {@code null} otherwise.
     * @return the updated competitor.
     * @throws ValidationException if the resulting home club is HPSC without a club number, or
     *                             the gender doesn't match a known
     *                             {@link za.co.hpsc.web.enums.Gender}.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    CompetitorResponse patchCompetitor(Long competitorId, CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing IPSC competitor.
     *
     * @param competitorId the identifier of the competitor to retrieve.
     * @return the competitor.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    CompetitorResponse getCompetitor(Long competitorId) throws NonFatalException;
}
