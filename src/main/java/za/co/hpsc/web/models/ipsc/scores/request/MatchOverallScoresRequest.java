package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * All competitors' overall match results — the plain, non-CSV counterpart of
 * {@link MatchOverallScoresRequestForCSV}, carrying the same data without the Practiscore
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
 *
 * @since 7.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchOverallScoresRequest {
    /** Internal identifier of the match this result belongs to. */
    @JsonProperty(required = true)
    private Long matchId;
    /** The competitor's full name. */
    @JsonProperty(required = true)
    private String name;
    /** Overall match score as a percentage of the match winner's score (winner = 100%). */
    private BigDecimal percentage;
    /** Total weighted points earned across the match — the sum of the competitor's
     * per-stage points. */
    private BigDecimal points;
    /** Total time, in seconds, taken across the match's stages. */
    private BigDecimal time;
    /** Total hits (weighted by the power factor less penalties) as a percentage of the maximum
     * points available in the match — accuracy only, independent of time. */
    private BigDecimal percentageOfPossiblePoints;
    /** Hit factor — raw score divided by time, reported for overall results as an aggregate
     * across the match. */
    private BigDecimal hitFactor;
    /** Division the competitor shot in, e.g. Open, Standard, Production. */
    private Division division;
    /** The competitor's club. */
    private ClubIdentifier club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior. */
    private List<CompetitorCategory> categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta
     * hit is worth. */
    private PowerFactor powerFactor;
    /** The competitor's membership number. */
    @JsonProperty(required = true)
    private String membershipNumber;
    /** Total A-zone (alpha) hits across the match — the highest-value scoring zone. */
    private Integer alpha;
    /** Total C-zone (charlie) hits across the match — the mid-value scoring zone. */
    private Integer charlie;
    /** Total D-zone (delta) hits across the match — the lowest-value scoring zone. */
    private Integer delta;
    /** Total required hits not scored (misses) across the match; each deducts points
     * from the raw score. */
    private Integer misses;
    /** Total misses that, under the applicable rules, did not attract the usual miss penalty. */
    private Integer noPenaltyMisses;
    /** Total no-shoot penalty hits across the match; each deducts points from the raw score. */
    private Integer noShoots;
    /** Total procedural penalties (rule infractions) applied across the match. */
    private Integer proceduralErrors;
    /** Total additional penalties (e.g. safety, range command) applied across the match,
     * on top of scoring/procedurals. */
    private Integer additionalPenalties;

    /**
     * Constructs a {@code MatchOverallScoresRequest} from its JSON representation.
     *
     * @param matchId                    the internal identifier of the match this result belongs to. Must not be null.
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
    public MatchOverallScoresRequest(@JsonProperty(value = "matchId", required = true) Long matchId,
                                     @JsonProperty(value = "name", required = true) String name,
                                     @JsonProperty("percentage") BigDecimal percentage,
                                     @JsonProperty("points") BigDecimal points,
                                     @JsonProperty("time") BigDecimal time,
                                     @JsonProperty("percentageOfPossiblePoints") BigDecimal percentageOfPossiblePoints,
                                     @JsonProperty("hitFactor") BigDecimal hitFactor,
                                     @JsonProperty("division") Division division,
                                     @JsonProperty("club") ClubIdentifier club,
                                     @JsonProperty("categories") List<CompetitorCategory> categories,
                                     @JsonProperty("powerFactor") PowerFactor powerFactor,
                                     @JsonProperty(value = "membershipNumber", required = true) String membershipNumber,
                                     @JsonProperty("alpha") Integer alpha,
                                     @JsonProperty("charlie") Integer charlie,
                                     @JsonProperty("delta") Integer delta,
                                     @JsonProperty("misses") Integer misses,
                                     @JsonProperty("noPenaltyMisses") Integer noPenaltyMisses,
                                     @JsonProperty("noShoots") Integer noShoots,
                                     @JsonProperty("proceduralErrors") Integer proceduralErrors,
                                     @JsonProperty("additionalPenalties") Integer additionalPenalties) {
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
