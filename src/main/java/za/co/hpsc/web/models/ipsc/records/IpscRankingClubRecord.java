package za.co.hpsc.web.models.ipsc.records;

import java.time.LocalDateTime;
import java.util.List;

public record IpscRankingClubRecord(
        String clubName,
        LocalDateTime startDate,
        LocalDateTime endDate,

        List<RankingClubRecord> rankings
) {
}
