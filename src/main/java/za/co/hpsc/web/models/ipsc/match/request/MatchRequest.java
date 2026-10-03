package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request to create or update an IPSC match.
 *
 * @since 1.1.3
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchRequest {
    /** Identifier of the match to update; {@code null} when this request is creating a new match. */
    private Long matchId;
    /** Date the match was/will be shot. */
    @JsonProperty(required = true)
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate matchDate;
    /** Time the match started; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime startTime;
    /** Time the match ended; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_TIME_FORMAT)
    private LocalTime endTime;
    /** The match's name. */
    @JsonProperty(required = true)
    private String matchName;
    /**
     * The name of the club hosting the match; resolved against existing clubs by name. May be
     * null or blank, in which case the match defaults to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    private String club;
    /** The firearm type this match is shot with; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    @JsonProperty
    private String matchFirearmType;
    /** The category/tier of this match; resolved against {@link za.co.hpsc.web.enums.MatchCategory} by name. */
    @JsonProperty
    private String matchCategory;
    /** A URL with more information about this match (e.g. a results page or event listing); may be null. */
    private String url;

    /**
     * Constructs a {@code MatchRequest} from its JSON representation.
     *
     * @param matchId          the identifier of the match to update; {@code null} when creating a new match.
     * @param matchDate        the date the match was/will be shot. Must not be null.
     * @param matchName        the match's name. Must not be null or blank.
     * @param club             the name of the club hosting the match; resolved against existing clubs by name.
     *                         May be null or blank, in which case the match defaults to
     *                         {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @param matchFirearmType the firearm type this match is shot with; resolved against
     *                         {@link za.co.hpsc.web.enums.FirearmType} by name. Must not be null or blank.
     * @param matchCategory    the category/tier of this match; resolved against
     *                         {@link za.co.hpsc.web.enums.MatchCategory} by name. Must not be null or blank.
     * @param startTime        time the match started; may be null.
     * @param endTime          time the match ended; may be null.
     * @param url              a URL with more information about this match; may be null.
     */
    @JsonCreator
    public MatchRequest(@JsonProperty("matchId") Long matchId,
                        @JsonProperty(value = "matchDate", required = true) LocalDate matchDate,
                        @JsonProperty(value = "matchName", required = true) String matchName,
                        @JsonProperty("club") String club,
                        @JsonProperty(value = "matchFirearmType", required = true) String matchFirearmType,
                        @JsonProperty(value = "matchCategory", required = true) String matchCategory,
                        @JsonProperty(value = "startTime") LocalTime startTime,
                        @JsonProperty(value = "endTime") LocalTime endTime,
                        @JsonProperty(value = "url") String url) {
        this.matchId = matchId;
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
