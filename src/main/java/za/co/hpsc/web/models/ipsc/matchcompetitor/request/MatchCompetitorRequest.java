package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

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
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchCompetitorRequest {
    /** The identifier of the competitor who shot the match. */
    @JsonProperty(required = true)
    private Long competitorId;
    /** The identifier of the match the competitor shot. */
    @JsonProperty(required = true)
    private Long matchId;
    /** The club the competitor represented at the match; resolved against {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation. */
    private String matchClub;
    /** The competitor's categories at the match, at least one; each resolved against {@link za.co.hpsc.web.enums.CompetitorCategory} by name. */
    @JsonProperty(required = true)
    private List<String> competitorCategory;
    /** The firearm type the competitor shot; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    @JsonProperty(required = true)
    private String firearmType;
    /** The division the competitor shot; resolved against {@link za.co.hpsc.web.enums.Division} by name. */
    @JsonProperty(required = true)
    private String division;
    /** The competitor's power factor; resolved against {@link za.co.hpsc.web.enums.PowerFactor} by name. */
    private String powerFactor;
    /** The competitor's match points. */
    private BigDecimal matchPoints;
    /** The competitor's overall ranking in the match. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match; stored as {@code null} when omitted. */
    private Boolean isVisitor;
}
