package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record RankingClubRecord(
        String clubName,

        String firearmType,
        String division,

        List<CompetitorRankingClubRecord> rankings
) {
}
