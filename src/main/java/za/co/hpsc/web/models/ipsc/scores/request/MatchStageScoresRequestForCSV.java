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
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchStageScoresRequestForCSV {
    /** Internal identifier of the match this stage result belongs to; not part of the CSV export,
     * populated separately. */
    private Long matchId;
    /** The stage's number/order within the match. */
    @JsonProperty(required = true)
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
    private Division division;
    /** The competitor's club. */
    @JsonProperty("Class")
    private ClubIdentifier club;
    /** Competitor categories entered, e.g. Lady, Junior, Senior. */
    @JsonProperty("Cats")
    private List<CompetitorCategory> categories;
    /** Major or Minor power factor — determines how many raw points each alpha/charlie/delta hit is worth. */
    @JsonProperty("PF")
    private PowerFactor powerFactor;
    /** The competitor's membership number. */
    @JsonProperty(value = "Mem#", required = true)
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

    /**
     * Constructs a {@code MatchStageScoresRequestForCSV} from its Practiscore CSV/JSON row.
     *
     * <p>
     * Each parameter is bound to its Practiscore column name explicitly (matching the field's own
     * {@link JsonProperty} override, or the {@link JsonNaming} strategy's transform where none is
     * set), since a multi-argument {@code @JsonCreator} constructor needs this spelled out for
     * Jackson to bind it during deserialisation. {@code matchId} isn't part of the CSV export
     * itself, but is still accepted here (typically {@code null}) so this constructor's signature
     * matches {@link MatchStageScoresRequest}'s.
     * </p>
     *
     * @param matchId             the internal identifier of the match this stage result belongs to; not part of
     *                            the CSV export, typically {@code null} here.
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
    public MatchStageScoresRequestForCSV(@JsonProperty("MatchId") Long matchId,
                                         @JsonProperty(value = "StageNumber", required = true) Integer stageNumber,
                                         @JsonProperty(value = "Name", required = true) String name,
                                         @JsonProperty("%") BigDecimal stagePercentage,
                                         @JsonProperty("Stg Pts") BigDecimal stagePoints,
                                         @JsonProperty("Pts") Integer points,
                                         @JsonProperty("HF") BigDecimal hitFactor,
                                         @JsonProperty("Time") BigDecimal time,
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
