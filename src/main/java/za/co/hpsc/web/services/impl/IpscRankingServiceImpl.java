package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;
import za.co.hpsc.web.services.IpscRankingService;

// TODO: Javadoc
// TODO: add tessts
@Slf4j
@Service
public class IpscRankingServiceImpl implements IpscRankingService {
    @Override
    public IpscRankingClubHolderRecord refreshClubRankings(String matchName) {
        return null;
    }

    @Override
    public IpscRankingMatchHolderRecord refreshMatchRankings(String matchName) {
        return null;
    }

    protected boolean mustUpdateClubRanking(String clubName) {

        return false;
    }

    protected boolean mustUpdateMatchRanking(String matchName) {
        return false;
    }

}
