package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.services.MatchStageEntityService;

import java.util.Optional;

// TODO: add tests
// TODO: add comments
@Slf4j
@Service
public class MatchStageEntityServiceImpl implements MatchStageEntityService {
    protected final IpscMatchStageRepository matchStageRepository;

    public MatchStageEntityServiceImpl(IpscMatchStageRepository matchStageRepository) {
        this.matchStageRepository = matchStageRepository;
    }

    @Override
    public Optional<IpscMatchStage> findMatchStage(Long matchId, Integer stageNumber) {
        if ((matchId == null) || (stageNumber == null)) {
            return Optional.empty();
        }
        return matchStageRepository.findByMatchIdAndStageNumber(matchId, stageNumber);
    }
}
