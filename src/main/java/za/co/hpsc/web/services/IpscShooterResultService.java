package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;

import java.util.List;

/**
 * The {@code IpscShooterResultService} interface provides access to shooter results: each competitor's result in
 * a match, calculated from the match's competitors and read back by match.
 *
 * @since 15.0.0
 */
public interface IpscShooterResultService {
    /**
     * Calculates the shooter results for one match, creating or updating a {@link za.co.hpsc.web.domain.ShooterResult}
     * for each of the match's competitors.
     *
     * <p>
     * A match's competitors are ranked within their firearm type and division by match points, highest first, with
     * competitors on equal points sharing a rank and the next rank skipped. Each competitor's percentage is their
     * points as a percentage of the group winner's points, to {@code IpscConstants.PERCENTAGE_SCALE} decimal places.
     * A competitor without match points gets neither a rank nor a percentage. Every result is stamped with the date
     * and time it was calculated.
     * </p>
     *
     * <p>
     * Calculating again updates the existing results rather than adding duplicates, but only when they are out of
     * date: that is, when the match, or any of its competitors, was updated at least five minutes after the results
     * were calculated, or a competitor has no result yet. Otherwise nothing is recalculated and the current results
     * are returned as they are.
     * </p>
     *
     * @param matchId the identifier of the match to calculate results for.
     * @return the match, linked to its shooter results, freshly calculated unless they were already up to date; the
     * results are empty if the match has no competitors.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 15.0.0
     */
    ShooterResponseHolder calculateMatchShooterResult(Long matchId) throws NonFatalException;

    /**
     * Retrieves the shooter results for one match.
     *
     * @param matchId the identifier of the match to retrieve results for.
     * @return the match, linked to its shooter results in order of creation; the results are empty if nobody has a
     * result in the match.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 15.0.0
     */
    ShooterResponseHolder getShooterResults(Long matchId) throws NonFatalException;

    /**
     * Retrieves the shooter results for every match.
     *
     * @return one holder per match, ordered by match date, oldest first, each linking the match to its shooter
     * results, which are empty for a match nobody has a result in; empty if there are no matches.
     * @since 15.0.0
     */
    List<ShooterResponseHolder> getAllShooterResults();
}
