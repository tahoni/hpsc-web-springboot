package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * All competitors' overall match results, shaped for deserializing a Practiscore
 * "Overall" results CSV export directly: {@link JsonNaming} maps camelCase field names to
 * Practiscore's upper-camel-case headers, and each field carries the {@link JsonProperty}
 * override for headers that don't follow that convention (e.g. {@code %}, {@code HF}).
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
 *
 * @since 7.4.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchOverallScoresRequestForCSV {
    /** Internal identifier of the match this result belongs to; not part of the CSV export,
     * populated separately. */
    private Long matchId;
    /** The competitor's full name. */
    private String name;
    /** Overall match score as a percentage of the match winner's score (winner = 100%). */
    @JsonProperty("%")
    private BigDecimal percentage;
    /** Total weighted points earned across the match — the sum of the competitor's
     * per-stage points. */
    @JsonProperty("Pts")
    private BigDecimal points;
    /** Total time, in seconds, taken across the match's stages. */
    @JsonProperty("Time")
    private BigDecimal time;
    /** Total hits (weighted by the power factor less penalties) as a percentage of the maximum
     * points available in the match — accuracy only, independent of time. */
    @JsonProperty("%psbl")
    private BigDecimal percentageOfPossiblePoints;
    /** Hit factor — raw score divided by time, reported for overall results as an aggregate
     * across the match. */
    @JsonProperty("HF")
    private BigDecimal hitFactor;
    /** Division the competitor shot in, e.g. Open, Standard, Production. */
    @JsonProperty("Div")
    private String division;
    /** The competitor's club. */
    @JsonProperty("Class")
    private String club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior — comma-separated when more than one applies. */
    @JsonProperty("Cats")
    private String categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta
     * hit is worth. */
    @JsonProperty("PF")
    private String powerFactor;
    /** The competitor's membership number. */
    @JsonProperty("Mem#")
    private String membershipNumber;
    /** Total A-zone (alpha) hits across the match — the highest-value scoring zone. */
    @JsonProperty("A")
    private Integer alpha;
    /** Total C-zone (charlie) hits across the match — the mid-value scoring zone. */
    @JsonProperty("C")
    private Integer charlie;
    /** Total D-zone (delta) hits across the match — the lowest-value scoring zone. */
    @JsonProperty("D")
    private Integer delta;
    /** Total required hits not scored (misses) across the match; each deducts points from the raw score. */
    @JsonProperty("M")
    private Integer misses;
    /** Total misses that, under the applicable rules, did not attract the usual miss penalty. */
    @JsonProperty("NPM")
    private Integer noPenaltyMisses;
    /** Total no-shoot penalty hits across the match; each deducts points from the raw score. */
    @JsonProperty("NS")
    private Integer noShoots;
    /** Total procedural penalties (rule infractions) applied across the match. */
    @JsonProperty("Proc")
    private Integer proceduralErrors;
    /** Total additional penalties (e.g. safety, range command) applied across the match,
     * on top of scoring/procedurals. */
    @JsonProperty("Apen")
    private Integer additionalPenalties;
}
