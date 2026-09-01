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
 * All competitors' results on a single match stage — the plain, non-CSV counterpart of
 * {@link MatchStageScoresRequestForCSV}, carrying the same data without the Practiscore
 * column-mapping annotations.
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
@NoArgsConstructor
public class MatchStageScoresRequest {
    /** Internal identifier of the match this stage result belongs to. */
    @JsonProperty(required = true)
    private Long matchId;
    /** The stage's number/order within the match. */
    @JsonProperty(required = true)
    private Integer stageNumber;
    /** The competitor's full name. */
    @JsonProperty(required = true)
    private String name;
    /** This stage's score as a percentage of the stage winner's hit factor (winner = 100%). */
    private BigDecimal stagePercentage;
    /** Weighted score points earned on this stage — the stage's raw {@link #points}
     * scaled by {@link #stagePercentage}. */
    private BigDecimal stagePoints;
    /** Raw score points on this stage, before weighting against the stage winner. */
    private Integer points;
    /** Hit factor for this stage — raw score divided by {@link #time}, the figure the stage is ranked on. */
    private BigDecimal hitFactor;
    /** Time, in seconds, taken to complete this stage. */
    private BigDecimal time;
    /** Division the competitor shot in, e.g. Open, Standard, Production. */
    private Division division;
    /** The competitor's club. */
    private ClubIdentifier club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior. */
    private List<CompetitorCategory> categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta hit is worth. */
    private PowerFactor powerFactor;
    /** The competitor's membership number. */
    @JsonProperty(required = true)
    private String membershipNumber;
    /** A-zone (alpha) hits on this stage — the highest-value scoring zone. */
    private Integer alpha;
    /** C-zone (charlie) hits on this stage — the mid-value scoring zone. */
    private Integer charlie;
    /** D-zone (delta) hits on this stage — the lowest-value scoring zone. */
    private Integer delta;
    /** Required hits not scored (misses) on this stage; each deducts points from the raw score. */
    private Integer misses;
    /** Misses that, under the applicable rules, did not attract the usual miss penalty. */
    private Integer noPenaltyMisses;
    /** No-shoot penalty hits on this stage; each deducts points from the raw score. */
    private Integer noShoots;
    /** Procedural penalties (rule infractions) applied on this stage. */
    private Integer proceduralErrors;
    /** Additional penalties (e.g. safety, range command) applied on this stage, on top of scoring/procedurals. */
    private Integer additionalPenalties;

    /**
     * Constructs a {@code MatchStageScoresRequest} from its JSON representation.
     *
     * @param matchId             the internal identifier of the match this stage result belongs to. Must not be null.
     * @param stageNumber         the stage's number/order within the match. Must not be null.
     * @param name                the competitor's full name. Must not be null or blank.
     * @param stagePercentage     this stage's score as a percentage of the stage winner's hit factor.
     * @param stagePoints         weighted score points earned on this stage.
     * @param points              raw score points on this stage, before weighting against the stage winner.
     * @param hitFactor           hit factor for this stage — raw score divided by time.
     * @param time                time, in seconds, taken to complete this stage.
     * @param division            division the competitor shot in.
     * @param club                the competitor's club.
     * @param categories          competitor categories entered.
     * @param powerFactor         major or Minor power factor.
     * @param membershipNumber    the competitor's membership number. Must not be null or blank.
     * @param alpha               A-zone (alpha) hits on this stage.
     * @param charlie             C-zone (charlie) hits on this stage.
     * @param delta               D-zone (delta) hits on this stage.
     * @param misses              required hits not scored (misses) on this stage.
     * @param noPenaltyMisses     misses that didn't attract the usual miss penalty.
     * @param noShoots            no-shoot penalty hits on this stage.
     * @param proceduralErrors    procedural penalties applied on this stage.
     * @param additionalPenalties additional penalties applied on this stage.
     */
    @JsonCreator
    public MatchStageScoresRequest(@JsonProperty(value = "matchId", required = true) Long matchId,
                                   @JsonProperty(value = "stageNumber", required = true) Integer stageNumber,
                                   @JsonProperty(value = "name", required = true) String name,
                                   @JsonProperty("stagePercentage") BigDecimal stagePercentage,
                                   @JsonProperty("stagePoints") BigDecimal stagePoints,
                                   @JsonProperty("points") Integer points,
                                   @JsonProperty("hitFactor") BigDecimal hitFactor,
                                   @JsonProperty("time") BigDecimal time,
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
        this.stageNumber = stageNumber;
        this.name = name;
        this.stagePercentage = stagePercentage;
        this.stagePoints = stagePoints;
        this.points = points;
        this.hitFactor = hitFactor;
        this.time = time;
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
