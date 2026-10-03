package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;

/**
 * A persisted IPSC match competitor, as returned by {@code MatchCompetitorController}'s CRUD endpoints.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCompetitorResponse {
    /** The match competitor's own identifier. */
    @NotNull
    private Long matchCompetitorId;
    /** The identifier of the competitor who shot the match. */
    @NotNull
    private Long competitorId;
    /** The identifier of the match the competitor shot. */
    @NotNull
    private Long matchId;
    /** The club the competitor represented at the match, if any. */
    private ClubIdentifier matchClub;
    /** The competitor's category at the match. */
    @NotNull
    private CompetitorCategory competitorCategory;
    /** The firearm type the competitor shot. */
    @NotNull
    private FirearmType firearmType;
    /** The division the competitor shot. */
    @NotNull
    private Division division;
    /** The competitor's power factor, if any. */
    private PowerFactor powerFactor;
    /** The competitor's match points, if any. */
    private BigDecimal points;
    /** The competitor's overall match score as a percentage of the match winner's score, if any. */
    private BigDecimal percentage;
    /** The competitor's total time, in seconds, taken across the match's stages, if any. */
    private BigDecimal time;
    /** The competitor's total hits as a percentage of the maximum points available in the match, if any. */
    private BigDecimal percentageOfPossiblePoints;
    /** The competitor's total A-zone (alpha) hits across the match, if any. */
    private Integer alpha;
    /** The competitor's total C-zone (charlie) hits across the match, if any. */
    private Integer charlie;
    /** The competitor's total D-zone (delta) hits across the match, if any. */
    private Integer delta;
    /** The competitor's total required hits not scored (misses) across the match, if any. */
    private Integer misses;
    /** The competitor's total misses that did not attract the usual miss penalty, if any. */
    private Integer noPenaltyMisses;
    /** The competitor's total no-shoot penalty hits across the match, if any. */
    private Integer noShoots;
    /** The competitor's total procedural penalties applied across the match, if any. */
    private Integer proceduralErrors;
    /** The competitor's total additional penalties applied across the match, if any. */
    private Integer additionalPenalties;
    /** The competitor's overall ranking in the match, if any. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match, if any. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match, if recorded. */
    private Boolean isVisitor;
}
