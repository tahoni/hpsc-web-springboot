package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.services.MatchStageCompetitorEntityService;

import java.util.Optional;

// TODO: add tests
@Slf4j
@Service
public class MatchStageCompetitorEntityServiceImpl implements MatchStageCompetitorEntityService {
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final CompetitorRepository competitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public MatchStageCompetitorEntityServiceImpl(IpscMatchStageRepository ipscMatchStageRepository,
                                                 CompetitorRepository competitorRepository,
                                                 MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.competitorRepository = competitorRepository;
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
