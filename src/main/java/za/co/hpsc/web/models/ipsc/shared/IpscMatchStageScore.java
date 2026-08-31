package za.co.hpsc.web.models.ipsc.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;

/**
 * A single competitor's Comstock-scored result for one match stage.
 *
 * <p>
 * Extends {@link IpscCommonScore} with the values specific to a single stage: the
 * {@link #rawPoints} scored (alpha/charlie/delta hits weighted by the power factor less
 * no-shoots, misses, and penalties), and the resulting {@link #hitFactor} —
 * {@code rawPoints / time}, using the inherited {@link IpscCommonScore#getTime() time} —
 * which is the figure Comstock stages are actually ranked on. The stage winner's hit factor
 * is the 100% benchmark that every other competitor's
 * {@link IpscCommonScore#getPercentage() percentage} and
 * {@link IpscCommonScore#getWeightedPoints() weighted points} for the stage are calculated
 * against.
 * </p>
 *
 * @since 7.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class IpscMatchStageScore extends IpscCommonScore {
    /** Raw points scored on this stage (weighted alpha/charlie/delta hits minus penalties)
     * before dividing by time. */
    private Integer rawPoints;
    /** {@code rawPoints / time} — the figure the stage is ranked on; the stage winner's hit factor
     * is the 100% benchmark. */
    private BigDecimal hitFactor;

    public IpscMatchStageScore(BigDecimal percentage, BigDecimal weightedPoints, BigDecimal time,
                               PowerFactor powerFactor, Integer alpha, Integer charlie, Integer delta,
                               Integer noShoots, Integer misses, Integer noPenaltyMisses,
                               Integer proceduralErrors, Integer additionalPenalties, Integer rawPoints,
                               BigDecimal hitFactor) {
        super(percentage, weightedPoints, time, powerFactor, alpha, charlie, delta, noShoots,
                misses, noPenaltyMisses, proceduralErrors, additionalPenalties);
        this.rawPoints = rawPoints;
        this.hitFactor = hitFactor;
    }
}

