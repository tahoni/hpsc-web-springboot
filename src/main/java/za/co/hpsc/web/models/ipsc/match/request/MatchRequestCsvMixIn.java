package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Jackson mix-in binding {@link MatchRequest}'s CSV constructor to the UpperCamelCase column
 * headers used by the bulk CSV import. It disables {@link MatchRequest}'s {@code @JsonCreator}, which takes a
 * {@code matchId}, and binds the constructor without one instead, for CSV reading only. Unknown columns are ignored.
 *
 * <p>
 * CSV bulk import only ever creates new matches, so a row has no identifier of its own: a {@code MatchId} column is
 * not read.
 * </p>
 *
 * @since 9.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MatchRequestCsvMixIn {
    /** The JSON constructor, which takes a {@code matchId}, is not used to read CSV. */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    MatchRequestCsvMixIn(Long matchId, LocalDate matchDate, String matchName, String club, String matchFirearmType,
                         String matchCategory, LocalTime startTime, LocalTime endTime, String url) {
    }

    @JsonCreator
    MatchRequestCsvMixIn(@JsonProperty(value = "MatchDate", required = true)
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
