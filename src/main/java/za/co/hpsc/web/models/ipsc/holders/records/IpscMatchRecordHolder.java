package za.co.hpsc.web.models.ipsc.holders.records;

import za.co.hpsc.web.models.ipsc.records.MatchRecord;

import java.util.List;

public record IpscMatchRecordHolder(
        List<MatchRecord> matches
) {
}
