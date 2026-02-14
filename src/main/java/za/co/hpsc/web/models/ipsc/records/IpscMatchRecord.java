package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscMatchRecord(
        String name,
        String scheduledDate,
        String clubName,

        String matchFirearmType,
        String matchCategory,

        List<CompetitorMatchRecord> competitors,

        String dateEdited
) {
}
