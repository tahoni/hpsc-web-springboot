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
 * All competitors' results on a single match stage, shaped for deserializing a
 * Practiscore "Stage" results CSV export directly: {@link JsonNaming} maps camelCase field
 * names to Practiscore's upper-camel-case headers, and each field carries the
 * {@link JsonProperty} override for headers that don't follow that convention (e.g.
 * {@code %}, {@code HF}).
 *
 * <p>
 * The scoring fields mirror Comstock scoring as described on {@code IpscCommonScore}:
 * {@link #alpha}, {@link #charlie}, and {@link #delta} are the competitor's hit-zone
 * counts on this stage, {@link #noShoots}, {@link #misses}, {@link #proceduralErrors}, and
 * {@link #additionalPenalties} reduce the raw score, and {@link #hitFactor} is that raw score
 * divided by {@link #time} — the figure the stage is ranked on. {@link #stagePoints} and
 * {@link #stagePercentage} are the resulting weighted points/percentage against the stage
 * winner's hit factor (100% benchmark); {@link #points} is the raw score before weighting.
 * </p>
 *
 * @since 7.4.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchStageScoresRequestForCSV {
    /** Internal identifier of the match this stage result belongs to; not part of the CSV export,
     * populated separately. */
    private Long matchId;
    /** The stage's number/order within the match. */
    private Integer stageNumber;
    /** The competitor's full name. */
    private String name;
    /** This stage's score as a percentage of the stage winner's hit factor (winner = 100%). */
    @JsonProperty("%")
    private BigDecimal stagePercentage;
    /** Weighted score points earned on this stage — the stage's raw {@link #points}
     * scaled by {@link #stagePercentage}. */
    @JsonProperty("Stg Pts")
    private BigDecimal stagePoints;
    /** Raw score points on this stage, before weighting against the stage winner. */
    @JsonProperty("Pts")
    private Integer points;
    /** Hit factor for this stage — raw score divided by {@link #time},
     * the figure the stage is ranked on. */
    @JsonProperty("HF")
    private BigDecimal hitFactor;
    /** Time, in seconds, taken to complete this stage. */
    private BigDecimal time;
    /** Division the competitor shot in, e.g. Open, Standard, Production. */
    @JsonProperty("Div")
    private String division;
    /** The competitor's club. */
    @JsonProperty("Class")
    private String club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior — comma-separated when more than one applies. */
    @JsonProperty("Cats")
    private String categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta hit is worth. */
    @JsonProperty("PF")
    private String powerFactor;
    /** The competitor's membership number. */
    @JsonProperty("Mem#")
    private String membershipNumber;
    /** A-zone (alpha) hits on this stage — the highest-value scoring zone. */
    @JsonProperty("A")
    private Integer alpha;
    /** C-zone (charlie) hits on this stage — the mid-value scoring zone. */
    @JsonProperty("C")
    private Integer charlie;
    /** D-zone (delta) hits on this stage — the lowest-value scoring zone. */
    @JsonProperty("D")
    private Integer delta;
    /** Required hits not scored (misses) on this stage; each deducts points from the raw score. */
    @JsonProperty("M")
    private Integer misses;
    /** Misses that, under the applicable rules, did not attract the usual miss penalty. */
    @JsonProperty("NPM")
    private Integer noPenaltyMisses;
    /** No-shoot penalty hits on this stage; each deducts points from the raw score. */
    @JsonProperty("NS")
    private Integer noShoots;
    /** Procedural penalties (rule infractions) applied on this stage. */
    @JsonProperty("Proc")
    private Integer proceduralErrors;
    /** Additional penalties (e.g. safety, range command) applied on this stage, on top of scoring/procedurals. */
    @JsonProperty("Apen")
    private Integer additionalPenalties;
}
