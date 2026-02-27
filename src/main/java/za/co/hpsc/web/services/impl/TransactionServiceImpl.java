package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.TransactionService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

// TODO: finish this
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final DomainService domainService;

    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  DomainService domainService,
                                  ClubRepository clubRepository,
                                  CompetitorRepository competitorRepository,
                                  IpscMatchRepository ipscMatchRepository,
                                  IpscMatchStageRepository ipscMatchStageRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  MatchStageCompetitorRepository matchStageCompetitorRepository) {

        this.transactionManager = transactionManager;
        this.domainService = domainService;
        this.clubRepository = clubRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<IpscMatch> saveMatchResults(DtoToEntityMapping dtoToEntityMapping)
            throws FatalException {

        if ((dtoToEntityMapping == null) || (dtoToEntityMapping.getMatch() == null)) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            getClub(dtoToEntityMapping.getClub())
                    .ifPresent(clubRepository::save);

            Optional<IpscMatch> optionalMatchEntity = getIpscMatch(dtoToEntityMapping.getMatch());
            AtomicReference<IpscMatch> atomicMatchSEntity = new AtomicReference<>();
            optionalMatchEntity.ifPresent(match -> {
                ipscMatchRepository.save(match);
                atomicMatchSEntity.set(match);
            });

            IpscMatch matchEntity = atomicMatchSEntity.get();
            if (matchEntity == null) {
                return Optional.empty();
            }

            Map<UUID, IpscMatchStage> matchStageEntityMap =
                    getIpscMatchStages(dtoToEntityMapping.getMatchStageMap().values(), matchEntity);
            if (!matchStageEntityMap.isEmpty()) {
                ipscMatchStageRepository.saveAll(matchStageEntityMap.values());
            }

            Map<UUID, Competitor> competitorEntityMap =
                    getCompetitors(dtoToEntityMapping.getCompetitorMap().values());
            if (!competitorEntityMap.isEmpty()) {
                competitorRepository.saveAll(competitorEntityMap.values());
            }

            List<MatchCompetitor> matchCompetitorList =
                    getMatchCompetitors(dtoToEntityMapping.getMatchCompetitorMap().values(),
                            matchEntity, competitorEntityMap);
            if (!matchCompetitorList.isEmpty()) {
                matchCompetitorRepository.saveAll(matchCompetitorList);
            }

            List<MatchStageCompetitor> matchStageCompetitorList =
                    getMatchStageCompetitors(dtoToEntityMapping.getMatchStageCompetitorMap().values(),
                            matchStageEntityMap, competitorEntityMap);
            if (!matchStageCompetitorList.isEmpty()) {
                matchStageCompetitorRepository.saveAll(matchStageCompetitorList);
            }

            transactionManager.commit(transaction);
            return optionalMatchEntity;

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
            clubEntity = clubRepository.findById(clubDto.getId())
                    .orElseGet(Club::new);
        }
        clubEntity.init(clubDto);
        return Optional.of(clubEntity);
    }

    protected Optional<IpscMatch> getIpscMatch(MatchDto matchDto) {
        if (matchDto == null) {
            return Optional.empty();
        }

        IpscMatch matchEntity = new IpscMatch();
        if (matchDto.getId() != null) {
            matchEntity = ipscMatchRepository.findById(matchDto.getId())
                    .orElseGet(IpscMatch::new);
        }
        matchEntity.init(matchDto);
        return Optional.of(matchEntity);
    }

    protected Map<UUID, IpscMatchStage> getIpscMatchStages(Collection<MatchStageDto> matchStageDtoList,
                                                           IpscMatch matchEntity) {
        if (matchStageDtoList == null) {
            return new HashMap<>();
        }

        Map<UUID, IpscMatchStage> matchStageEntityMap = new HashMap<>();
        List<MatchStageDto> filteredMatchStageDtoList = matchStageDtoList.stream()
                .filter(Objects::nonNull).toList();

        filteredMatchStageDtoList.forEach(matchStageDto -> {
            IpscMatchStage matchStageEntity = new IpscMatchStage();
            if (matchStageDto.getId() != null) {
                matchStageEntity = ipscMatchStageRepository.findById(matchStageDto.getId())
                        .orElseGet(IpscMatchStage::new);
            }
            matchStageEntity.init(matchStageDto);
            matchStageEntity.setMatch(matchEntity);
            matchStageEntityMap.put(matchStageDto.getUuid(), matchStageEntity);
        });

        return matchStageEntityMap;
    }

    protected Map<UUID, Competitor> getCompetitors(Collection<CompetitorDto> competitorDtoList) {
        if (competitorDtoList == null) {
            return new HashMap<>();
        }

        List<Competitor> competitorEntityList = new ArrayList<>();
        List<CompetitorDto> filteredCompetitorDtoList = competitorDtoList.stream()
                .filter(Objects::nonNull).toList();

        Map<UUID, Competitor> competitorEntityMap = new HashMap<>();
        filteredCompetitorDtoList.forEach(competitorDto -> {
            Competitor competitorEntity = new Competitor();
            if (competitorDto.getId() != null) {
                competitorEntity = competitorRepository.findById(competitorDto.getId())
                        .orElseGet(Competitor::new);
            }
            competitorEntity.init(competitorDto);
            competitorEntityMap.put(competitorDto.getUuid(), competitorEntity);
        });

        return competitorEntityMap;
    }

    protected List<MatchCompetitor> getMatchCompetitors(Collection<MatchCompetitorDto> matchCompetitorDtoList,
                                                        IpscMatch matchEntity,
                                                        Map<UUID, Competitor> competitorEntityMap) {

        if ((matchCompetitorDtoList == null) || (matchEntity == null) || (competitorEntityMap == null)) {
            return new ArrayList<>();
        }

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        List<MatchCompetitorDto> filteredMatchCompetitorDtoList = matchCompetitorDtoList.stream()
                .filter(Objects::nonNull).toList();

        filteredMatchCompetitorDtoList.forEach(matchCompetitorDto -> {
            MatchCompetitor matchCompetitorEntity = new MatchCompetitor();
            if (matchCompetitorDto.getId() != null) {
                matchCompetitorEntity.setId(matchCompetitorDto.getId());
            }
            matchCompetitorEntity.init(matchCompetitorDto);
            matchCompetitorEntity.setMatch(matchEntity);
            matchCompetitorEntity.setCompetitor(competitorEntityMap.get(matchCompetitorDto.getCompetitor().getUuid()));
            matchCompetitorList.add(matchCompetitorEntity);
        });

        return matchCompetitorList;
    }

    protected List<MatchStageCompetitor> getMatchStageCompetitors(Collection<MatchStageCompetitorDto> matchStageCompetitorDtoList,
                                                                  Map<UUID, IpscMatchStage> matchStageMap,
                                                                  Map<UUID, Competitor> competitorEntityMap) {
        if ((matchStageCompetitorDtoList == null) || (matchStageMap == null) || (competitorEntityMap == null)) {
            return new ArrayList<>();
        }

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        matchStageMap.values().forEach(matchStageEntity -> {
            List<MatchStageCompetitor> matchStageCompetitorsForStage = getMatchStageCompetitors(
                    matchStageCompetitorDtoList, matchStageEntity, competitorEntityMap);
            matchStageCompetitorList.addAll(matchStageCompetitorsForStage);
        });

        return matchStageCompetitorList;
    }

    protected List<MatchStageCompetitor> getMatchStageCompetitors(Collection<MatchStageCompetitorDto> matchStageCompetitorDtoList,
                                                                  IpscMatchStage matchStageEntity,
                                                                  Map<UUID, Competitor> competitorEntityMap) {

        if ((matchStageCompetitorDtoList == null) || (matchStageEntity == null) || (competitorEntityMap == null)) {
            return new ArrayList<>();
        }

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        List<MatchStageCompetitorDto> filteredMatchStageCompetitorDtoList = matchStageCompetitorDtoList.stream()
                .filter(Objects::nonNull).toList();

        filteredMatchStageCompetitorDtoList.forEach(matchStageCompetitorDto -> {
            MatchStageCompetitor matchStageCompetitorEntity = new MatchStageCompetitor();
            if (matchStageCompetitorDto.getId() != null) {
                matchStageCompetitorEntity.setId(matchStageCompetitorDto.getId());
            }
            matchStageCompetitorEntity.init(matchStageCompetitorDto);
            matchStageCompetitorEntity.setMatchStage(matchStageEntity);
            matchStageCompetitorEntity.setCompetitor(competitorEntityMap.get(matchStageCompetitorDto.getCompetitor().getUuid()));
            matchStageCompetitorList.add(matchStageCompetitorEntity);
        });

        return matchStageCompetitorList;
    }
}
