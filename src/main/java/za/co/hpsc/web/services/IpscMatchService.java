package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;

import java.util.List;

/**
 * The {@code IpscMatchService} interface provides methods for creating, updating and
 * retrieving IPSC matches and their stages. Implementations are responsible for validating
 * input data, resolving the hosting club, and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscMatchService {
    /**
     * Creates a new IPSC match together with any stages supplied on the request.
     *
     * @param request the match to create. Must not be null, and must carry a match name, date,
     *                club and firearm type/category.
     * @return the created match, including its generated ID and any persisted stages.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if the named club cannot be found.
     */
    MatchResponse createMatch(MatchRequest request) throws ValidationException, NonFatalException;

    /**
     * Fully replaces an existing IPSC match's fields and stages with those on the request.
     *
     * <p>
     * Any stages not present in the request are removed from the match; stages present are
     * (re)created.
     * </p>
     *
     * @param matchId the identifier of the match to replace.
     * @param request the match's replacement fields. Must not be null, and must carry a match
     *                name, date, club and firearm type/category.
     * @return the updated match, including its persisted stages.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     */
    MatchResponse updateMatch(Long matchId, MatchRequest request) throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing IPSC match, applying only the non-null fields on the
     * request.
     *
     * <p>
     * If stages are supplied, each is matched to an existing stage by its stage number —
     * updating it if found, or adding it if not. Stages already on the match that aren't
     * mentioned in the request are left untouched.
     * </p>
     *
     * @param matchId the identifier of the match to update.
     * @param request the fields to change. Must not be null; any field left {@code null} is
     *                left unchanged.
     * @return the updated match, including its persisted stages.
     * @throws ValidationException if the named club is blank.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     */
    MatchResponse patchMatch(Long matchId, MatchRequest request) throws ValidationException, NonFatalException;

    /**
     * Retrieves an existing IPSC match together with its stages.
     *
     * @param matchId the identifier of the match to retrieve.
     * @return the match, including its persisted stages.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    MatchResponse getMatch(Long matchId) throws NonFatalException;

    /**
     * Retrieves every IPSC match together with its stages.
     *
     * @return all persisted matches, including their stages.
     */
    List<MatchResponse> getAllMatches();
}
