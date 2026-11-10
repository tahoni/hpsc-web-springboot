package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Jackson mix-in binding {@link MatchCompetitorRequest}'s CSV constructor to the UpperCamelCase column
 * headers used by CSV reading. It disables {@link MatchCompetitorRequest}'s {@code @JsonCreator}, which takes a
 * {@code matchCompetitorId}, and binds the constructor without one instead, for CSV reading only. Unknown columns are
 * ignored.
 *
 * <p>
 * CSV bulk import only ever creates new entries, so a row has no identifier of its own: a
 * {@code MatchCompetitorId} column is not read. The {@code CompetitorId} and {@code MatchId} columns are, as they name
 * the competitor and match the entry belongs to.
 * </p>
 *
 * @since 9.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MatchCompetitorRequestCsvMixIn {
    /** The JSON constructor, which takes a {@code matchCompetitorId}, is not used to read CSV. */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    MatchCompetitorRequestCsvMixIn(Long matchCompetitorId, Long competitorId, String competitorName,
                                   String competitorNumber, Long matchId, String matchClub,
                                   String competitorCategory, String firearmType, String division,
                                   String powerFactor, BigDecimal points, BigDecimal percentage, BigDecimal time,
                                   BigDecimal percentageOfPossiblePoints, Integer alpha, Integer charlie,
                                   Integer delta, Integer misses, Integer noPenaltyMisses, Integer noShoots,
                                   Integer proceduralErrors, Integer additionalPenalties, Boolean isVisitor) {
    }

    @JsonCreator
    MatchCompetitorRequestCsvMixIn(@JsonProperty("CompetitorId") Long competitorId,
                                   @JsonProperty("Name") String competitorName,
                                   @JsonProperty("Mem #") String competitorNumber,
                                   @JsonProperty(value = "MatchId", required = true) Long matchId,
                                   @JsonProperty("Class") String matchClub,
                                   @JsonProperty(value = "Cats") String competitorCategory,
                                   @JsonProperty("FirearmType") String firearmType,
                                   @JsonProperty(value = "Div", required = true) String division,
                                   @JsonProperty("PF") String powerFactor,
                                   @JsonProperty("Pts") BigDecimal points,
                                   @JsonProperty("%") BigDecimal percentage,
                                   @JsonProperty("Time") BigDecimal time,
                                   @JsonProperty("% psbl") BigDecimal percentageOfPossiblePoints,
                                   @JsonProperty("A") Integer alpha,
                                   @JsonProperty("C") Integer charlie,
                                   @JsonProperty("D") Integer delta,
                                   @JsonProperty("M") Integer misses,
                                   @JsonProperty("NPM") Integer noPenaltyMisses,
                                   @JsonProperty("NS") Integer noShoots,
                                   @JsonProperty("Proc") Integer proceduralErrors,
                                   @JsonProperty("Apen") Integer additionalPenalties,
                                   @JsonProperty("IsVisitor") Boolean isVisitor) {
    }
}
