package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Jackson mix-in binding {@link ShooterLogBulkRequest}'s constructor to the UpperCamelCase column headers used by CSV
 * reading. Unknown columns are ignored.
 *
 * <p>
 * No column is marked required, so a row without a {@code ShooterLogId}, or without a date a replacement needs, is
 * reported on its own rather than failing the whole file. {@code MatchIds} holds the identifiers of the matches the log
 * covers, and {@code AddMatchIds} and {@code RemoveMatchIds} those a partial update adds and removes, each separated
 * by {@link za.co.hpsc.web.constants.SystemConstants#ARRAY_SEPARATOR}.
 * </p>
 *
 * @since 16.4.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ShooterLogBulkRequestCsvMixIn {
    @JsonCreator
    ShooterLogBulkRequestCsvMixIn(
            @JsonProperty("ShooterLogId") Long shooterLogId,
            @JsonProperty("StartDate") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT) LocalDate startDate,
            @JsonProperty("EndDate") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT) LocalDate endDate,
            @JsonProperty("MatchIds") List<Long> matchIds,
            @JsonProperty("AddMatchIds") List<Long> addMatchIds,
            @JsonProperty("RemoveMatchIds") List<Long> removeMatchIds) {
    }
}
