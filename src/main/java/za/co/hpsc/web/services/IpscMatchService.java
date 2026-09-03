package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;

import java.util.List;

/**
 * The {@code IpscMatchService} interface provides methods for creating, updating and
 * retrieving IPSC matches and their stages. Implementations are responsible for validating
 * input data, resolving the hosting club and mapping to and from the persisted domain model.
 *
 * @since 8.0.0
 */
public interface IpscMatchService {
    /**
     * Creates a new IPSC match together with any stages supplied on the request.
     *
     * @param request the match to create. Must not be null and must carry a match name, date,
     *                club and firearm type/category.
     * @return the created match, including its generated ID and any persisted stages.
     * @throws ValidationException if a required field is missing, or the firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if the named club cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is
     *                             null.
     */
    MatchResponse createMatch(MatchRequest request) throws ValidationException, NonFatalException, FatalException;

    /**
     * Creates a batch of new IPSC matches, together with their stages, from CSV data.
     *
     * <p>
     * Each row is created independently via {@link #createMatch(MatchRequest)}, so the same
     * validation, firearm type/category resolution and club resolution rules apply per row. A
     * row's {@code Stages} cell is parsed into individual stages as described on
     * {@link za.co.hpsc.web.models.ipsc.match.request.MatchRequestForCSV#getStages()}.
     * </p>
     *
     * @param csvData the CSV data containing match information, one match per row. Must not be
     *                null or blank.
     * @return a {@link MatchResponseHolder} containing the created matches, in the same order as
     * the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}, or if a row's
     *                             {@code Stages} cell is malformed.
     * @throws NonFatalException   if a row's named club cannot be found.
     * @throws FatalException      if an I/O error occurs while reading the CSV data, or a row names
     *                             no club and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is
     *                             null.
     */
    MatchResponseHolder createMatches(String csvData)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Fully replaces an existing IPSC match's fields and stages with those on the request.
     *
     * <p>
     * Any stages not present in the request are removed from the match; stages present are
     * (re)created.
     * </p>
     *
     * @param matchId the identifier of the match to replace.
     * @param request the match's replacement fields. Must not be null and must carry a match
     *                name, date, club and firearm type/category.
     * @return the updated match, including its persisted stages.
     * @throws ValidationException if a required field is missing, or the firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is
     *                             null.
     */
    MatchResponse updateMatch(Long matchId, MatchRequest request)
            throws ValidationException, NonFatalException, FatalException;

    /**
     * Partially updates an existing IPSC match, applying only the non-null fields on the
     * request.
     *
     * <p>
     * If stages are supplied, each is matched to an existing stage by its stage number —
     * updating it if found or adding it if not. Stages already on the match that aren't
     * mentioned in the request are left untouched.
     * </p>
     *
     * @param matchId the identifier of the match to update.
     * @param request the fields to change. Must not be null; any field left {@code null} is
     *                left unchanged.
     * @return the updated match, including its persisted stages.
     * @throws ValidationException if the named club is blank, or the firearm type/category
     *                             doesn't match a known {@link za.co.hpsc.web.enums.FirearmType}/
     *                             {@link za.co.hpsc.web.enums.MatchCategory}.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     * @throws FatalException      if the request's {@code club} is blank and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is
     *                             null.
     */
    MatchResponse patchMatch(Long matchId, MatchRequest request)
            throws ValidationException, NonFatalException, FatalException;

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
