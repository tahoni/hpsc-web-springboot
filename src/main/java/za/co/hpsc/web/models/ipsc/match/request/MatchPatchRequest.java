package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request to partially update an existing IPSC match.
 *
 * <p>
 * The match to update is identified by the ID in the request path, so no field here is required: any field left
 * {@code null} is left unchanged on the match. Unlike {@link MatchRequest}, which creates or replaces a match in full,
 * this carries no {@code matchId}.
 * </p>
 *
 * @see MatchRequest
 * @since 9.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchPatchRequest {
    /** Date the match was/will be shot; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate matchDate;
    /** Time the match started; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime startTime;
    /** Time the match ended; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime endTime;
    /** The match's name; may be null. */
    private String matchName;
    /** The name of the club hosting the match; resolved against existing clubs by name. May be null. */
    private String club;
    /** The firearm type this match is shot with; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    private String matchFirearmType;
    /** The category/tier of this match; resolved against {@link za.co.hpsc.web.enums.MatchCategory} by name. */
    private String matchCategory;
    /** A URL with more information about this match (e.g. a results page or event listing); may be null. */
    private String url;
}
