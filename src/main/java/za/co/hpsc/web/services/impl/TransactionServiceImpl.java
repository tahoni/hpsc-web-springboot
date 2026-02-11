package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.TransactionService;

import java.util.*;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final IpscMatchRepository matchRepository;
    protected final ClubRepository clubRepository;
    protected final IpscMatchStageRepository matchStageRepository;
    protected final CompetitorRepository competitorRepository;

    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    protected final Map<UUID, Club> clubMap = new HashMap<>();
    protected final Map<UUID, IpscMatch> matchMap = new HashMap<>();
    protected final Map<UUID, Competitor> competitorMap = new HashMap<>();
    protected final Map<UUID, IpscMatchStage> matchStageMap = new HashMap<>();
    protected final Map<UUID, MatchCompetitor> matchCompetitorMap = new HashMap<>();
    protected final Map<UUID, MatchStageCompetitor> matchStageCompetitorMap = new HashMap<>();

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  IpscMatchRepository matchRepository, ClubRepository clubRepository,
                                  IpscMatchStageRepository matchStageRepository,
                                  CompetitorRepository competitorRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  MatchStageCompetitorRepository matchStageCompetitorRepository) {

        this.transactionManager = transactionManager;

        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.matchStageRepository = matchStageRepository;
        this.competitorRepository = competitorRepository;

        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public void saveMatchResults(MatchResultsDto matchResults) {
        if ((matchResults == null) || (matchResults.getMatch() == null)) {
            return;
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            initClubEntity(matchResults.getClub());
            IpscMatch match = initMatchEntity(matchResults.getMatch());
            initCompetitorEntities(matchResults.getCompetitors());
            initMatchStageEntities(matchResults.getStages());

            initMatchCompetitorEntities(matchResults.getMatchCompetitors());
            initMatchStageCompetitorEntities(matchResults.getMatchStageCompetitors());

            matchRepository.save(match);
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void saveMatchLogs() {
        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        try {
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    protected void initClubEntity(ClubDto clubDto) {
        if (clubDto != null) {
            // Initialises the club entity from the DTO or creates a new entity
            Optional<Club> optionalClubEntity = ((clubDto.getId() != null) ?
                    clubRepository.findById(clubDto.getId()) : Optional.empty());
            Club clubEntity = optionalClubEntity.orElseGet(Club::new);
            clubEntity.init(clubDto);

            // Update the map of clubs
            clubMap.put(clubDto.getUuid(), clubEntity);
        }
    }

    protected IpscMatch initMatchEntity(@NotNull MatchDto matchDto) {
        // Find the club entity
        Club clubEntity = null;
        if (matchDto.getClub() != null) {
            clubEntity = clubMap.get(matchDto.getClub().getUuid());
        }

        // Initialises the match entity from DTO or creates a new entity
        Optional<IpscMatch> optionalMatchEntity = ((matchDto.getId() != null) ?
                matchRepository.findById(matchDto.getId()) : Optional.empty());
        IpscMatch matchEntity = optionalMatchEntity.orElseGet(IpscMatch::new);
        matchEntity.init(matchDto, clubEntity);

        // Update the map of matches
        matchMap.put(matchDto.getUuid(), matchEntity);
        return matchEntity;
    }

    protected void initCompetitorEntities(@NotNull List<CompetitorDto> competitorDtoList) {
        // Initialises and accumulates competitor entities from DTOs
        competitorDtoList.forEach(competitorDto -> {
            // Initialises the competitor entity from DTO or creates a new entity
            Optional<Competitor> optionalCompetitorEntity = ((competitorDto.getId() != null) ?
                    competitorRepository.findById(competitorDto.getId()) : Optional.empty());
            Competitor competitorEntity = optionalCompetitorEntity.orElseGet(Competitor::new);
            competitorEntity.init(competitorDto);

            // Update the map of competitors
            competitorMap.put(competitorDto.getUuid(), competitorEntity);
        });
    }

    protected void initMatchStageEntities(@NotNull List<MatchStageDto> matchStageDtoList) {
        // Initialises and accumulates match stages from DTOs
        matchStageDtoList.forEach(stage -> {
            // Find the match entity
            IpscMatch matchEntity = matchMap.get(stage.getMatch().getUuid());

            // Initialises the match stage entity from DTO or creates a new entity
            Optional<IpscMatchStage> optionalMatchStageEntity = ((stage.getId() != null) ?
                    matchStageRepository.findById(stage.getId()) : Optional.empty());
            IpscMatchStage matchStageEntity = optionalMatchStageEntity.orElseGet(IpscMatchStage::new);
            matchStageEntity.init(stage, matchEntity);

            // Update the map of match stages
            matchStageMap.put(stage.getUuid(), matchStageEntity);
        });
    }

    protected void initMatchCompetitorEntities(@NotNull List<MatchCompetitorDto> matchCompetitors) {
        // Initialises and accumulates match competitors from DTOs
        matchCompetitors.forEach(matchCompetitorDto -> {
            // Find the competitor entity
            Competitor competitorEntity =
                    competitorMap.get(matchCompetitorDto.getCompetitor().getUuid());
            // Find the match entity
            IpscMatch matchEntity = matchMap.get(matchCompetitorDto.getMatch().getUuid());

            // Initialises the match competitor entity from DTO or creates a new entity
            Optional<MatchCompetitor> optionalMatchCompetitorEntity =
                    ((matchCompetitorDto.getId() != null) ?
                            matchCompetitorRepository.findById(matchCompetitorDto.getId()) : Optional.empty());
            MatchCompetitor matchCompetitorEntity =
                    optionalMatchCompetitorEntity.orElseGet(MatchCompetitor::new);
            matchCompetitorEntity.init(matchCompetitorDto, matchEntity, competitorEntity);

            // Update the map of match competitors
            matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorEntity);
        });
    }

    protected void initMatchStageCompetitorEntities(@NotNull List<MatchStageCompetitorDto> matchStageCompetitors) {

        // Initialises and accumulates match stage competitors from DTOs
        matchStageCompetitors.forEach(matchStageCompetitorDto -> {
            // Find the competitor entity
            Competitor competitorEntity = competitorMap.get(matchStageCompetitorDto.getCompetitor().getUuid());
            // Find the match stage entity
            IpscMatchStage matchStageEntity = matchStageMap.get(matchStageCompetitorDto.getMatchStage().getUuid());

            // Initialises the match stage competitor entity from DTO or creates a new entity
            Optional<MatchStageCompetitor> optionalMatchStageCompetitorEntity =
                    ((matchStageCompetitorDto.getId() != null) ?
                            matchStageCompetitorRepository.findById(matchStageCompetitorDto.getId()) :
                            Optional.empty());
            MatchStageCompetitor matchStageCompetitorEntity =
                    optionalMatchStageCompetitorEntity.orElseGet(MatchStageCompetitor::new);
            matchStageCompetitorEntity.init(matchStageCompetitorDto, matchStageEntity, competitorEntity);

            // Update the map of match stage competitors
            matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorEntity);
        });
    }
}
