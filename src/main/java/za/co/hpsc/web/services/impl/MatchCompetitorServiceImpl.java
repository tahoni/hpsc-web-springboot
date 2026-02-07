package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.services.MatchCompetitorService;

import java.util.Optional;

@Slf4j
@Service
public class MatchCompetitorServiceImpl implements MatchCompetitorService {
    @Override
    public Optional<MatchCompetitor> findMatchCompetitor(Long competitorId, Long matchId) {
        // TODO: add logic to find match competitor by competitorId and matchId
        return Optional.empty();
    }
}
