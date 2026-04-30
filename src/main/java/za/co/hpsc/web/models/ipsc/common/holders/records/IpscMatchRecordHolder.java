package za.co.hpsc.web.models.ipsc.common.holders.records;

import za.co.hpsc.web.models.ipsc.common.records.MatchRecord;

import java.util.List;

public record IpscMatchRecordHolder(
        List<MatchRecord> matches
) {
}
