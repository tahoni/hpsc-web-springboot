package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.records.IpscMatchRecordHolder;

// TODO: Javadoc
public interface IpscRankingService {
    public IpscMatchRecordHolder refreshClubLogs(String matchName);

    public IpscMatchRecordHolder refreshMatchRankings(String matchName);
}
