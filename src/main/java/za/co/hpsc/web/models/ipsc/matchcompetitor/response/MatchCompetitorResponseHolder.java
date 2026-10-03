package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link MatchCompetitorResponse}s created by a bulk CSV import.
 *
 * @see za.co.hpsc.web.controllers.IpscMatchCompetitorController
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchCompetitorResponseHolder {
    /** The list of match competitors created by the bulk import. */
    @NotNull
    private List<MatchCompetitorResponse> matchCompetitors;
}
