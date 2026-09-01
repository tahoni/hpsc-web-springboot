package za.co.hpsc.web.models.ipsc.match.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;

import java.time.LocalDate;
import java.util.List;

/**
 * A persisted IPSC match together with its stages, as returned by
 * {@code IpscMatchController}'s CRUD endpoints.
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
    /** The identifier of the club hosting the match. */
    private ClubIdentifier club;
    /** The firearm type this match is shot with. */
    private FirearmType matchFirearmType;
    /** The category/tier of this match. */
    private MatchCategory matchCategory;
    /** The stages that make up this match, ordered by stage number. */
    private List<MatchStageResponse> stages;
}
