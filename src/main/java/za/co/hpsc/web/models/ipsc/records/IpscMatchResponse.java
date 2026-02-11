package za.co.hpsc.web.models.ipsc.records;

import java.time.LocalDate;
import java.util.List;

public record IpscMatchResponse(
        String name,
        LocalDate scheduledDate,
        String clubName,

        String matchFirearmType,
        String matchCategory,

        String dateEdited,

        List<CompetitorResponse> competitors
) {
}
