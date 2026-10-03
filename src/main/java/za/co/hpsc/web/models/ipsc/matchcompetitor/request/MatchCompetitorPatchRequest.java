package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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
    /** The identifier of the competitor who shot the match; may be null. */
    private Long competitorId;
    /** The identifier of the match the competitor shot; may be null. */
    private Long matchId;
    /** The club the competitor represented at the match; resolved against {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation. May be null. */
    private String matchClub;
    /** The competitor's categories at the match, replacing any existing ones; each resolved against {@link za.co.hpsc.web.enums.CompetitorCategory} by name. May be null, but not empty. */
    private List<String> competitorCategory;
    /** The firearm type the competitor shot; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. May be null. */
    private String firearmType;
    /** The division the competitor shot; resolved against {@link za.co.hpsc.web.enums.Division} by name. May be null. */
    private String division;
    /** The competitor's power factor; resolved against {@link za.co.hpsc.web.enums.PowerFactor} by name. May be null. */
    private String powerFactor;
    /** The competitor's match points; may be null. */
    private BigDecimal matchPoints;
    /** The competitor's overall ranking in the match; may be null. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match; may be null. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match; may be null. */
    private Boolean isVisitor;
}
