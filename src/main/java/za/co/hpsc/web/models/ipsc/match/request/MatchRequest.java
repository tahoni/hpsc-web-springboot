package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Request to create or update an IPSC match together with its stages in a single call.
 *
 * <p>
 * The {@link MatchStageRequest}s that make it up have their own {@code matchId}, which is
 * typically unset when nested here for a new match, since the match doesn't exist yet.
 * </p>
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
    /** The match's name. */
    @JsonProperty(required = true)
    private String matchName;
    /** The name of the club hosting the match; resolved against existing clubs by name. */
    private String club;
    /** The firearm type this match is shot with; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    private String matchFirearmType;
    /** The category/tier of this match; resolved against {@link za.co.hpsc.web.enums.MatchCategory} by name. */
    private String matchCategory;
    /** The stages that make up this match. */
    private List<MatchStageRequest> stages;

    /**
     * Constructs a {@code MatchRequest} from its JSON representation.
     *
     * @param matchId          the identifier of the match to update; {@code null} when creating a new match.
     * @param matchDate        the date the match was/will be shot. Must not be null.
     * @param matchName        the match's name. Must not be null or blank.
     * @param club             the name of the club hosting the match; resolved against existing clubs by name.
     * @param matchFirearmType the firearm type this match is shot with; resolved against
     *                         {@link za.co.hpsc.web.enums.FirearmType} by name.
     * @param matchCategory    the category/tier of this match; resolved against
     *                         {@link za.co.hpsc.web.enums.MatchCategory} by name.
     * @param stages           the stages that make up this match.
     */
    @JsonCreator
    public MatchRequest(@JsonProperty("matchId") Long matchId,
                        @JsonProperty(value = "matchDate", required = true) LocalDate matchDate,
                        @JsonProperty(value = "matchName", required = true) String matchName,
                        @JsonProperty("club") String club,
                        @JsonProperty("matchFirearmType") String matchFirearmType,
                        @JsonProperty("matchCategory") String matchCategory,
                        @JsonProperty("stages") List<MatchStageRequest> stages) {
        this.matchId = matchId;
        this.matchDate = matchDate;
        this.matchName = matchName;
        this.club = club;
        this.matchFirearmType = matchFirearmType;
        this.matchCategory = matchCategory;
        this.stages = stages;
    }
}
