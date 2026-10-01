package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request model for bulk-importing IPSC matches from CSV data.
 *
 * <p>
 * Mirrors {@link MatchRequest}'s fields, other than {@code matchId} — CSV bulk import only ever
 * creates new matches, so no identifier is accepted. Column headers are matched using
 * {@link PropertyNamingStrategies.UpperCamelCaseStrategy}, so a CSV header of {@code MatchName}
 * maps onto the {@code matchName} field, and so on.
 * </p>
 *
 * @see MatchRequest
 * @since 8.3.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class MatchRequestCsv {
    /**
     * Date the match was/will be shot.
     */
    @JsonProperty(required = true)
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate matchDate;
    /**
     * Time the match started; may be null.
     */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime startTime;
    /**
     * Time the match ended; may be null.
     */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime endTime;
    /**
     * The match's name.
     */
    @JsonProperty(required = true)
    private String matchName;
    /**
     * The name of the club hosting the match; resolved against existing clubs by name. May be
     * null or blank, in which case the match defaults to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    private String club;
    /**
     * The firearm type this match is shot with; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name.
     */
    private String matchFirearmType;
    /**
     * The category/tier of this match; resolved against {@link za.co.hpsc.web.enums.MatchCategory} by name.
     */
    private String matchCategory;
    /**
     * A URL with more information about this match (e.g. a results page or event listing); may
     * be null.
     */
    private String url;

    /**
     * Constructs a {@code MatchRequestCsv} from its CSV/JSON representation.
     *
     * <p>
     * Each parameter is bound to its {@link PropertyNamingStrategies.UpperCamelCaseStrategy}
     * column/property name explicitly, since {@code @JsonNaming} alone only governs
     * serialisation — a multi-argument {@code @JsonCreator} constructor needs each parameter's
     * name spelled out for Jackson to bind it during deserialisation.
     * </p>
     *
     * @param matchDate        the date the match was/will be shot. Must not be null.
     * @param matchName        the match's name. Must not be null or blank.
     * @param club             the name of the club hosting the match; resolved against existing clubs by name.
     *                         May be null or blank, in which case the match defaults to
     *                         {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @param matchFirearmType the firearm type this match is shot with; resolved against
     *                         {@link za.co.hpsc.web.enums.FirearmType} by name.
     * @param matchCategory    the category/tier of this match; resolved against
     *                         {@link za.co.hpsc.web.enums.MatchCategory} by name.
     * @param startTime        time the match started; may be null.
     * @param endTime          time the match ended; may be null.
     * @param url              a URL with more information about this match; may be null.
     */
    @JsonCreator
    public MatchRequestCsv(@JsonProperty(value = "MatchDate", required = true) LocalDate matchDate,
                           @JsonProperty(value = "MatchName", required = true) String matchName,
                           @JsonProperty("Club") String club,
                           @JsonProperty("MatchFirearmType") String matchFirearmType,
                           @JsonProperty("MatchCategory") String matchCategory,
                           @JsonProperty(value = "StartTime") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT) LocalTime startTime,
                           @JsonProperty(value = "EndTime") @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT) LocalTime endTime,
                           @JsonProperty(value = "Url") String url) {
        this.matchDate = matchDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.matchName = matchName;
        this.club = club;
        this.matchFirearmType = matchFirearmType;
        this.matchCategory = matchCategory;
        this.url = url;
    }
}
