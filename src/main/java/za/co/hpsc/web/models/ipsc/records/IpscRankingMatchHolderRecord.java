package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscRankingMatchHolderRecord(
        List<IpscRankingMatchRecord> rankings
) {
}
