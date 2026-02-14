package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscRankingClubRecord(
        String clubName,

        List<RankingClubRecord> rankings
) {
}
