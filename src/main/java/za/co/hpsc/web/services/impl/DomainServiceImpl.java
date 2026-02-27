package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.DomainService;

import java.util.*;

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

    @Override
    public Optional<DtoToEntityMapping> initMatchEntities(MatchResultsDto matchResults,
                                                          String filterClubAbbreviation) {
        if ((matchResults == null) || (matchResults.getMatch() == null)) {
            return Optional.empty();
        }

        // Find the club entity based on the filter club abbreviation if specified;
        // otherwise, use the club from match results
        ClubIdentifier filterClubIdentifier = ClubIdentifier.getByName(filterClubAbbreviation)
                .orElse(null);
        Optional<ClubDto> optionalClub = ((matchResults.getClub() != null) ?
                initClubEntity(matchResults.getClub()) : initClubEntity(filterClubIdentifier));
        ClubDto clubDto = optionalClub.orElse(null);
        optionalClub.ifPresent(c -> clubDto.setId(c.getId()));

        // Initialise the match DTO based on match results
        Optional<MatchDto> optionalMatchDto = initMatchEntity(matchResults.getMatch());

        if (optionalMatchDto.isPresent()) {
            // Caches of DTOs with their UUIDs
            MatchDto matchDto = optionalMatchDto.get();
            Map<UUID, CompetitorDto> competitorMap =
                    initCompetitorEntities(matchResults.getCompetitors());
            Map<UUID, MatchStageDto> matchStageMap =
                    initMatchStageEntities(matchResults.getStages(), matchDto);

            // Generate caches of DTOs with their UUIDs
            Map<UUID, MatchCompetitorDto> matchCompetitorMap =
                    initMatchCompetitorEntities(matchResults.getMatchCompetitors(), matchDto,
                            competitorMap, filterClubIdentifier);
            Map<UUID, MatchStageCompetitorDto> matchStageCompetitorMap =
                    initMatchStageCompetitorEntities(matchResults.getMatchStageCompetitors(),
                            matchStageMap, competitorMap, filterClubIdentifier);

            // Initialise the mapping of the DTO to an entity
            DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
            dtoToEntityMapping.setClub(clubDto);
            dtoToEntityMapping.setMatch(matchDto);
            dtoToEntityMapping.setCompetitorMap(competitorMap);
            dtoToEntityMapping.setMatchStageMap(matchStageMap);
            dtoToEntityMapping.setMatchCompetitorMap(matchCompetitorMap);
            dtoToEntityMapping.setMatchStageCompetitorMap(matchStageCompetitorMap);

            return Optional.of(dtoToEntityMapping);
        }

        return Optional.empty();
    }

    /**
     * Initialises a Club entity based on the provided ClubDto.
     * If the ClubDto contains a valid ID, attempts to find the existing Club entity
     * in the repository.
     * If not found, creates a new Club entity, populates it with data from the DTO, and returns it.
     *
     * @param clubDto the data transfer object containing data to initialise or update a Club entity
     * @return an Optional containing the initialised Club entity if the input is valid,
     * or an empty Optional if the input is null or invalid
     */
    protected Optional<ClubDto> initClubEntity(ClubDto clubDto) {
        if ((clubDto != null) && (clubDto.getId() != null)) {
            // Find the club entity if present
            Optional<Club> optionalClubEntity = clubRepository.findById(clubDto.getId());

            // Initialise the club entity from DTO or create a new entity
            optionalClubEntity.ifPresent(competitor ->
                    clubDto.setId(competitor.getId()));

            // Add attributes to the club
            return Optional.of(clubDto);
        }

        return Optional.empty();
    }

    /**
     * Initialises and retrieves a Club entity based on the given ClubIdentifier.
     *
     * @param clubIdentifier the identifier used to look up the club;
     * @return an Optional containing the club entity if found, or an empty Optional
     * if no match is found
     */
    protected Optional<ClubDto> initClubEntity(ClubIdentifier clubIdentifier) {
        if (clubIdentifier != null) {
            // Find the club entity if present
            ClubDto clubDto = new ClubDto();
            clubRepository.findByAbbreviation(clubIdentifier.getName())
                    .ifPresent(club -> clubDto.setId(club.getId()));
            if (clubDto.getId() != null) {
                return Optional.of(clubDto);
            } else {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Initialises an instance of the {@code IpscMatch} entity using the provided DTO and
     * links it to the given club entity.
     * If a match with the specified ID in the DTO exists in the repository, it is
     * retrieved and updated; otherwise, a new {@code IpscMatch} entity is created and initialised.
     *
     * @param matchDto the data transfer object containing match details and an optional
     *                 match ID for lookup.
     * @return an {@code Optional} containing the initialised {@code IpscMatch} entity.
     */
    protected Optional<MatchDto> initMatchEntity(MatchDto matchDto) {
        // Find the match entity if present
        Optional<IpscMatch> optionalIpscMatchEntity = Optional.empty();
        if ((matchDto != null) && (matchDto.getId() != null)) {
            optionalIpscMatchEntity = ipscMatchRepository.findByIdWithClubStages(matchDto.getId());
        }

        // Initialise the match entity from DTO or create a new entity
        optionalIpscMatchEntity.ifPresent(match -> matchDto.setId(match.getId()));

        return Optional.ofNullable(matchDto);
    }

    /**
     * Initialises a map of Competitor entities from a list of CompetitorDto objects.
     * Each CompetitorDto is used to either find an existing Competitor entity or create a new one.
     * The map is constructed with the UUID as the key and the corresponding Competitor entity
     * as the value.
     *
     * @param competitorDtoList the list of CompetitorDto objects containing the data used to
     *                          initialise or update Competitor entities
     * @return a map where the key is the UUID from the CompetitorDto and the value is the
     * corresponding Competitor entity
     */
    protected Map<UUID, CompetitorDto> initCompetitorEntities(List<CompetitorDto> competitorDtoList) {
        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
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
                        optionalCompetitorEntity.ifPresent(competitor ->
                                competitor.setId(competitorDto.getId()));

                        // Update the map of competitors
                        competitorMap.put(competitorDto.getUuid(), competitorDto);
                    });
        }

        return competitorMap;
    }

    /**
     * Initialises and maps IPSC match stage entities based on the provided list of
     * MatchStageDto objects and the associated IpscMatch entity.
     * If the DTO contains an ID, it attempts to retrieve the corresponding entity
     * from the repository; otherwise, it creates a new entity.
     * Each stage is then linked to the associated match entity and added to the map.
     *
     * @param matchStageDtoList the list of MatchStageDto objects containing data used to
     *                          initialise or update the match stage entities.
     * @param matchEntity       the IpscMatch entity that the match stages are associated with.
     * @return a map where the keys are the UUIDs from the MatchStageDto objects and the values
     * are the initialised or updated IpscMatchStage entities.
     */
    protected Map<UUID, MatchStageDto> initMatchStageEntities(List<MatchStageDto> matchStageDtoList,
                                                              MatchDto matchEntity) {
        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        if (matchStageDtoList != null) {

            // Initialise and accumulate match stages from DTOs
            List<MatchStageDto> filteredMatchStageDtoList = matchStageDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(matchStageDto -> matchStageDto.getMatch() != null)
                    .filter(matchCompetitorDto -> matchCompetitorDto.getMatch().getUuid()
                            .equals(matchEntity.getUuid()))
                    .toList();
            filteredMatchStageDtoList.forEach(matchDto -> {
                // Find the match stage entity if present
                Optional<IpscMatchStage> optionalIpscMatchStageEntity = Optional.empty();
                if (matchDto.getId() != null) {
                    optionalIpscMatchStageEntity = ipscMatchStageRepository.findById(matchDto.getId());
                }

                // Initialise the match stage entity from DTO or create a new entity
                IpscMatchStage matchStageEntity = optionalIpscMatchStageEntity.orElse(null);
                MatchStageDto matchStageDto = null;
                if (matchStageEntity != null) {
                    matchStageDto = new MatchStageDto(matchStageEntity);
                }

                // Update the map of match stages
                matchStageMap.put(matchDto.getUuid(), matchStageDto);
            });
        }

        return matchStageMap;
    }

    /**
     * Initialises a map of match competitor entities based on the provided list of
     * match competitor DTOs, a competitor map, and a club identifier. This method populates the
     * map by either creating new match competitor entities or using existing ones retrieved
     * from the repository, if available.
     * It filters competitors by the specified club identifier when applicable.
     *
     * @param matchCompetitorDtoList the list of match competitor DTOs containing details for each
     *                               match competitor
     * @param matchDto               the match entity with which the match competitors are associated
     * @param competitorMap          a map of UUIDs to Competitor entities used to map DTOs to
     *                               existing competitors
     * @param clubIdentifier         the identifier of the club used to filter match competitors
     *                               by their club reference
     * @return a map of match competitor UUIDs to their corresponding MatchCompetitor entities.
     * If a required competitor or match competitor cannot be found, an empty map is returned
     */
    protected Map<UUID, MatchCompetitorDto> initMatchCompetitorEntities(List<MatchCompetitorDto> matchCompetitorDtoList,
                                                                        MatchDto matchDto,
                                                                        Map<UUID, CompetitorDto> competitorMap,
                                                                        ClubIdentifier clubIdentifier) {

        Map<UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
        if (matchCompetitorDtoList != null) {

            // Initialise and accumulate match competitors from DTOs
            List<MatchCompetitorDto> filteredMapCompetitorDtoList = matchCompetitorDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(matchCompetitorDto -> matchCompetitorDto.getMatch() != null)
                    .filter(matchCompetitorDto -> matchCompetitorDto.getMatch().getUuid()
                            .equals(matchDto.getUuid()))
                    .toList();
            for (MatchCompetitorDto matchCompetitorDto : filteredMapCompetitorDtoList) {
                // Find the competitor entity
                CompetitorDto competitorDto = competitorMap.get(matchCompetitorDto.getCompetitor().getUuid());
                if (competitorDto == null) {
                    return new HashMap<>();
                }

                // Find the match competitor entity if present
                Optional<MatchCompetitor> optionalMatchCompetitorEntity = Optional.empty();
                if (matchCompetitorDto.getId() != null) {
                    optionalMatchCompetitorEntity = matchCompetitorRepository
                            .findById(matchCompetitorDto.getId());
                }

                // Initialise the match competitor entity from DTO or create a new entity
                optionalMatchCompetitorEntity.ifPresent(matchCompetitor ->
                        matchCompetitorDto.setId(matchCompetitor.getId()));

                // Filter by club reference if specified
/*
                if (matchCompetitorDto.getMatch() != null) {
                    if ((clubIdentifier != null) && (!clubIdentifier.equals(ClubIdentifier.UNKNOWN))) {
                        String clubName = matchEntity.getClub().getName();
                        if (!clubName.equalsIgnoreCase(clubIdentifier.getName())) {
                            continue;
                        }
                    }
                }
*/

                // Update the map of match competitors
                matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorDto);
            }
        }

        return matchCompetitorMap;
    }

    /**
     * Initialises and maps match stage competitor DTOs to their corresponding
     * `MatchStageCompetitor` entities.
     * This method processes the provided list of match stage competitor DTOs, matches them with
     * existing entities (if present), or creates new entities as required.
     * If a club identifier is specified, filtering is applied to include only the match
     * stage competitors associated with the specified club.
     *
     * @param matchStageCompetitorDtoList a list of `MatchStageCompetitorDto` objects representing
     *                                    the input data for match stage competitors.
     * @param competitorMap               a map of `UUID` to `Competitor` entities used to resolve
     *                                    competitors by UUID.
     * @param clubIdentifier              the `ClubIdentifier` to filter match stage competitors
     *                                    based on club information, or `null` to skip filtering.
     * @return a map of `UUID` to `MatchStageCompetitor` entities representing the initialised and
     * mapped match stage competitors.
     */
    protected Map<UUID, MatchStageCompetitorDto> initMatchStageCompetitorEntities(List<MatchStageCompetitorDto> matchStageCompetitorDtoList,
                                                                                  Map<UUID, MatchStageDto> matchStageMap,
                                                                                  Map<UUID, CompetitorDto> competitorMap,
                                                                                  ClubIdentifier clubIdentifier) {

        if (matchStageCompetitorDtoList == null) {
            return new HashMap<>();
        }

        Map<UUID, MatchStageCompetitorDto> matchStageCompetitorMap = new HashMap<>();
        List<MatchStageDto> filteredMatchStageDtoList = matchStageMap.values().stream()
                .filter(Objects::nonNull).toList();
        for (MatchStageDto matchStageDto : filteredMatchStageDtoList) {

            // Initialises and accumulates match stage competitors from DTOs
            List<MatchStageCompetitorDto> filteredMatchCompetitors = matchStageCompetitorDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(matchStageCompetitorDto ->
                            matchStageCompetitorDto.getMatchStage() != null)
                    .filter(matchStageCompetitorDto ->
                            matchStageCompetitorDto.getMatchStage().getUuid().equals(matchStageDto.getUuid()))
                    .toList();
            for (MatchStageCompetitorDto matchStageCompetitorDto : filteredMatchCompetitors) {
                // Find the competitor entity
                CompetitorDto competitorDto =
                        competitorMap.get(matchStageCompetitorDto.getCompetitor().getUuid());
                if (competitorDto == null) {
                    return new HashMap<>();
                }

                // Find the match stage competitor entity if present
                Optional<MatchStageCompetitor> optionalMatchStageEntity = Optional.empty();
                if (matchStageCompetitorDto.getId() != null) {
                    optionalMatchStageEntity =
                            matchStageCompetitorRepository.findById(matchStageCompetitorDto.getId());
                }

                // Initialises the match stage competitor entity from DTO or create a new entity
                MatchStageCompetitor matchStageCompetitorEntity = optionalMatchStageEntity.orElse(null);
                if (matchStageCompetitorEntity == null) {
                    continue;
                }

                // Filter by club reference if specified
                if ((clubIdentifier != null) &&
                        (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
                    if (matchStageCompetitorDto.getClub() != null) {
                        if (!clubIdentifier.getName().equals(matchStageCompetitorDto.getClub().getName())) {
                            continue;
                        }
                    }
                }

                // Update the map of match stage competitors
                matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorDto);
            }
        }

        return matchStageCompetitorMap;
    }
}
