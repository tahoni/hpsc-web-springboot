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
import za.co.hpsc.web.models.ipsc.common.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.common.data.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.common.dto.*;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
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
    public Optional<MatchHolder> saveMatchResults(DtoMapping dtoMapping)
            throws FatalException {

        if ((dtoMapping == null) || (dtoMapping.getMatch() == null)) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        MatchHolder matchHolder = new MatchHolder();
        try {
            DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping(dtoMapping);

            Optional<Club> optionalClub = getClub(dtoMapping.getClub());

            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                club = clubRepository.save(club);
                matchHolder.setClub(club);
                dtoToEntityMapping.setClub(club);
            }

            Optional<IpscMatch> optionalIpscMatch = getIpscMatch(dtoMapping.getMatch());
            if (optionalIpscMatch.isEmpty()) {
                throw new FatalException("Unable to save the match: Match is null");
            }
            IpscMatch ipscMatch = optionalIpscMatch.get();
            ipscMatch = ipscMatchRepository.save(ipscMatch);
            matchHolder.setMatch(ipscMatch);
            dtoToEntityMapping.setMatch(ipscMatch);

            List<Competitor> competitorList = getCompetitors(dtoToEntityMapping);
            if (!competitorList.isEmpty()) {
                competitorRepository.saveAll(competitorList);
                matchHolder.setCompetitors(competitorList);
            }

            List<IpscMatchStage> ipscMatchStageList = getIpscMatchStages(dtoToEntityMapping);
            if (!ipscMatchStageList.isEmpty()) {
                ipscMatchStageRepository.saveAll(ipscMatchStageList);
                matchHolder.setMatchStages(ipscMatchStageList);
            }

            List<MatchCompetitor> matchCompetitorList = getMatchCompetitors(dtoToEntityMapping);
            if (!matchCompetitorList.isEmpty()) {
                matchCompetitorRepository.saveAll(matchCompetitorList);
                matchHolder.setMatchCompetitors(matchCompetitorList);
            }

            List<MatchStageCompetitor> matchStageCompetitorList = getAllMatchStageCompetitors(dtoToEntityMapping);
            if (!matchStageCompetitorList.isEmpty()) {
                matchStageCompetitorRepository.saveAll(matchStageCompetitorList);
                matchHolder.setMatchStageCompetitors(matchStageCompetitorList);
            }

            ipscMatchRepository.save(ipscMatch);
            transactionManager.commit(transaction);
            return Optional.of(matchHolder);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            log.error(e.getMessage(), e);
            throw new FatalException("Unable to save the match: " + e.getMessage(), e);
        }
    }

    // TODO: add Javadoc
    @Override
    public Optional<MatchHolder> saveMatch(MatchOnlyResultsDto matchOnlyResultsDto) throws FatalException {
        if (matchOnlyResultsDto == null) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        MatchHolder matchHolder = new MatchHolder();
        try {
            ClubDto clubDto = matchOnlyResultsDto.getClub();
            Optional<Club> optionalClub = getClub(clubDto);
            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                club = clubRepository.save(club);
                matchHolder.setClub(club);
            }

            MatchOnlyDto matchOnlyDto = matchOnlyResultsDto.getMatch();
            Optional<IpscMatch> optionalIpscMatch = getIpscMatch(matchOnlyDto);
            if (optionalIpscMatch.isEmpty()) {
                throw new FatalException("Unable to save the match: Match is null");
            }
            IpscMatch ipscMatch = optionalIpscMatch.get();
            ipscMatch = ipscMatchRepository.save(ipscMatch);
            matchHolder.setMatch(ipscMatch);

            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            log.error(e.getMessage(), e);
            throw new FatalException("Unable to save the match: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Retrieves a club entity based on the provided club DTO and maps it using the given DTO to entity mapping.
     * If the club DTO is null, an empty {@code Optional} is returned.
     *
     * @param clubDto the data transfer object containing club information can be null
     * @return an {@code Optional} containing the club entity, or an empty {@code Optional} if the club DTO is null
     */
    protected Optional<Club> getClub(ClubDto clubDto) {
        if ((clubDto == null) || (clubDto.getName() == null)) {
            return Optional.empty();
        }

        Club clubEntity = new Club();
        if (clubDto.getId() != null) {
            clubEntity = clubRepository.findById(clubDto.getId()).orElseGet(Club::new);
        }
        clubEntity.init(clubDto);

        return Optional.of(clubEntity);
    }

    /**
     * Retrieves an optional IpscMatch entity based on the provided DtoToEntityMapping.
     * If the mapping contains a valid MatchDto with an ID, the corresponding
     * IpscMatch entity is fetched from the repository. If no entity is found, a new one
     * is created. The entity is then initialised with the data from the MatchDto.
     * The updated entity is set back into the DtoToEntityMapping.
     *
     * @param matchDto@return an Optional containing the initialized IpscMatch entity if the MatchDto is
     *                        present in the mapping, otherwise an empty Optional
     */
    protected Optional<IpscMatch> getIpscMatch(MatchDto matchDto) {
        if (matchDto == null) {
            return Optional.empty();
        }

        IpscMatch matchEntity = new IpscMatch();
        if (matchDto.getId() != null) {
            matchEntity = ipscMatchRepository.findById(matchDto.getId()).orElseGet(IpscMatch::new);
        }
        matchEntity.init(matchDto);

        return Optional.of(matchEntity);
    }

    // TODO: add Javadoc
    protected Optional<IpscMatch> getIpscMatch(MatchOnlyDto matchOnlyDto) {
        if (matchOnlyDto == null) {
            return Optional.empty();
        }

        IpscMatch matchEntity = new IpscMatch();
        if (matchOnlyDto.getId() != null) {
            matchEntity = ipscMatchRepository.findById(matchOnlyDto.getId()).orElseGet(IpscMatch::new);
        }
        matchEntity.init(matchOnlyDto);

        return Optional.of(matchEntity);
    }

    /**
     * Retrieves and processes a list of match stages for an IPSC match by
     * mapping data from DTOs to entity objects.
     *
     * @param dtoToEntityMapping an object containing the mapping details
     *                           between DTOs and entity models, including
     *                           the match entity and stage DTO list.
     * @return a list of {@code IpscMatchStage} entities representing the match
     * stages. If the match entity is not present, an empty list is returned.
     */
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

    /**
     * Retrieves a list of competitor entities mapped from the provided DTO to entity mapping.
     *
     * @param dtoToEntityMapping an object containing the mapping between DTOs and entities,
     *                           including a list of Competitor DTOs.
     * @return a list of Competitor entities, initialised and mapped from the provided DTOs.
     */
    protected List<Competitor> getCompetitors(@NotNull DtoToEntityMapping dtoToEntityMapping) {

        List<CompetitorDto> competitorDtoList = dtoToEntityMapping.getCompetitorDtoList();

        List<Competitor> competitorEntityList = new ArrayList<>();
        competitorDtoList.forEach(competitorDto -> {
            Competitor competitorEntity = new Competitor();
            if (competitorDto.getId() != null) {
                competitorEntity =
                        competitorRepository.findById(competitorDto.getId())
                                .orElseGet(Competitor::new);
            }
            competitorEntity.init(competitorDto);

            dtoToEntityMapping.setCompetitor(competitorDto, competitorEntity);
            competitorEntityList.add(competitorEntity);
        });

        return competitorEntityList;
    }

    /**
     * Converts a list of MatchCompetitorDto objects from the given DtoToEntityMapping instance
     * into a list of MatchCompetitor entities, initialising and mapping each entity accordingly.
     *
     * @param dtoToEntityMapping Object containing the mapping details and the list of
     *                           MatchCompetitorDto objects to be converted.
     * @return A list of MatchCompetitor entities created from the provided DTO list.
     */
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

    /**
     * Retrieves a list of all competitors participating in various match stages.
     * This method processes the provided mapping to extract and accumulate competitors
     * from all match stages.
     *
     * @param dtoToEntityMapping an object containing the mapping between data transfer objects (DTOs)
     *                           and their corresponding entities, including the list of match stage DTOs
     *                           to be processed.
     * @return a list of {@code MatchStageCompetitor} instances containing competitors
     * from all processed match stages.
     */
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

    /**
     * Retrieves a list of match stage competitors by mapping and filtering data from the provided match stage DTO.
     *
     * @param matchStageDto      the data transfer object representing the match stage containing information to filter competitors
     * @param dtoToEntityMapping the utility object responsible for mapping DTOs to entities and performing necessary transformations
     * @return a list of {@link MatchStageCompetitor} objects corresponding to the provided match stage DTO
     */
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
