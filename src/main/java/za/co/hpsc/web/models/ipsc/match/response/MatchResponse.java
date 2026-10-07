package za.co.hpsc.web.models.ipsc.match.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A persisted IPSC match, as returned by {@code IpscMatchController}'s CRUD endpoints.
 *
 * <p>
 * Built from an {@link IpscMatch} via {@link #MatchResponse(IpscMatch)}, which flattens the match's club to its
 * {@link ClubIdentifier} and its scheduled date-time to a date, so no lazily loaded entity leaves the service layer.
 * {@code matchId}, {@code matchName} and {@code matchDate} are always present; every other field may be null.
 * </p>
 *
 * @since 8.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {
    /** The match's own identifier. */
    @NotNull
    private Long matchId;
    /** The match's name. */
    @NotNull
    private String matchName;
    /** Date the match was/will be shot. */
    @NotNull
    private LocalDate matchDate;
    /** Time the match started; may be null. */
    private LocalTime startTime;
    /** Time the match ended; may be null. */
    private LocalTime endTime;
    /** The identifier of the club hosting the match; null if the match has no club. */
    private ClubIdentifier club;
    /** The firearm type this match is shot with; may be null. */
    private FirearmType matchFirearmType;
    /** The category/tier of this match; may be null. */
    private MatchCategory matchCategory;
    /** A URL with more information about this match (e.g. a results page or event listing); may be null. */
    private String url;

    /**
     * Creates a response from a persisted match.
     *
     * @param match the match to convert.
     *              Must not be null and must have a scheduled date.
     *              The lazily loaded club is read here, so call this while the persistence session is still open.
     */
    public MatchResponse(@NotNull IpscMatch match) {
        this.matchId = match.getId();
        this.matchName = match.getName();

        this.matchDate = match.getScheduledDate().toLocalDate();
        this.startTime = match.getStartTime();
        this.endTime = match.getEndTime();

        this.club = ((match.getClub() != null) ? match.getClub().getIdentifier() : null);
        this.matchFirearmType = match.getMatchFirearmType();
        this.matchCategory = match.getMatchCategory();

        this.url = match.getUrl();
    }
}
