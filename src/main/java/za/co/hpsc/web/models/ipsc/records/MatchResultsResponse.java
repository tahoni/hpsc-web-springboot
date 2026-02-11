package za.co.hpsc.web.models.matchresults.response;

import java.util.List;

public record MatchResultsResponse(
        List<IpscMatchResponse> matches
) {
}
