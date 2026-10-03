package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Jackson mix-in binding {@link MatchCompetitorRequest}'s constructor to the UpperCamelCase column
 * headers used by CSV reading. It is declared with the same signature as
 * {@link MatchCompetitorRequest}'s {@code @JsonCreator}, so its annotations replace that
 * constructor's for CSV reading only. Unknown columns are ignored.
 *
 * <p>
 * A {@code MatchCompetitorId} column is bound as given. {@code CompetitorCategory} is a single cell of categories
 * separated by the shared array separator.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MatchCompetitorRequestCsvMixIn {
    @JsonCreator
    MatchCompetitorRequestCsvMixIn(@JsonProperty("MatchCompetitorId") Long matchCompetitorId,
                                   @JsonProperty(value = "CompetitorId", required = true) Long competitorId,
                                   @JsonProperty(value = "MatchId", required = true) Long matchId,
                                   @JsonProperty("MatchClub") String matchClub,
                                   @JsonProperty(value = "CompetitorCategory", required = true)
                                   List<String> competitorCategory,
                                   @JsonProperty("FirearmType") String firearmType,
                                   @JsonProperty(value = "Division", required = true) String division,
                                   @JsonProperty("PowerFactor") String powerFactor,
                                   @JsonProperty("Points") BigDecimal points,
                                   @JsonProperty("OverallRanking") BigDecimal overallRanking,
                                   @JsonProperty("ClubRanking") BigDecimal clubRanking,
                                   @JsonProperty("IsVisitor") Boolean isVisitor) {
    }
}
