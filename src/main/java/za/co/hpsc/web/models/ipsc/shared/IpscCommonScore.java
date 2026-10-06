package za.co.hpsc.web.models.ipsc.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Common scoring fields shared by IPSC results scored under the Comstock scoring method.
 *
 * <p>
 * Comstock is the standard IPSC scoring method: a competitor fires an unlimited number of
 * rounds (as many as needed to score all required hits, plus make-up shots) and is timed
 * from the start signal to the last shot. Each scoring hit is graded {@link #alpha},
 * {@link #charlie}, or {@link #delta} depending on where it lands in the target zones, and
 * the raw points awarded per hit depend on the power factor of the competitor's entry, which
 * is not held here (Major hits score more per zone than Minor hits). {@link #noShoots}, {@link #misses}, and any
 * {@link #proceduralErrors} or {@link #additionalPenalties} are deducted from the raw score.
 * </p>
 *
 * <p>
 * For a single stage, dividing the resulting raw score by the time taken yields the "hit
 * factor" (see {@code IpscMatchStageScore}), which is why Comstock is often called
 * hit-factor scoring. Within each stage/match category, the competitor with the highest raw
 * score (or hit factor) becomes the 100% benchmark; every other competitor's {@link #points}
 * is expressed as a {@link #percentage} of that benchmark, and match-level totals
 * ({@code IpscMatchScore}) sum the stage percentages/points across the whole match.
 * </p>
 *
 * @since 7.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class IpscCommonScore {
    /** This competitor's score expressed as a percentage of the stage/match winner's score
     * (winner = 100%). */
    protected BigDecimal percentage;
    /** The weighted score points awarded to this competitor for the stage/match. */
    protected BigDecimal points;
    /** Time, in seconds, taken to complete the course of fire; divided into the raw score
     * to produce the Comstock hit factor. */
    protected BigDecimal time;
    /** Number of hits in the A-zone (highest-value scoring zone). */
    protected Integer alpha;
    /** Number of hits in the C-zone (mid-value scoring zone). */
    protected Integer charlie;
    /** Number of hits in the D-zone (lowest-value scoring zone). */
    protected Integer delta;
    /** Number of penalty hits scored on no-shoot targets; each deducts points from
     * the raw score. */
    protected Integer noShoots;
    /** Number of required hits not scored on a target (misses); each deducts points from
     * the raw score. */
    protected Integer misses;
    /** Misses that, under the applicable rules, did not attract the usual miss penalty. */
    protected Integer noPenaltyMisses;
    /** Count of procedural penalties (rule infractions) applied, each deducting a fixed
     * number of points. */
    protected Integer proceduralErrors;
    /** Count of other additional penalties (e.g. safety, range command) applied on top of
     * scoring/procedurals. */
    protected Integer additionalPenalties;

    public IpscCommonScore(BigDecimal percentage, BigDecimal points, BigDecimal time,
                           Integer alpha, Integer charlie, Integer delta,
                           Integer noShoots, Integer misses, Integer noPenaltyMisses,
                           Integer proceduralErrors, Integer additionalPenalties) {
        this.percentage = percentage;
        this.points = points;
        this.time = time;
        this.alpha = alpha;
        this.charlie = charlie;
        this.delta = delta;
        this.noShoots = noShoots;
        this.misses = misses;
        this.noPenaltyMisses = noPenaltyMisses;
        this.proceduralErrors = proceduralErrors;
        this.additionalPenalties = additionalPenalties;
    }
}
