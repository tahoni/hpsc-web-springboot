package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.services.MatchStageCompetitorService;

import java.util.Optional;

@Slf4j
@Service
public class MatchStageCompetitorServiceImpl implements MatchStageCompetitorService {
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public MatchStageCompetitorServiceImpl(MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<MatchStageCompetitor> findMatchStageCompetitor(Long matchStageId, Long competitorId) {
        if ((matchStageId == null) || (competitorId == null)) {
            return Optional.empty();
        }
        return matchStageCompetitorRepository.findByMatchStageIdAndCompetitorId(matchStageId, competitorId);
    }
}
