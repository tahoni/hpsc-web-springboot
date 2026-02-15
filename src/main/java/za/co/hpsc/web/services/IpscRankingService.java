package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;
import za.co.hpsc.web.models.ipsc.request.IpscRankingClubRequest;
import za.co.hpsc.web.models.ipsc.request.IpscRankingMatchRequest;

// TODO: Javadoc
public interface IpscRankingService {
    IpscRankingClubHolderRecord refreshClubRankings(IpscRankingClubRequest rankingClubRequest);

    IpscRankingMatchHolderRecord refreshMatchRankings(IpscRankingMatchRequest rankingMatchRequest);
}
