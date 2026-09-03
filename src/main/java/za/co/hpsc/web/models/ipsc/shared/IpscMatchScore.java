package za.co.hpsc.web.models.ipsc.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;

/**
 * A single competitor's aggregated Comstock-scored result across an entire match.
 *
 * <p>
 * Extends {@link IpscCommonScore} with the match-level total: {@link #percentageOfPossiblePoints}
 * summarises the competitor's overall combat accuracy across every stage (total alpha/charlie/delta
 * hits weighted by the power factor less no-shoots, misses and penalties, as a percentage of the
 * maximum points available in the match), independent of time. The inherited
 * {@link IpscCommonScore#getWeightedPoints() points} and {@link IpscCommonScore#getPercentage() percentage}
 * are instead the sum of the competitor's per-stage Comstock (hit-factor) percentages/points,
 * which is what match standings are actually ranked on — the match winner is the 100% benchmark.
 * </p>
 *
 * @since 7.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class IpscMatchScore extends IpscCommonScore {
    /** Total hits (weighted by the power factor less penalties) as a percentage
     * of the maximum points available in the match — accuracy only, independent of time. */
    private BigDecimal percentageOfPossiblePoints;

    public IpscMatchScore(BigDecimal percentage, BigDecimal weightedPoints, BigDecimal time, PowerFactor powerFactor,
                          Integer alpha, Integer charlie, Integer delta, Integer noShoots, Integer misses,
                          Integer noPenaltyMisses, Integer proceduralErrors, Integer additionalPenalties,
                          BigDecimal percentageOfPossiblePoints) {
        super(percentage, weightedPoints, time, powerFactor, alpha, charlie, delta, noShoots,
                misses, noPenaltyMisses, proceduralErrors, additionalPenalties);
        this.percentageOfPossiblePoints = percentageOfPossiblePoints;
    }
}
