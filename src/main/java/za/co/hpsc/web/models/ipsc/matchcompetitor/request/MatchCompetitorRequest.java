package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.shared.IpscMatchScore;

import java.math.BigDecimal;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Request to create or fully replace an IPSC match competitor: one competitor's entry in one match, in one firearm
 * type.
 *
 * <p>
 * The entry to replace is identified by the ID in the request path, so this carries no {@code matchCompetitorId}.
 * </p>
 *
 * @see MatchCompetitorPatchRequest
 * @see MatchCompetitorResponse
 * @since 9.1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchCompetitorRequest extends IpscMatchScore {
    /** Identifier of the match competitor to update, or {@code null} when creating a new match competitor. */
    private Long matchCompetitorId;
    /**
     * The identifier of the competitor who shot the match. When {@code null}, the competitor is found by
     * {@link #competitorNumber}, then by {@link #competitorName}.
     */
    private Long competitorId;
    /**
     * The competitor's full name, "First Last", matched case-insensitively; only used when {@link #competitorId}
     * and {@link #competitorNumber} are both {@code null}.
     */
    @JsonProperty("name")
    private String competitorName;
    /**
     * The competitor's number, as assigned for competition (CSV {@code Mem #}), matched exactly; only used when
     * {@link #competitorId} is {@code null}.
     */
    @JsonProperty("competitorNumber")
    private String competitorNumber;
    /** The identifier of the match the competitor shot. */
    @NotNull(message = "Match ID is required.")
    @JsonProperty(required = true)
    private Long matchId;
    /** The club the competitor represented at the match; resolved against {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation. */
    private String matchClub;
    /** The competitor's category at the match; resolved against {@link za.co.hpsc.web.enums.CompetitorCategory} by name. */
    @NotBlank(message = "Competitor category is required.")
    @JsonProperty(required = true)
    private String competitorCategory;
    /**
     * The firearm type the competitor shot; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. May be
     * null or blank, in which case it is taken from the division.
     */
    private String firearmType;
    /** The division the competitor shot; resolved against {@link za.co.hpsc.web.enums.Division} by name. */
    @NotBlank(message = "Division is required.")
    @JsonProperty(required = true)
    private String division;
    /** The competitor's power factor; resolved against {@link za.co.hpsc.web.enums.PowerFactor} by name. */
    @NotBlank(message = "Power factor is required.")
    @JsonProperty(required = true)
    private String powerFactor;
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
     * @param competitorId       the identifier of the competitor who shot the match; when null, the competitor is
     *                           found by {@code competitorNumber}, then by {@code competitorName}.
     * @param competitorName     the competitor's full name, "First Last", matched case-insensitively; only used when
     *                           {@code competitorId} and {@code competitorNumber} are both null.
     * @param competitorNumber   the competitor's number, as assigned for competition, matched exactly; only used when
     *                           {@code competitorId} is null.
     * @param matchId            the identifier of the match the competitor shot. Must not be null.
     * @param matchClub          the club the competitor represented at the match; resolved against
     *                           {@link za.co.hpsc.web.enums.ClubIdentifier} by name or abbreviation.
     * @param competitorCategory the competitor's category at the match; resolved against
     *                           {@link za.co.hpsc.web.enums.CompetitorCategory} by name. Must not be null or blank.
     * @param firearmType        the firearm type the competitor shot; resolved against
     *                           {@link za.co.hpsc.web.enums.FirearmType} by name. May be null or blank, in which case
     *                           it is taken from the division.
     * @param division           the division the competitor shot; resolved against
     *                           {@link za.co.hpsc.web.enums.Division} by name. Must not be null or blank.
     * @param powerFactor        the competitor's power factor; resolved against
     *                           {@link za.co.hpsc.web.enums.PowerFactor} by name.
     * @param points             the competitor's match points.
     * @param percentage                  the competitor's overall match score as a percentage of the match winner's score.
     * @param time                        the competitor's total time, in seconds, taken across the match's stages.
     * @param percentageOfPossiblePoints  the competitor's total hits as a percentage of the maximum points available in the match.
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
                                  @JsonProperty("name") String competitorName,
                                  @JsonProperty("competitorNumber") String competitorNumber,
                                  @JsonProperty(value = "matchId", required = true) Long matchId,
                                  @JsonProperty("matchClub") String matchClub,
                                  @JsonProperty(value = "competitorCategory", required = true) String competitorCategory,
                                  @JsonProperty("firearmType") String firearmType,
                                  @JsonProperty(value = "division", required = true) String division,
                                  @JsonProperty("powerFactor") String powerFactor,
                                  @JsonProperty("points") BigDecimal points,
                                  @JsonProperty("percentage") BigDecimal percentage,
                                  @JsonProperty("time") BigDecimal time,
                                  @JsonProperty("percentageOfPossiblePoints") BigDecimal percentageOfPossiblePoints,
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
        this.competitorName = competitorName;
        this.competitorNumber = competitorNumber;
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

    /**
     * Checks that the request carries what is needed to identify the competitor and the match, before any of it is
     * resolved. Nothing is looked up, so this only checks that values are present.
     *
     * <p>
     * The request is valid when:
     * </p>
     * <ul>
     *     <li>{@code competitorId} is set, or {@code competitorNumber} or {@code competitorName} has text;</li>
     *     <li>{@code matchId} is set; and</li>
     *     <li>{@code competitorCategory}, {@code division} and {@code powerFactor} each have text; and</li>
     *     <li>the {@code division}, when it and the {@code firearmType} both name a known value, is one shot with that
     *     firearm type.</li>
     * </ul>
     *
     * <p>
     * The {@code firearmType} is optional: when it is blank the mapper takes it from the division. The category,
     * firearm type, division, club and power factor are resolved against their enums later, so a value that is present
     * but unknown is not caught here; it only skips the firearm type and division check.
     * </p>
     *
     * @throws ValidationException if the competitor ID, number and name are all missing, if the match ID,
     *                             competitor category, division or power factor is missing, or if the division does not
     *                             belong to the firearm type.
     */
    public void validate() {
        if ((getCompetitorId() == null) && !hasText(getCompetitorNumber())
                && !hasText(getCompetitorName())) {
            throw new ValidationException("Competitor ID, number or name is required.");
        }
        if (getMatchId() == null) {
            throw new ValidationException("Match ID is required.");
        }
        if (!hasText(getCompetitorCategory())) {
            throw new ValidationException("Competitor category is required.");
        }
        if (!hasText(getDivision())) {
            throw new ValidationException("Division is required.");
        }
        if (!hasText(getPowerFactor())) {
            throw new ValidationException("Power factor is required.");
        }
        validateDivisionMatchesFirearmType();
    }

    private void validateDivisionMatchesFirearmType() {
        Division division = Division.fromName(getDivision()).orElse(null);
        FirearmType firearmType = FirearmType.fromName(getFirearmType()).orElse(null);
        if ((division != null) && (firearmType != null) && (division.getFirearmType() != firearmType)) {
            throw new ValidationException("Division " + division + " is not a " + firearmType + " division.");
        }
    }
}
