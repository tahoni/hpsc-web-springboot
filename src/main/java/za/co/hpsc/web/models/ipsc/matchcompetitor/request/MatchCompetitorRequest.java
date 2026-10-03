package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request to create or fully replace an IPSC match competitor: one competitor's entry in one match, in one firearm
 * type.
 *
 * <p>
 * The entry to replace is identified by the ID in the request path, so this carries no {@code matchCompetitorId}.
 * </p>
 *
 * @see MatchCompetitorPatchRequest
 * @see za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse
 * @since 9.1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchCompetitorRequest {
    /** Identifier of the match competitor to update, or {@code null} when creating a new match competitor. */
    private Long matchCompetitorId;
    /** The identifier of the competitor who shot the match; when {@code null}, {@link #name} is used to find the competitor. */
    private Long competitorId;
    /** The competitor's full name, "First Last", matched case-insensitively; only used when {@link #competitorId} is {@code null}. */
    private String name;
    /** The identifier of the match the competitor shot. */
    @JsonProperty(required = true)
    private Long matchId;
    /** The club the competitor represented at the match; resolved against {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation. */
    private String matchClub;
    /** The competitor's categories at the match, at least one; each resolved against {@link za.co.hpsc.web.enums.CompetitorCategory} by name. */
    @JsonProperty(required = true)
    private List<String> competitorCategory;
    /** The firearm type the competitor shot; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    private String firearmType;
    /** The division the competitor shot; resolved against {@link za.co.hpsc.web.enums.Division} by name. */
    @JsonProperty(required = true)
    private String division;
    /** The competitor's power factor; resolved against {@link za.co.hpsc.web.enums.PowerFactor} by name. */
    private String powerFactor;
    /** The competitor's match points. */
    private BigDecimal points;
    /** The competitor's overall match score as a percentage of the match winner's score. */
    private BigDecimal percentage;
    /** The competitor's total time, in seconds, taken across the match's stages. */
    private BigDecimal time;
    /** The competitor's total hits as a percentage of the maximum points available in the match. */
    private BigDecimal percentageOfPossiblePoints;
    /** The competitor's hit factor — raw score divided by time. */
    private BigDecimal hitFactor;
    /** The competitor's total A-zone (alpha) hits across the match. */
    private Integer alpha;
    /** The competitor's total C-zone (charlie) hits across the match. */
    private Integer charlie;
    /** The competitor's total D-zone (delta) hits across the match. */
    private Integer delta;
    /** The competitor's total required hits not scored (misses) across the match. */
    private Integer misses;
    /** The competitor's total misses that did not attract the usual miss penalty. */
    private Integer noPenaltyMisses;
    /** The competitor's total no-shoot penalty hits across the match. */
    private Integer noShoots;
    /** The competitor's total procedural penalties applied across the match. */
    private Integer proceduralErrors;
    /** The competitor's total additional penalties applied across the match. */
    private Integer additionalPenalties;
    /** The competitor's overall ranking in the match. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match; stored as {@code null} when omitted. */
    private Boolean isVisitor;

    /**
     * Constructs a {@code MatchCompetitorRequest} from its JSON representation.
     *
     * @param matchCompetitorId  the identifier of the match competitor to update; {@code null} when creating a new
     *                           match competitor.
     * @param competitorId       the identifier of the competitor who shot the match; when null, {@code name} is used
     *                           to find the competitor.
     * @param name               the competitor's full name, "First Last", matched case-insensitively; only used
     *                           when {@code competitorId} is null.
     * @param matchId            the identifier of the match the competitor shot. Must not be null.
     * @param matchClub          the club the competitor represented at the match; resolved against
     *                           {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation.
     * @param competitorCategory the competitor's categories at the match, at least one; each resolved against
     *                           {@link za.co.hpsc.web.enums.CompetitorCategory} by name. Must not be null.
     * @param firearmType        the firearm type the competitor shot; resolved against
     *                           {@link za.co.hpsc.web.enums.FirearmType} by name.
     * @param division           the division the competitor shot; resolved against
     *                           {@link za.co.hpsc.web.enums.Division} by name. Must not be null or blank.
     * @param powerFactor        the competitor's power factor; resolved against
     *                           {@link za.co.hpsc.web.enums.PowerFactor} by name.
     * @param points             the competitor's match points.
     * @param percentage                  the competitor's overall match score as a percentage of the match winner's score.
     * @param time                        the competitor's total time, in seconds, taken across the match's stages.
     * @param percentageOfPossiblePoints  the competitor's total hits as a percentage of the maximum points available in the match.
     * @param hitFactor                   the competitor's hit factor — raw score divided by time.
     * @param alpha                       the competitor's total A-zone (alpha) hits across the match.
     * @param charlie                     the competitor's total C-zone (charlie) hits across the match.
     * @param delta                       the competitor's total D-zone (delta) hits across the match.
     * @param misses                      the competitor's total required hits not scored (misses) across the match.
     * @param noPenaltyMisses             the competitor's total misses that did not attract the usual miss penalty.
     * @param noShoots                    the competitor's total no-shoot penalty hits across the match.
     * @param proceduralErrors            the competitor's total procedural penalties applied across the match.
     * @param additionalPenalties         the competitor's total additional penalties applied across the match.
     * @param overallRanking     the competitor's overall ranking in the match.
     * @param clubRanking        the competitor's ranking among their club's competitors in the match.
     * @param isVisitor          whether the competitor was a visitor at the match; stored as {@code null} when
     *                           omitted.
     */
    @JsonCreator
    public MatchCompetitorRequest(@JsonProperty("matchCompetitorId") Long matchCompetitorId,
                                  @JsonProperty("competitorId") Long competitorId,
                                  @JsonProperty("name") String name,
                                  @JsonProperty(value = "matchId", required = true) Long matchId,
                                  @JsonProperty("matchClub") String matchClub,
                                  @JsonProperty(value = "competitorCategory", required = true)
                                  List<String> competitorCategory,
                                  @JsonProperty("firearmType") String firearmType,
                                  @JsonProperty(value = "division", required = true) String division,
                                  @JsonProperty("powerFactor") String powerFactor,
                                  @JsonProperty("points") BigDecimal points,
                                  @JsonProperty("percentage") BigDecimal percentage,
                                  @JsonProperty("time") BigDecimal time,
                                  @JsonProperty("percentageOfPossiblePoints") BigDecimal percentageOfPossiblePoints,
                                  @JsonProperty("hitFactor") BigDecimal hitFactor,
                                  @JsonProperty("alpha") Integer alpha,
                                  @JsonProperty("charlie") Integer charlie,
                                  @JsonProperty("delta") Integer delta,
                                  @JsonProperty("misses") Integer misses,
                                  @JsonProperty("noPenaltyMisses") Integer noPenaltyMisses,
                                  @JsonProperty("noShoots") Integer noShoots,
                                  @JsonProperty("proceduralErrors") Integer proceduralErrors,
                                  @JsonProperty("additionalPenalties") Integer additionalPenalties,
                                  @JsonProperty("overallRanking") BigDecimal overallRanking,
                                  @JsonProperty("clubRanking") BigDecimal clubRanking,
                                  @JsonProperty("isVisitor") Boolean isVisitor) {
        this.matchCompetitorId = matchCompetitorId;
        this.competitorId = competitorId;
        this.name = name;
        this.matchId = matchId;
        this.matchClub = matchClub;
        this.competitorCategory = competitorCategory;
        this.firearmType = firearmType;
        this.division = division;
        this.powerFactor = powerFactor;
        this.points = points;
        this.percentage = percentage;
        this.time = time;
        this.percentageOfPossiblePoints = percentageOfPossiblePoints;
        this.hitFactor = hitFactor;
        this.alpha = alpha;
        this.charlie = charlie;
        this.delta = delta;
        this.misses = misses;
        this.noPenaltyMisses = noPenaltyMisses;
        this.noShoots = noShoots;
        this.proceduralErrors = proceduralErrors;
        this.additionalPenalties = additionalPenalties;
        this.overallRanking = overallRanking;
        this.clubRanking = clubRanking;
        this.isVisitor = isVisitor;
    }
}
