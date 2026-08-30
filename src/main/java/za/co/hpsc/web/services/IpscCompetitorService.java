package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;

/**
 * The {@code IpscCompetitorService} interface provides methods for creating, updating and
 * retrieving IPSC competitors. Implementations are responsible for validating input data,
 * resolving the competitor's home club, and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscCompetitorService {
    /**
     * Creates a new IPSC competitor.
     *
     * @param request the competitor to create. Must not be null, and must carry a first name,
     *                last name and club number.
     * @return the created competitor, including its generated ID.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if the named home club cannot be found.
     */
    CompetitorRequest createCompetitor(CompetitorRequest request) throws ValidationException, NonFatalException;

    /**
     * Fully replaces an existing IPSC competitor's fields with those on the request.
     *
     * @param competitorId the identifier of the competitor to replace.
     * @param request      the competitor's replacement fields. Must not be null, and must carry
     *                     a first name, last name and club number.
     * @return the updated competitor.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    CompetitorRequest updateCompetitor(Long competitorId, CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing IPSC competitor, applying only the non-null fields on the
     * request.
     *
     * @param competitorId the identifier of the competitor to update.
     * @param request      the fields to change. Must not be null; any field left {@code null}
     *                     is left unchanged.
     * @return the updated competitor.
     * @throws ValidationException if the club number is blank.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    CompetitorRequest patchCompetitor(Long competitorId, CompetitorRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing IPSC competitor.
     *
     * @param competitorId the identifier of the competitor to retrieve.
     * @return the competitor.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    CompetitorRequest getCompetitor(Long competitorId) throws NonFatalException;
}
