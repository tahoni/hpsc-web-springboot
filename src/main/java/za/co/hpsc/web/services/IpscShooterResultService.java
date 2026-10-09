package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;

import java.util.List;

/**
 * The {@code IpscShooterResultService} interface provides read-only access to shooter results: each competitor's
 * result in a match, in one firearm type.
 *
 * @since 15.0.0
 */
public interface IpscShooterResultService {
    /**
     * Retrieves the shooter results for one match.
     *
     * @param matchId the identifier of the match to retrieve results for.
     * @return the match, linked to its shooter results, best percentage first and those without a percentage last;
     * the results are empty if nobody has a result in the match.
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
