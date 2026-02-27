package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.TransactionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  ClubRepository clubRepository,
                                  CompetitorRepository competitorRepository,
                                  IpscMatchRepository ipscMatchRepository,
                                  IpscMatchStageRepository ipscMatchStageRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  MatchStageCompetitorRepository matchStageCompetitorRepository) {

        this.transactionManager = transactionManager;
        this.clubRepository = clubRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<IpscMatch> saveMatchResults(DtoMapping dtoMapping)
            throws FatalException {

        if ((dtoMapping == null) || (dtoMapping.getMatch() == null)) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping(dtoMapping);

            getClub(dtoMapping.getClub()).ifPresent(clubRepository::save);
            getIpscMatch(dtoToEntityMapping).ifPresent(ipscMatchRepository::save);

            if (dtoMapping.getMatch() == null) {
                return Optional.empty();
            }

            List<Competitor> competitorList = getCompetitors(dtoToEntityMapping);
            if (!competitorList.isEmpty()) {
                competitorRepository.saveAll(competitorList);
            }

            List<IpscMatchStage> ipscMatchStageList = getIpscMatchStages(dtoToEntityMapping);
            if (!ipscMatchStageList.isEmpty()) {
                ipscMatchStageRepository.saveAll(ipscMatchStageList);
            }

            List<MatchCompetitor> matchCompetitorList = getMatchCompetitors(dtoToEntityMapping);
            if (!matchCompetitorList.isEmpty()) {
                matchCompetitorRepository.saveAll(matchCompetitorList);
            }

            List<MatchStageCompetitor> matchStageCompetitorList = getAllMatchStageCompetitors(dtoToEntityMapping);
            if (!matchStageCompetitorList.isEmpty()) {
                matchStageCompetitorRepository.saveAll(matchStageCompetitorList);
            }

            transactionManager.commit(transaction);
            return dtoToEntityMapping.getMatchEntity();

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            log.error(e.getMessage(), e);
            throw new FatalException("Unable to save the match: " + e.getMessage(), e);
        }
    }

    protected Optional<Club> getClub(ClubDto clubDto) {
        if (clubDto == null) {
            return Optional.empty();
        }

        Club clubEntity = new Club();
        if (clubDto.getId() != null) {
            clubEntity = clubRepository.findById(clubDto.getId()).orElseGet(Club::new);
        }
        clubEntity.init(clubDto);
        return Optional.of(clubEntity);
    }

    protected Optional<IpscMatch> getIpscMatch(@NotNull DtoToEntityMapping dtoToEntityMapping) {
        MatchDto matchDto = dtoToEntityMapping.getMatchDto().orElse(null);
        if (matchDto == null) {
            return Optional.empty();
        }

        IpscMatch matchEntity = new IpscMatch();
        if (matchDto.getId() != null) {
            matchEntity = ipscMatchRepository.findById(matchDto.getId()).orElseGet(IpscMatch::new);
        }
        matchEntity.init(matchDto);

        dtoToEntityMapping.setMatch(matchEntity);
        return Optional.of(matchEntity);
    }

    protected List<IpscMatchStage> getIpscMatchStages(@NotNull DtoToEntityMapping dtoToEntityMapping) {

        IpscMatch matchEntity = dtoToEntityMapping.getMatchEntity().orElse(null);
        if (matchEntity == null) {
            return new ArrayList<>();
        }

        List<IpscMatchStage> matchStageEntityList = new ArrayList<>();
        List<MatchStageDto> filteredMatchStageDtoList = dtoToEntityMapping.getMatchStageDtoList().stream()
                .filter(Objects::nonNull).toList();

        filteredMatchStageDtoList.forEach(matchStageDto -> {
            IpscMatchStage matchStageEntity = new IpscMatchStage();
            if (matchStageDto.getId() != null) {
                matchStageEntity = ipscMatchStageRepository.findById(matchStageDto.getId())
                        .orElseGet(IpscMatchStage::new);
            }
            matchStageEntity.init(matchStageDto);
            matchStageEntity.setMatch(matchEntity);

            dtoToEntityMapping.setMatchStage(matchStageDto, matchStageEntity);
            matchStageEntityList.add(matchStageEntity);
        });

        return matchStageEntityList;
    }

    protected List<Competitor> getCompetitors(@NotNull DtoToEntityMapping dtoToEntityMapping) {

        List<CompetitorDto> competitorDtoList = dtoToEntityMapping.getCompetitorDtoList();

        List<Competitor> competitorEntityList = new ArrayList<>();
        competitorDtoList.forEach(competitorDto -> {
            Competitor competitorEntity = new Competitor();
            if (competitorDto.getId() != null) {
                competitorEntity = competitorRepository.findById(competitorDto.getId())
                        .orElseGet(Competitor::new);
            }
            competitorEntity.init(competitorDto);

            dtoToEntityMapping.setCompetitor(competitorDto, competitorEntity);
            competitorEntityList.add(competitorEntity);
        });

        return competitorEntityList;
    }

    protected List<MatchCompetitor> getMatchCompetitors(@NotNull DtoToEntityMapping dtoToEntityMapping) {

        List<MatchCompetitorDto> matchCompetitorDtoList = dtoToEntityMapping.getMatchCompetitorDtoList();

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorDtoList.forEach(matchCompetitorDto -> {
            MatchCompetitor matchCompetitorEntity = new MatchCompetitor();
            if (matchCompetitorDto.getId() != null) {
                matchCompetitorEntity.setId(matchCompetitorDto.getId());
            }
            matchCompetitorEntity.init(matchCompetitorDto);

            dtoToEntityMapping.setMatchCompetitor(matchCompetitorDto, matchCompetitorEntity);
            matchCompetitorList.add(matchCompetitorEntity);
        });

        return matchCompetitorList;
    }

    protected List<MatchStageCompetitor> getAllMatchStageCompetitors(@NotNull DtoToEntityMapping dtoToEntityMapping) {

        List<MatchStageDto> filteredMatchStageDtoList = dtoToEntityMapping.getMatchStageDtoList();

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        filteredMatchStageDtoList.forEach(matchStageDto -> {
            List<MatchStageCompetitor> matchStageCompetitorsForStage =
                    getMatchStageCompetitors(matchStageDto, dtoToEntityMapping);

            matchStageCompetitorList.addAll(matchStageCompetitorsForStage);
        });

        return matchStageCompetitorList;
    }

    protected List<MatchStageCompetitor> getMatchStageCompetitors(MatchStageDto matchStageDto,
                                                                  @NotNull DtoToEntityMapping dtoToEntityMapping) {

        List<MatchStageCompetitorDto> matchStageCompetitoDtoList =
                dtoToEntityMapping.getMatchStageCompetitorDtoList();
        List<MatchStageCompetitorDto> filteredMatchStageCompetitorDtoList =
                matchStageCompetitoDtoList.stream()
                        .filter(matchStageCompetitorDto -> matchStageCompetitorDto.getMatchStage() != null)
                        .filter(matchStageCompetitorDto -> matchStageDto.getUuid()
                                .equals(matchStageCompetitorDto.getMatchStage().getUuid()))
                        .toList();

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        filteredMatchStageCompetitorDtoList.forEach(matchStageCompetitorDto -> {
            MatchStageCompetitor matchStageCompetitorEntity = new MatchStageCompetitor();
            if (matchStageCompetitorDto.getId() != null) {
                matchStageCompetitorEntity.setId(matchStageCompetitorDto.getId());
            }
            matchStageCompetitorEntity.init(matchStageCompetitorDto);

            dtoToEntityMapping.setMatchStageCompetitor(matchStageCompetitorDto, matchStageCompetitorEntity);
            matchStageCompetitorList.add(matchStageCompetitorEntity);
        });

        return matchStageCompetitorList;
    }
}
