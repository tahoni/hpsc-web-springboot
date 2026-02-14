package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;

// TODO: Javadoc
public interface IpscRankingService {
    IpscRankingClubHolderRecord refreshClubRankings(String clubName);

    IpscRankingMatchHolderRecord refreshMatchRankings(String matchName);
}
