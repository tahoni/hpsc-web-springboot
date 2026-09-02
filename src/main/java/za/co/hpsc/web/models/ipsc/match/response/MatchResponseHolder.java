package za.co.hpsc.web.models.ipsc.match.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link MatchResponse}s created by a bulk CSV import.
 *
 * @see za.co.hpsc.web.controllers.IpscMatchController
 * @since 8.3.0
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchResponseHolder {
    /** The list of matches created by the bulk import. */
    @NotNull
    private List<MatchResponse> matches;
}
