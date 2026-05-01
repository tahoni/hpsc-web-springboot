package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.MatchCompetitorEntityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MatchCompetitorEntityServiceImpl implements MatchCompetitorEntityService {
    protected final MatchCompetitorRepository matchCompetitorRepository;

    public MatchCompetitorEntityServiceImpl(MatchCompetitorRepository matchCompetitorRepository) {
        this.matchCompetitorRepository = matchCompetitorRepository;
    }

    @Override
    public Optional<MatchCompetitor> findMatchCompetitorById(Long matchCompetitorId) {
        return matchCompetitorRepository.findById(matchCompetitorId);
    }

    @Override
    public List<MatchCompetitor> findMatchCompetitors(Long matchId, Long competitorId) {
        if ((competitorId == null) || (matchId == null)) {
            return new ArrayList<>();
        }
        return matchCompetitorRepository.findAllByCompetitorIdAndMatchId(competitorId, matchId);
    }
}
