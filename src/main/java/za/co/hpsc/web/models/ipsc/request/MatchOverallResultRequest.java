package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A single competitor's overall match result — the plain, non-CSV counterpart of
 * {@link MatchOverallResultRequestForCSV}, carrying the same data without the Practiscore
 * column-mapping annotations.
 *
 * <p>
 * The scoring fields mirror Comstock scoring as described on {@code IpscCommonScore}:
 * {@link #alpha}, {@link #charlie}, and {@link #delta} are the competitor's hit-zone
 * counts, {@link #noShoots}, {@link #misses}, {@link #proceduralErrors}, and
 * {@link #additionalPenalties} reduce the raw score, and {@link #points} and
 * {@link #percentage} are the resulting match totals — the sum of the competitor's
 * per-stage weighted points/percentages — with the match winner as the 100% benchmark.
 * {@link #percentageOfPossiblePoints} is the separate, time-independent accuracy measure:
 * total hits (weighted by {@link #powerFactor}) as a percentage of the maximum points
 * available in the match.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchOverallResultRequest {
    /** Internal identifier of the match this result belongs to. */
    private Long matchId;
    /** The competitor's full name. */
    private String name;
    /** Overall match score as a percentage of the match winner's score (winner = 100%). */
    private BigDecimal percentage;
    /** Total weighted points earned across the match — the sum of the competitor's per-stage points. */
    private BigDecimal points;
    /** Total time, in seconds, taken across the match's stages. */
    private BigDecimal time;
    /** Total hits (weighted by the power factor less penalties) as a percentage of the maximum points available in the match — accuracy only, independent of time. */
    private BigDecimal percentageOfPossiblePoints;
    /** Hit factor — raw score divided by time, reported for overall results as an aggregate across the match. */
    private BigDecimal hitFactor;
    /** Division the competitor shot in, e.g. Open, Standard, Production. */
    private String division;
    /** The competitor's club. */
    private String club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior — comma-separated when more than one applies. */
    private String categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta hit is worth. */
    private String powerFactor;
    /** The competitor's membership number. */
    private String membershipNumber;
    /** Total A-zone (alpha) hits across the match — the highest-value scoring zone. */
    private Integer alpha;
    /** Total C-zone (charlie) hits across the match — the mid-value scoring zone. */
    private Integer charlie;
    /** Total D-zone (delta) hits across the match — the lowest-value scoring zone. */
    private Integer delta;
    /** Total required hits not scored (misses) across the match; each deducts points from the raw score. */
    private Integer misses;
    /** Total misses that, under the applicable rules, did not attract the usual miss penalty. */
    private Integer noPenaltyMisses;
    /** Total no-shoot penalty hits across the match; each deducts points from the raw score. */
    private Integer noShoots;
    /** Total procedural penalties (rule infractions) applied across the match. */
    private Integer proceduralErrors;
    /** Total additional penalties (e.g. safety, range command) applied across the match, on top of scoring/procedurals. */
    private Integer additionalPenalties;
}
