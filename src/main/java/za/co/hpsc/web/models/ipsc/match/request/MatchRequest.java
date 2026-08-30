package za.co.hpsc.web.models.ipsc.match.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;

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
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequest {
    /** Identifier of the match to update; {@code null} when this request is creating a new match. */
    private Long matchId;
    /** Date the match was/will be shot. */
    private LocalDate matchDate;
    /** The match's name. */
    private String matchName;
    /** The name of the club hosting the match; resolved against existing clubs by name. */
    private String club;
    /** The firearm type this match is shot with. */
    private FirearmType matchFirearmType;
    /** The category/tier of this match. */
    private MatchCategory matchCategory;
    /** The stages that make up this match. */
    private List<MatchStageRequest> stages;
}
