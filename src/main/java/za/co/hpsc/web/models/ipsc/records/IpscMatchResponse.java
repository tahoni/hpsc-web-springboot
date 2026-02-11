package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscMatchResponse(
        String name,
        String scheduledDate,
        String clubName,

        String matchFirearmType,
        String matchCategory,

        String dateEdited,

        List<CompetitorResponse> competitors
) {
}
