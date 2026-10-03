package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request to partially update an existing IPSC match competitor.
 *
 * <p>
 * The entry to update is identified by the ID in the request path, so no field here is required: any field left
 * {@code null} is left unchanged. Unlike {@link MatchCompetitorRequest}, which creates or replaces an entry in full,
 * this cannot clear an optional field back to {@code null}.
 * </p>
 *
 * @see MatchCompetitorRequest
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchCompetitorPatchRequest {
    /**
     * The identifier of the competitor who shot the match; may be null. When all of {@link #competitorId},
     * {@link #competitorNumber} and {@link #competitorName} are null, the competitor is left unchanged.
     */
    private Long competitorId;
    /**
     * The competitor's full name, "First Last", matched case-insensitively; only used when {@link #competitorId}
     * and {@link #competitorNumber} are both null; may be null.
     */
    @JsonProperty("name")
    private String competitorName;
    /**
     * The competitor's number, as assigned for competition, matched exactly; only used when {@link #competitorId}
     * is null; may be null.
     */
    @JsonProperty("competitorNumber")
    private String competitorNumber;
    /** The identifier of the match the competitor shot; may be null. */
    private Long matchId;
    /** The club the competitor represented at the match; resolved against {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation. May be null. */
    private String matchClub;
    /** The competitor's category at the match; resolved against {@link za.co.hpsc.web.enums.CompetitorCategory} by name. May be null. */
    private String competitorCategory;
    /** The firearm type the competitor shot; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. May be null. */
    private String firearmType;
    /** The division the competitor shot; resolved against {@link za.co.hpsc.web.enums.Division} by name. May be null. */
    private String division;
    /** The competitor's power factor; resolved against {@link za.co.hpsc.web.enums.PowerFactor} by name. May be null. */
    private String powerFactor;
    /** The competitor's match points; may be null. */
    private BigDecimal points;
    /** The competitor's overall match score as a percentage of the match winner's score; may be null. */
    private BigDecimal percentage;
    /** The competitor's total time, in seconds, taken across the match's stages; may be null. */
    private BigDecimal time;
    /** The competitor's total hits as a percentage of the maximum points available in the match; may be null. */
    private BigDecimal percentageOfPossiblePoints;
    /** The competitor's total A-zone (alpha) hits across the match; may be null. */
    private Integer alpha;
    /** The competitor's total C-zone (charlie) hits across the match; may be null. */
    private Integer charlie;
    /** The competitor's total D-zone (delta) hits across the match; may be null. */
    private Integer delta;
    /** The competitor's total required hits not scored (misses) across the match; may be null. */
    private Integer misses;
    /** The competitor's total misses that did not attract the usual miss penalty; may be null. */
    private Integer noPenaltyMisses;
    /** The competitor's total no-shoot penalty hits across the match; may be null. */
    private Integer noShoots;
    /** The competitor's total procedural penalties applied across the match; may be null. */
    private Integer proceduralErrors;
    /** The competitor's total additional penalties applied across the match; may be null. */
    private Integer additionalPenalties;
    /** The competitor's overall ranking in the match; may be null. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match; may be null. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match; may be null. */
    private Boolean isVisitor;
}
