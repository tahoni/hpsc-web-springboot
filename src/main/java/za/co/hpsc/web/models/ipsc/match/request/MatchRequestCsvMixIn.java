package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Jackson mix-in binding {@link MatchRequest}'s constructor to the UpperCamelCase column
 * headers used by the bulk CSV import. It is declared with the same signature as
 * {@link MatchRequest}'s {@code @JsonCreator}, so its annotations replace that constructor's
 * for CSV reading only. Unknown columns are ignored.
 *
 * <p>
 * CSV bulk import only ever creates new matches, so a {@code MatchId} column is bound but never used.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MatchRequestCsvMixIn {
    @JsonCreator
    MatchRequestCsvMixIn(@JsonProperty("MatchId") Long matchId,
                         @JsonProperty(value = "MatchDate", required = true)
                         @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT) LocalDate matchDate,
                         @JsonProperty(value = "MatchName", required = true) String matchName,
                         @JsonProperty("Club") String club,
                         @JsonProperty("MatchFirearmType") String matchFirearmType,
                         @JsonProperty("MatchCategory") String matchCategory,
                         @JsonProperty("StartTime") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
                         LocalTime startTime,
                         @JsonProperty("EndTime") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
                         LocalTime endTime,
                         @JsonProperty("Url") String url) {
    }
}
