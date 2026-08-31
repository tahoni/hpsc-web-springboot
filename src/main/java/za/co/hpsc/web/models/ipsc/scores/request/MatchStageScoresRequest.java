package za.co.hpsc.web.models.ipsc.scores.request;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class MatchStageScoresRequest {
    /** Internal identifier of the match this stage result belongs to. */
    private Long matchId;
    /** The stage's number/order within the match. */
    private Integer stageNumber;
    /** The competitor's full name. */
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
}
