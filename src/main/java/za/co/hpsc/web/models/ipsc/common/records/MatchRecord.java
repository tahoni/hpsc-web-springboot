package za.co.hpsc.web.models.ipsc.common.records;

import java.util.List;

public record MatchRecord(
        String name,
        String scheduledDate,
        String clubName,

        String matchFirearmType,
        String matchCategory,

        List<CompetitorRecord> competitors,

        String dateEdited
) {
}
