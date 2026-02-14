package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscRankingMatchRecord(
        String clubName,
        String matchName,

        List<RankingMatchRecord> rankings
) {
}
