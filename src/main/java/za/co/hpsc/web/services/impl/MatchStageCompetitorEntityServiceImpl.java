package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.services.MatchStageCompetitorEntityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MatchStageCompetitorEntityServiceImpl implements MatchStageCompetitorEntityService {
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public MatchStageCompetitorEntityServiceImpl(MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<MatchStageCompetitor> findMatchStageCompetitorById(Long matchStageCompetitorId) {
        return matchStageCompetitorRepository.findById(matchStageCompetitorId);
    }

    @Override
    public List<MatchStageCompetitor> findMatchStageCompetitors(Long matchStageId, Long competitorId) {
        if ((matchStageId == null) || (competitorId == null)) {
            return new ArrayList<>();
        }
        return matchStageCompetitorRepository.findAllByMatchStageIdAndCompetitorId(matchStageId, competitorId);
    }
}
