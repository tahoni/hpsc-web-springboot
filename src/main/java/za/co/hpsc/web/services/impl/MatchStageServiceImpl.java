package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.repositories.MatchStageRepository;
import za.co.hpsc.web.services.MatchStageService;

import java.util.Optional;

@Slf4j
@Service
public class MatchStageServiceImpl implements MatchStageService {
    protected final MatchStageRepository matchStageRepository;

    public MatchStageServiceImpl(MatchStageRepository matchStageRepository) {
        this.matchStageRepository = matchStageRepository;
    }

    @Override
    public Optional<MatchStage> findMatchStage(Long matchId, Integer stageNumber) {
        return matchStageRepository.findByMatchIdAndStageNumber(matchId, stageNumber);
    }
}
