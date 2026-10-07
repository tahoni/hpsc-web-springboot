package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A container class holding the {@link MatchCompetitorResponse}s created by a bulk CSV import.
 *
 * @see za.co.hpsc.web.controllers.IpscMatchCompetitorController
 * @since 9.1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchCompetitorResponseHolder {
    /** The list of match competitors created by the bulk import. */
    @NonNull
    private List<MatchCompetitorResponse> matchCompetitors;
}
