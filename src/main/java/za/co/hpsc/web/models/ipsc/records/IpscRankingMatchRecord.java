package za.co.hpsc.web.models.ipsc.records;

import java.time.LocalDateTime;
import java.util.List;

public record IpscRankingMatchRecord(
        String clubName,
        String matchName,
        LocalDateTime date,

        List<RankingMatchRecord> rankings
) {
}
