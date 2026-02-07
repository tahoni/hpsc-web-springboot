package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.services.MatchStageCompetitorService;

import java.util.Optional;

@Slf4j
@Service
public class MatchStageCompetitorServiceImpl implements MatchStageCompetitorService {
    @Override
    public Optional<MatchStageCompetitor> findMatchStageCompetitor(Long matchId, Long matchStageId,
                                                                   Long competitorId) {
        // TODO: add logic to find match stage competitor by matchId, matchStageId and competitorId
        return Optional.empty();
    }
}
