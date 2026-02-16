package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;
import za.co.hpsc.web.models.ipsc.request.IpscRankingClubRequest;
import za.co.hpsc.web.models.ipsc.request.IpscRankingMatchRequest;

// TODO: Javadoc
// TODO: add tests
public interface IpscRankingService {
    IpscRankingClubHolderRecord initClubRankingEntities(MatchEntityHolder matchEntityHolder);

    IpscRankingClubHolderRecord refreshClubRankings(IpscRankingClubRequest rankingClubRequest);

    IpscRankingMatchHolderRecord refreshMatchRankings(IpscRankingMatchRequest rankingMatchRequest);
}
