package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Jackson mix-in binding {@link MatchCompetitorRequest}'s constructor to the UpperCamelCase column
 * headers used by CSV reading. It is declared with the same signature as
 * {@link MatchCompetitorRequest}'s {@code @JsonCreator}, so its annotations replace that
 * constructor's for CSV reading only. Unknown columns are ignored.
 *
 * <p>
 * A {@code MatchCompetitorId} column is bound as given.
 * </p>
 *
 * @since 9.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MatchCompetitorRequestCsvMixIn {
    @JsonCreator
    MatchCompetitorRequestCsvMixIn(@JsonProperty("MatchCompetitorId") Long matchCompetitorId,
                                   @JsonProperty("CompetitorId") Long competitorId,
                                   @JsonProperty("Name") String competitorName,
                                   @JsonProperty("Mem #") String competitorNumber,
                                   @JsonProperty(value = "MatchId", required = true) Long matchId,
                                   @JsonProperty("Class") String matchClub,
                                   @JsonProperty(value = "Cats", required = true) String competitorCategory,
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
                                   @JsonProperty("OverallRanking") BigDecimal overallRanking,
                                   @JsonProperty("ClubRanking") BigDecimal clubRanking,
                                   @JsonProperty("IsVisitor") Boolean isVisitor) {
    }
}
