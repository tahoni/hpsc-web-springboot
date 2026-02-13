package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record MatchResultsResponse(
        List<IpscMatchRecord> matches
) {
}
