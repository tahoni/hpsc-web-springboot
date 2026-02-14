package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecordHolder;
import za.co.hpsc.web.services.IpscRankingService;

// TODO: Javadoc
// TODO: add tessts
@Slf4j
@Service
public class IpscRankingServiceImpl implements IpscRankingService {
    @Override
    public IpscMatchRecordHolder refreshClubLogs(String matchName) {
        return null;
    }

    @Override
    public IpscMatchRecordHolder refreshMatchRankings(String matchName) {
        return null;
    }
}
