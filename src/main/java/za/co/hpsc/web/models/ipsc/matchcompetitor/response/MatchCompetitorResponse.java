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
import java.util.List;

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
    /** The competitor's categories at the match. */
    @NotNull
    private List<CompetitorCategory> competitorCategory;
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
    /** The competitor's overall ranking in the match, if any. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match, if any. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match, if recorded. */
    private Boolean isVisitor;
}
