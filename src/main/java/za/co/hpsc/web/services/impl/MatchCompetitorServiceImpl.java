package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.MatchCompetitorService;

import java.util.Optional;

// TODO: add tests
@Slf4j
@Service
public class MatchCompetitorServiceImpl implements MatchCompetitorService {
    protected final MatchCompetitorRepository matchCompetitorRepository;

    public MatchCompetitorServiceImpl(MatchCompetitorRepository matchCompetitorRepository) {
        this.matchCompetitorRepository = matchCompetitorRepository;
    }

    @Override
    public Optional<MatchCompetitor> findMatchCompetitor(Long matchId, Long competitorId) {
        if ((competitorId == null) || (matchId == null)) {
            return Optional.empty();
        }
        return matchCompetitorRepository.findByCompetitorIdAndMatchId(competitorId, matchId);
    }
}
