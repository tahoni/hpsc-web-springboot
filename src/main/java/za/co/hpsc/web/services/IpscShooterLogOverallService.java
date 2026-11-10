package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;

import java.util.List;

/**
 * The {@code IpscShooterLogOverallService} interface provides access to shooter log overall results: each verified
 * competitor's overall standing across the matches of a shooter log, calculated from their shooter results and read
 * back by shooter log.
 *
 * @since 16.1.0
 */
public interface IpscShooterLogOverallService {
    /**
     * Calculates the overall results for one shooter log, creating or updating a
     * {@link za.co.hpsc.web.domain.ShooterLogOverall} for each verified competitor with a shooter result in one of
     * the log's matches.
     *
     * <p>
     * The shooter results of each of the log's matches are brought up to date first, as by
     * {@link IpscShooterResultService#calculateMatchShooterResult(Long)}. Only competitors whose {@code isVerified} is
     * {@code true} are included; the overall of one who is not, or who no longer has a result, is removed. Every
     * match must be scheduled within the log's start and end dates, both inclusive. That is checked again here, since a
     * match can be rescheduled after it was added to the log, and nothing is calculated or changed if one is outside
     * them. A competitor's overall percentage is the
     * average of their best {@code IpscConstants.SHOOTER_LOG_BEST_MATCHES} percentages across the matches they have
     * one in (all of them if they have fewer), to {@code IpscConstants.PERCENTAGE_SCALE} decimal places. Their
     * category and division are those of their most recent match in the log. Competitors are ranked by overall
     * percentage, highest first, within their division and competitor category (none being a group of its own),
     * with competitors on an equal percentage sharing a rank and the next rank skipped. Every overall is stamped with
     * the date and time it was calculated.
     * </p>
     *
     * <p>
     * A shooter result without a percentage is ignored, and a competitor with none in any match gets neither a rank nor
     * a percentage. {@link za.co.hpsc.web.domain.ShooterResult} stores the percentage in a nullable column and
     * {@link IpscShooterResultService} always sets it, so this only tolerates a row inserted by hand or by an earlier
     * version, rather than failing the calculation of the whole log over one bad row.
     * </p>
     *
     * <p>
     * Calculating again updates the existing overall results rather than adding duplicates, and removes those of
     * competitors who no longer have a result in the log, but only when they are out of date: that is, when a
     * competitor has no overall yet or one calculated before a shooter result was, or the shooter log or one of its
     * matches was updated after its overall results were calculated. Otherwise, nothing is recalculated and the
     * current overall results are returned as they are.
     * </p>
     *
     * @param shooterLogId the identifier of the shooter log to calculate overall results for.
     * @return the shooter log, linked to its overall results, freshly calculated unless they were already up to date;
     * the
     * overall results are empty if the log has no competitors.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists, or a match's shooter results
     *                           cannot be calculated.
     * @throws za.co.hpsc.web.exceptions.ValidationException if a match the log covers is scheduled before its start
     *                                                      date or after its end date.
     * @since 16.1.0
     */
    ShooterLogOverallResponseHolder calculateShooterLogOverall(Long shooterLogId) throws NonFatalException;

    /**
     * Retrieves the overall results for one shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to retrieve overall results for.
     * @return the shooter log, linked to its overall results in order of creation; the overall results are empty if
     * none has been
     * calculated.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    ShooterLogOverallResponseHolder getShooterLogOverallResults(Long shooterLogId) throws NonFatalException;

    /**
     * Retrieves the overall results for every shooter log.
     *
     * @return one holder per shooter log, ordered by start date, earliest first, each linking the log to its
     * overall results, which are empty for a log none has been calculated for; empty if there are no shooter logs.
     * @since 16.1.0
     */
    List<ShooterLogOverallResponseHolder> getAllShooterLogOverallResults();
}
