package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.DomainService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DomainServiceImpl implements DomainService {
    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public DomainServiceImpl(ClubRepository clubRepository,
                             CompetitorRepository competitorRepository,
                             IpscMatchRepository ipscMatchRepository,
                             IpscMatchStageRepository ipscMatchStageRepository,
                             MatchCompetitorRepository matchCompetitorRepository,
                             MatchStageCompetitorRepository matchStageCompetitorRepository) {

        this.clubRepository = clubRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    /**
     * Initialises match entities based on the provided match results.
     *
     * @param matchResults The DTO containing match results, including match, club, competitors, and stages.
     * @return An Optional containing the initialised MatchEntityHolder if the match is present; otherwise, an empty Optional.
     */
    @Override
    public Optional<MatchEntityHolder> initMatchEntities(MatchResultsDto matchResults) {
        if ((matchResults == null) || (matchResults.getMatch() == null)) {
            return Optional.empty();
        }

        AtomicReference<Optional<MatchEntityHolder>> optionalMatchEntityHolder =
                new AtomicReference<>(Optional.empty());

        Optional<Club> optionalClub = initClubEntity(matchResults.getClub());
        if (optionalClub.isPresent()) {
            optionalClub.ifPresent(club -> club.init(matchResults.getClub()));
        } else {
            optionalClub = initClubEntity(ClubIdentifier.HPSC);
        }
        Club club = optionalClub.orElse(null);
        Optional<IpscMatch> optionalMatch = initMatchEntity(matchResults.getMatch(), club);

        optionalMatch.ifPresent(match -> {
            Map<UUID, Competitor> competitorMap = initCompetitorEntities(matchResults.getCompetitors());
            Map<UUID, IpscMatchStage> matchStageMap = initMatchStageEntities(matchResults.getStages(), match);

            Map<UUID, MatchCompetitor> matchCompetitorMap =
                    initMatchCompetitorEntities(matchResults.getMatchCompetitors(),
                            competitorMap, ClubIdentifier.HPSC);
            Map<UUID, MatchStageCompetitor> matchStageCompetitorMap =
                    initMatchStageCompetitorEntities(matchResults.getMatchStageCompetitors(),
                            competitorMap, ClubIdentifier.HPSC);

            optionalMatchEntityHolder.set(Optional.of(new MatchEntityHolder(match, club,
                    matchStageMap.values().stream().filter(Objects::nonNull).toList(),
                    competitorMap.values().stream().filter(Objects::nonNull).toList(),
                    matchCompetitorMap.values().stream().filter(Objects::nonNull).toList(),
                    matchStageCompetitorMap.values().stream().filter(Objects::nonNull).toList())));
        });

        return optionalMatchEntityHolder.get();
    }

    /**
     * Initialises a Club entity based on the provided ClubDto.
     * If the ClubDto contains a valid ID, attempts to find the existing Club entity in the repository.
     * If not found, creates a new Club entity, populates it with data from the DTO, and returns it.
     *
     * @param clubDto the data transfer object containing data to initialise or update a Club entity
     * @return an Optional containing the initialised Club entity if the input is valid, or an empty Optional if the input is null or invalid
     */
    protected Optional<Club> initClubEntity(ClubDto clubDto) {
        if ((clubDto != null) && (clubDto.getId() != null)) {
            // Find the club entity if present
            Optional<Club> optionalClubEntity = clubRepository.findById(clubDto.getId());

            // Initialise the club entity from DTO or create a new entity
            Club clubEntity = optionalClubEntity.orElse(new Club());
            clubEntity.init(clubDto);

            // Add attributes to the club
            return Optional.of(clubEntity);
        }

        return Optional.empty();
    }

    /**
     * Initialises and retrieves a Club entity based on the given ClubIdentifier.
     *
     * @param clubIdentifier the identifier used to look up the club; if null, an empty Optional is returned
     * @return an Optional containing the club entity if found, or an empty Optional if no match is found
     */
    protected Optional<Club> initClubEntity(ClubIdentifier clubIdentifier) {
        if (clubIdentifier != null) {
            // Find the club entity if present
            return clubRepository.findByAbbreviation(clubIdentifier.getName());
        }

        return Optional.empty();
    }

    /**
     * Initialises an instance of the {@code IpscMatch} entity using the provided DTO and links it to the given club entity.
     * If a match with the specified ID in the DTO exists in the repository, it is retrieved and updated; otherwise,
     * a new {@code IpscMatch} entity is created and initialised.
     *
     * @param matchDto   the data transfer object containing match details and an optional match ID for lookup.
     * @param clubEntity the club entity to associate with the match entity.
     * @return an {@code Optional} containing the initialised {@code IpscMatch} entity.
     */
    protected Optional<IpscMatch> initMatchEntity(MatchDto matchDto, Club clubEntity) {
        // Find the match entity if present
        Optional<IpscMatch> optionalIpscMatchEntity = Optional.empty();
        if ((matchDto != null) && (matchDto.getId() != null)) {
            optionalIpscMatchEntity = ipscMatchRepository.findByIdWithClubStages(matchDto.getId());
        }

        // Initialise the match entity from DTO or create a new entity
        IpscMatch matchEntity = optionalIpscMatchEntity.orElse(new IpscMatch());

        // Add attributes to the match
        matchEntity.init(matchDto);

        // Link the match to the club
        matchEntity.setClub(clubEntity);

        return Optional.of(matchEntity);
    }

    /**
     * Initialises a map of Competitor entities from a list of CompetitorDto objects.
     * Each CompetitorDto is used to either find an existing Competitor entity or create a new one.
     * The map is constructed with the UUID as the key and the corresponding Competitor entity as the value.
     *
     * @param competitorDtoList the list of CompetitorDto objects containing the data used to initialise or update Competitor entities
     * @return a map where the key is the UUID from the CompetitorDto and the value is the corresponding Competitor entity
     */
    protected Map<UUID, Competitor> initCompetitorEntities(List<CompetitorDto> competitorDtoList) {
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        if (competitorDtoList != null) {

            // Initialise and accumulate competitor entities from DTOs
            competitorDtoList.stream().filter(Objects::nonNull)
                    .forEach(competitorDto -> {
                        // Find the competitor entity if present
                        Optional<Competitor> optionalCompetitorEntity = Optional.empty();
                        if (competitorDto.getId() != null) {
                            optionalCompetitorEntity = competitorRepository.findById(competitorDto.getId());
                        }

                        // Initialise the competitor entity from DTO or create a new entity
                        Competitor competitorEntity = optionalCompetitorEntity.orElse(new Competitor());
                        // Add attributes to the competitor
                        competitorEntity.init(competitorDto);

                        // Update the map of competitors
                        competitorMap.put(competitorDto.getUuid(), competitorEntity);
                    });
        }

        return competitorMap;
    }

    /**
     * Initialises and maps IPSC match stage entities based on the provided list of MatchStageDto objects
     * and the associated IpscMatch entity. If the DTO contains an ID, it attempts to retrieve the
     * corresponding entity from the repository; otherwise, it creates a new entity. Each stage
     * is then linked to the associated match entity and added to the map.
     *
     * @param matchStageDtoList the list of MatchStageDto objects containing data used to
     *                          initialise or update the match stage entities.
     * @param matchEntity       the IpscMatch entity that the match stages are associated with.
     * @return a map where the keys are the UUIDs from the MatchStageDto objects and the values
     * are the initialised or updated IpscMatchStage entities.
     */
    protected Map<UUID, IpscMatchStage> initMatchStageEntities(List<MatchStageDto> matchStageDtoList,
                                                               IpscMatch matchEntity) {
        Map<UUID, IpscMatchStage> matchStageMap = new HashMap<>();
        if (matchStageDtoList != null) {

            // Initialise and accumulate match stages from DTOs
            matchStageDtoList.stream().filter(Objects::nonNull)
                    .forEach(stage -> {
                        // Find the match stage entity if present
                        Optional<IpscMatchStage> optionalIpscMatchStageEntity = Optional.empty();
                        if (stage.getId() != null) {
                            optionalIpscMatchStageEntity = ipscMatchStageRepository.findById(stage.getId());
                        }

                        // Initialise the match stage entity from DTO or create a new entity
                        IpscMatchStage matchStageEntity = optionalIpscMatchStageEntity.orElse(new IpscMatchStage());
                        // Add attributes to the match stage
                        matchStageEntity.init(stage, matchEntity);
                        // Link the match stage to the match
                        matchStageEntity.setMatch(matchEntity);

                        // Update the map of match stages
                        matchStageMap.put(stage.getUuid(), matchStageEntity);
                    });
        }

        return matchStageMap;
    }

    /**
     * Initialises a map of match competitor entities based on the provided list of match competitor DTOs,
     * a competitor map, and a club identifier. This method populates the map by either creating new match
     * competitor entities or using existing ones retrieved from the repository, if available. It filters
     * competitors by the specified club identifier when applicable.
     *
     * @param matchCompetitors the list of match competitor DTOs containing details for each match competitor
     * @param competitorMap    a map of UUIDs to Competitor entities used to map DTOs to existing competitors
     * @param clubIdentifier   the identifier of the club used to filter match competitors by their club reference
     * @return a map of match competitor UUIDs to their corresponding MatchCompetitor entities. If a required
     * competitor or match competitor cannot be found, an empty map is returned
     */
    protected Map<UUID, MatchCompetitor> initMatchCompetitorEntities(List<MatchCompetitorDto> matchCompetitors,
                                                                     Map<UUID, Competitor> competitorMap,
                                                                     ClubIdentifier clubIdentifier) {

        Map<UUID, MatchCompetitor> matchCompetitorMap = new HashMap<>();
        if (matchCompetitors != null) {

            // Initialise and accumulate match competitors from DTOs
            for (MatchCompetitorDto matchCompetitorDto : matchCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match competitor entity if present
                Optional<MatchCompetitor> optionalMatchCompetitorEntity = Optional.empty();
                if (matchCompetitorDto.getId() != null) {
                    optionalMatchCompetitorEntity =
                            matchCompetitorRepository.findById(matchCompetitorDto.getId());
                }

                // Initialise the match competitor entity from DTO or create a new entity
                MatchCompetitor matchCompetitorEntity = optionalMatchCompetitorEntity
                        .orElse(new MatchCompetitor());

                // Filter by club reference if specified
                if ((matchCompetitorDto.getMatch() != null) && (matchCompetitorEntity.getMatch().getClub() != null)) {
                    if ((clubIdentifier != null) && (!clubIdentifier.equals(ClubIdentifier.UNKNOWN))) {
                        if (!clubIdentifier.getName().equals(matchCompetitorDto.getMatch().getClub().getAbbreviation())) {
                            continue;
                        }
                    }
                }

                // Update the map of match competitors
                matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorEntity);
            }
        }

        return matchCompetitorMap;
    }

    /**
     * Initialises and maps match stage competitor DTOs to their corresponding `MatchStageCompetitor` entities.
     * This method processes the provided list of match stage competitor DTOs, matches them with existing entities
     * (if present), or creates new entities as required. If a club identifier is specified, filtering is applied
     * to include only the match stage competitors associated with the specified club.
     *
     * @param matchStageCompetitors a list of `MatchStageCompetitorDto` objects representing the input data for match stage competitors.
     * @param competitorMap         a map of `UUID` to `Competitor` entities used to resolve competitors by UUID.
     * @param clubIdentifier        the `ClubIdentifier` to filter match stage competitors based on club information, or `null` to skip filtering.
     * @return a map of `UUID` to `MatchStageCompetitor` entities representing the initialised and mapped match stage competitors.
     */
    protected Map<UUID, MatchStageCompetitor> initMatchStageCompetitorEntities(List<MatchStageCompetitorDto> matchStageCompetitors,
                                                                               Map<UUID, Competitor> competitorMap,
                                                                               ClubIdentifier clubIdentifier) {

        Map<UUID, MatchStageCompetitor> matchStageCompetitorMap = new HashMap<>();
        if (matchStageCompetitors != null) {
            // Initialises and accumulates match stage competitors from DTOs
            for (MatchStageCompetitorDto matchStageCompetitorDto : matchStageCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchStageCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match stage competitor entity if present
                Optional<MatchStageCompetitor> optionalMatchStageEntity = Optional.empty();
                if (matchStageCompetitorDto.getId() != null) {
                    optionalMatchStageEntity =
                            matchStageCompetitorRepository.findById(matchStageCompetitorDto.getId());
                }

                // Initialises the match stage competitor entity from DTO or create a new entity
                MatchStageCompetitor matchStageCompetitorEntity = optionalMatchStageEntity
                        .orElse(new MatchStageCompetitor());

                // Filter by club reference if specified
                if ((clubIdentifier != null) && (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
                    if (matchStageCompetitorDto.getClub() != null) {
                        if (!clubIdentifier.getName().equals(matchStageCompetitorDto.getClub().getName())) {
                            continue;
                        }
                    }
                }

                // Update the map of match stage competitors
                matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorEntity);
            }
        }

        return matchStageCompetitorMap;
    }
}
