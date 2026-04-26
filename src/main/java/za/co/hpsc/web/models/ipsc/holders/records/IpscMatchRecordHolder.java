package za.co.hpsc.web.models.ipsc.holders.records;

import za.co.hpsc.web.models.ipsc.records.IpscMatchRecord;

import java.util.List;

public record IpscMatchRecordHolder(
        List<IpscMatchRecord> matches
) {
}
