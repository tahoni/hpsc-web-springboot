package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.services.MatchStageCompetitorService;

import java.util.Optional;

@Slf4j
@Service
public class MatchStageCompetitorServiceImpl implements MatchStageCompetitorService {
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final CompetitorRepository competitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public MatchStageCompetitorServiceImpl(IpscMatchStageRepository ipscMatchStageRepository,
                                           CompetitorRepository competitorRepository,
                                           MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.competitorRepository = competitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<MatchStageCompetitor> findMatchStageCompetitor(MatchStageDto matchStageDto, CompetitorDto competitorDto) {
        if ((matchStageDto == null) || (competitorDto == null)) {
            return Optional.empty();
        }

        IpscMatchStage matchStage = null;
        if (matchStageDto.getId() != null) {
            matchStage = ipscMatchStageRepository.findById(matchStageDto.getId()).orElse(null);
        }

        Competitor competitor = null;
        if (competitorDto.getId() != null) {
            competitor = competitorRepository.findById(competitorDto.getId()).orElse(null);
        }

        if ((matchStage == null) || (competitor == null)) {
            return Optional.empty();
        }

        return matchStageCompetitorRepository.findByMatchStageAndCompetitor(matchStage, competitor);
    }
}
