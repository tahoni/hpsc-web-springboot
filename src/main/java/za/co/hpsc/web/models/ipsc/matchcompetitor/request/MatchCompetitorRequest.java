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

    /**
     * Constructs a {@code MatchCompetitorRequest} from its JSON representation.
     *
     * @param matchCompetitorId  the identifier of the match competitor to update; {@code null} when creating a new
     *                           match competitor.
     * @param competitorId       the identifier of the competitor who shot the match. Must not be null.
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
     * @param matchPoints        the competitor's match points.
     * @param overallRanking     the competitor's overall ranking in the match.
     * @param clubRanking        the competitor's ranking among their club's competitors in the match.
     * @param isVisitor          whether the competitor was a visitor at the match; stored as {@code null} when
     *                           omitted.
     */
    @JsonCreator
    public MatchCompetitorRequest(@JsonProperty("matchCompetitorId") Long matchCompetitorId,
                                  @JsonProperty(value = "competitorId", required = true) Long competitorId,
                                  @JsonProperty(value = "matchId", required = true) Long matchId,
                                  @JsonProperty("matchClub") String matchClub,
                                  @JsonProperty(value = "competitorCategory", required = true)
                                  List<String> competitorCategory,
                                  @JsonProperty("firearmType") String firearmType,
                                  @JsonProperty(value = "division", required = true) String division,
                                  @JsonProperty("powerFactor") String powerFactor,
                                  @JsonProperty("matchPoints") BigDecimal matchPoints,
                                  @JsonProperty("overallRanking") BigDecimal overallRanking,
                                  @JsonProperty("clubRanking") BigDecimal clubRanking,
                                  @JsonProperty("isVisitor") Boolean isVisitor) {
        this.matchCompetitorId = matchCompetitorId;
        this.competitorId = competitorId;
        this.matchId = matchId;
        this.matchClub = matchClub;
        this.competitorCategory = competitorCategory;
        this.firearmType = firearmType;
        this.division = division;
        this.powerFactor = powerFactor;
        this.matchPoints = matchPoints;
        this.overallRanking = overallRanking;
        this.clubRanking = clubRanking;
        this.isVisitor = isVisitor;
    }
}
