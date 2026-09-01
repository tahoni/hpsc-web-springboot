package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;
import java.util.List;

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
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchOverallScoresRequestForCSV {
    /** Internal identifier of the match this result belongs to; not part of the CSV export,
     * populated separately. */
    private Long matchId;
    /** The competitor's full name. */
    @JsonProperty(required = true)
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
    private Division division;
    /** The competitor's club. */
    @JsonProperty("Class")
    private ClubIdentifier club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior. */
    @JsonProperty("Cats")
    private List<CompetitorCategory> categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta
     * hit is worth. */
    @JsonProperty("PF")
    private PowerFactor powerFactor;
    /** The competitor's membership number. */
    @JsonProperty(value = "Mem#", required = true)
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

    /**
     * Constructs a {@code MatchOverallScoresRequestForCSV} from its Practiscore CSV/JSON row.
     *
     * <p>
     * Each parameter is bound to its Practiscore column name explicitly (matching the field's own
     * {@link JsonProperty} override, or the {@link JsonNaming} strategy's transform where none is
     * set), since a multi-argument {@code @JsonCreator} constructor needs this spelled out for
     * Jackson to bind it during deserialisation. {@code matchId} isn't part of the CSV export
     * itself, but is still accepted here (typically {@code null}) so this constructor's signature
     * matches {@link MatchOverallScoresRequest}'s.
     * </p>
     *
     * @param matchId                    the internal identifier of the match this result belongs to; not part of
     *                                   the CSV export, typically {@code null} here.
     * @param name                       the competitor's full name. Must not be null or blank.
     * @param percentage                 overall match score as a percentage of the match winner's score.
     * @param points                     total weighted points earned across the match.
     * @param time                       total time, in seconds, taken across the match's stages.
     * @param percentageOfPossiblePoints total hits as a percentage of the maximum points available in the match.
     * @param hitFactor                  hit factor — raw score divided by time.
     * @param division                   division the competitor shot in.
     * @param club                       the competitor's club.
     * @param categories                 competitor categories entered.
     * @param powerFactor                major or Minor power factor.
     * @param membershipNumber           the competitor's membership number. Must not be null or blank.
     * @param alpha                      total A-zone (alpha) hits across the match.
     * @param charlie                    total C-zone (charlie) hits across the match.
     * @param delta                      total D-zone (delta) hits across the match.
     * @param misses                     total required hits not scored (misses) across the match.
     * @param noPenaltyMisses            total misses that didn't attract the usual miss penalty.
     * @param noShoots                   total no-shoot penalty hits across the match.
     * @param proceduralErrors           total procedural penalties applied across the match.
     * @param additionalPenalties        total additional penalties applied across the match.
     */
    @JsonCreator
    public MatchOverallScoresRequestForCSV(@JsonProperty("MatchId") Long matchId,
                                           @JsonProperty(value = "Name", required = true) String name,
                                           @JsonProperty("%") BigDecimal percentage,
                                           @JsonProperty("Pts") BigDecimal points,
                                           @JsonProperty("Time") BigDecimal time,
                                           @JsonProperty("%psbl") BigDecimal percentageOfPossiblePoints,
                                           @JsonProperty("HF") BigDecimal hitFactor,
                                           @JsonProperty("Div") Division division,
                                           @JsonProperty("Class") ClubIdentifier club,
                                           @JsonProperty("Cats") List<CompetitorCategory> categories,
                                           @JsonProperty("PF") PowerFactor powerFactor,
                                           @JsonProperty(value = "Mem#", required = true) String membershipNumber,
                                           @JsonProperty("A") Integer alpha,
                                           @JsonProperty("C") Integer charlie,
                                           @JsonProperty("D") Integer delta,
                                           @JsonProperty("M") Integer misses,
                                           @JsonProperty("NPM") Integer noPenaltyMisses,
                                           @JsonProperty("NS") Integer noShoots,
                                           @JsonProperty("Proc") Integer proceduralErrors,
                                           @JsonProperty("Apen") Integer additionalPenalties) {
        this.matchId = matchId;
        this.name = name;
        this.percentage = percentage;
        this.points = points;
        this.time = time;
        this.percentageOfPossiblePoints = percentageOfPossiblePoints;
        this.hitFactor = hitFactor;
        this.division = division;
        this.club = club;
        this.categories = categories;
        this.powerFactor = powerFactor;
        this.membershipNumber = membershipNumber;
        this.alpha = alpha;
        this.charlie = charlie;
        this.delta = delta;
        this.misses = misses;
        this.noPenaltyMisses = noPenaltyMisses;
        this.noShoots = noShoots;
        this.proceduralErrors = proceduralErrors;
        this.additionalPenalties = additionalPenalties;
    }
}
