package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;

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
     */
    MatchCompetitorResponse createMatchCompetitor(MatchCompetitorRequest request)
            throws ValidationException, NonFatalException;

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
     */
    MatchCompetitorResponse patchMatchCompetitor(Long matchCompetitorId, MatchCompetitorPatchRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing match competitor.
     *
     * @param matchCompetitorId the identifier of the match competitor to retrieve.
     * @return the match competitor.
     * @throws NonFatalException if no match competitor with {@code matchCompetitorId} exists.
     */
    MatchCompetitorResponse getMatchCompetitor(Long matchCompetitorId) throws NonFatalException;

    /**
     * Retrieves every match competitor.
     *
     * @return all persisted match competitors; empty if there are none.
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
     */
    void deleteMatchCompetitor(Long matchCompetitorId) throws ValidationException, NonFatalException;
}
