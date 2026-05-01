package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.common.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.common.dto.*;
import za.co.hpsc.web.models.ipsc.common.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
import za.co.hpsc.web.services.*;

import java.util.*;

@Slf4j
@Service
public class DomainServiceImpl implements DomainService {
    protected final ClubEntityService clubEntityService;
    protected final CompetitorEntityService competitorEntityService;
    protected final MatchEntityService matchEntityService;
    protected final MatchStageEntityService matchStageEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    public DomainServiceImpl(ClubEntityService clubEntityService, CompetitorEntityService competitorEntityService,
                             MatchEntityService matchEntityService, MatchStageEntityService matchStageEntityService,
                             MatchCompetitorEntityService matchCompetitorEntityService,
                             MatchStageCompetitorEntityService matchStageCompetitorEntityService) {
        this.clubEntityService = clubEntityService;
        this.competitorEntityService = competitorEntityService;
        this.matchEntityService = matchEntityService;
        this.matchStageEntityService = matchStageEntityService;
        this.matchCompetitorEntityService = matchCompetitorEntityService;
        this.matchStageCompetitorEntityService = matchStageCompetitorEntityService;
    }

    @Override
    public Optional<DtoMapping> initMatchEntities(MatchResultsDto matchResults, String filterClubAbbreviation, String matchClubAbbreviation) {

        if ((matchResults == null) || (matchResults.getMatch() == null)) {
            return Optional.empty();
        }

        // Find the club to filter the results by from the provided filter abbreviation if specified
        ClubIdentifier filterClubIdentifier = ClubIdentifier.getByAbbreviation(filterClubAbbreviation)
                .orElse(null);

        // Find the club entity from the match results or the provided match club abbreviation
        ClubIdentifier matchClubIdentifier = ClubIdentifier.getByAbbreviation(matchClubAbbreviation)
                .orElse(null);
        ClubDto matchClub = matchResults.getClub();
        Optional<ClubDto> optionalClubDto = Optional.empty();
        if (matchClub != null) {
            optionalClubDto = initClubEntity(matchClub);
        }
        ClubDto clubDto = optionalClubDto.orElseGet(() -> initClubEntity(matchClubIdentifier)
                .orElse(null));

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
            DtoMapping dtoMapping = new DtoMapping();
            dtoMapping.setClub(clubDto);
            dtoMapping.setMatch(matchDto);
            dtoMapping.setCompetitorMap(competitorMap);
            dtoMapping.setMatchStageMap(matchStageMap);
            dtoMapping.setMatchCompetitorMap(matchCompetitorMap);
            dtoMapping.setMatchStageCompetitorMap(matchStageCompetitorMap);

            return Optional.of(dtoMapping);
        }

        return Optional.empty();
    }

    // TODO: add Javadoc
    @Override
    public Optional<MatchOnlyResultsDto> initMatchOnlyEntities(MatchOnlyDto matchOnlyDto) {
        if (matchOnlyDto == null) {
            return Optional.empty();
        }

        ClubDto clubDto = matchOnlyDto.getClub();
        if (clubDto == null) {
            clubDto = initClubEntity(matchOnlyDto.getClubName()).orElse(null);
        }

        MatchOnlyResultsDto matchOnlyResultsDto = new MatchOnlyResultsDto();
        matchOnlyResultsDto.setMatch(matchOnlyDto);
        matchOnlyResultsDto.setClub(clubDto);

        return Optional.of(matchOnlyResultsDto);
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
        if (clubDto == null) {
            return Optional.empty();
        }

        // Initialise the club entity from DTO or create a new entity
        Optional.ofNullable(clubDto.getId())
                .flatMap(clubEntityService::findClubById)
                .ifPresent(club -> clubDto.setId(club.getId()));

        // Add attributes to the club
        return Optional.of(clubDto);
    }

    /**
     * Initialises and retrieves a Club entity based on the given ClubIdentifier.
     *
     * @param clubIdentifier the identifier used to look up the club;
     * @return an Optional containing the club entity if found, or an empty Optional
     * if no match is found
     */
    protected Optional<ClubDto> initClubEntity(ClubIdentifier clubIdentifier) {
        if ((clubIdentifier == null) || (IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
            return Optional.empty();
        }

        // Find the club entity if present
        return initClubEntity(clubIdentifier.getName(), clubIdentifier.getAbbreviation());
    }

    // TODO: add Javadoc
    protected Optional<ClubDto> initClubEntity(String clubName) {
        if (clubName == null) {
            return Optional.empty();
        }

        // Find the club entity if present
        Optional<Club> optionalClub = clubEntityService.findClubByNameOrAbbreviation(clubName);
        ClubDto clubDto = null;
        if (optionalClub.isPresent()) {
            clubDto = new ClubDto(optionalClub.get());
        }

        return Optional.ofNullable(clubDto);
    }

    // TODO: add Javadoc
    protected Optional<ClubDto> initClubEntity(String clubName, String clubAbbreviation) {
        if (clubName == null) {
            return Optional.empty();
        }

        // Find the club entity if present
        Optional<Club> optionalClub = clubEntityService.findClubByNameOrAbbreviation(clubName, clubAbbreviation);
        ClubDto clubDto = null;
        if (optionalClub.isPresent()) {
            clubDto = new ClubDto(optionalClub.get());
        }

        return Optional.ofNullable(clubDto);
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
        // Initialise the match entity from DTO or create a new entity
        Optional.ofNullable(matchDto)
                .map(MatchDto::getId)
                .flatMap(matchCompetitorEntityService::findMatchCompetitorById)
                .ifPresent(match -> matchDto.setId(match.getId()));

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
        if (competitorDtoList == null) {
            return new HashMap<>();
        }

        // Filter out null entries from the competitor DTO list
        List<CompetitorDto> filteredCompetitorDtoList = competitorDtoList.stream()
                .filter(Objects::nonNull).toList();

        // Initialise and accumulate competitor entities from DTOs
        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        filteredCompetitorDtoList.forEach(competitorDto -> {
            // Initialise the competitor entity from DTO or create a new entity
            Optional.ofNullable(competitorDto.getId())
                    .flatMap(competitorEntityService::findCompetitorById)
                    .ifPresent(competitor -> competitor.setId(competitorDto.getId()));

            // Update the map of competitors
            competitorMap.put(competitorDto.getUuid(), competitorDto);
        });

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
        if (matchStageDtoList == null) {
            return new HashMap<>();
        }

        // Filter out null entries from the match stage DTO list
        // and also filter by match association
        List<MatchStageDto> filteredMatchStageDtoList = matchStageDtoList.stream()
                .filter(Objects::nonNull)
                .filter(matchStageDto -> matchStageDto.getMatch() != null)
                .filter(matchCompetitorDto -> matchCompetitorDto.getMatch().getUuid()
                        .equals(matchEntity.getUuid()))
                .toList();

        // Initialise and accumulate match stages from DTOs
        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        filteredMatchStageDtoList.forEach(matchStageDto -> {
            // Initialise the match stage entity from DTO or create a new entity
            Optional.ofNullable(matchStageDto.getId())
                    .flatMap(matchStageEntityService::findMatchStageById)
                    .ifPresent(ipscMatchStage -> matchStageDto.setId(ipscMatchStage.getId()));

            // Update the map of match stages
            matchStageMap.put(matchStageDto.getUuid(), matchStageDto);
        });

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
     * @param clubIdentifier         the `ClubIdentifier` to filter match stage competitors
     *                               based on club information, or `null` to skip filtering.
     * @return a map of match competitor UUIDs to their corresponding MatchCompetitor entities.
     * If a required competitor or match competitor cannot be found, an empty map is returned
     */
    protected Map<UUID, MatchCompetitorDto> initMatchCompetitorEntities(List<MatchCompetitorDto> matchCompetitorDtoList,
                                                                        MatchDto matchDto,
                                                                        Map<UUID, CompetitorDto> competitorMap, ClubIdentifier clubIdentifier) {

        if (matchCompetitorDtoList == null) {
            return new HashMap<>();
        }

        // Filter out null entries from the match competitor DTO list
        // and also filter by match association
        List<MatchCompetitorDto> filteredMapCompetitorDtoList = matchCompetitorDtoList.stream()
                .filter(Objects::nonNull)
                .filter(matchCompetitorDto -> matchCompetitorDto.getMatch() != null)
                .filter(matchCompetitorDto -> matchCompetitorDto.getMatch().getUuid()
                        .equals(matchDto.getUuid()))
                .toList();

        // Initialise and accumulate match competitors from DTOs
        Map<UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
        for (MatchCompetitorDto matchCompetitorDto : filteredMapCompetitorDtoList) {
            // Find the competitor entity
            if (isCompetitorMissing(competitorMap, matchCompetitorDto.getCompetitor())) {
                return new HashMap<>();
            }

            // Initialise the match competitor entity from DTO or create a new entity
            Optional.ofNullable(matchCompetitorDto.getId())
                    .flatMap(matchCompetitorEntityService::findMatchCompetitorById)
                    .ifPresent(matchCompetitor -> matchCompetitorDto.setId(matchCompetitor.getId()));

            // Filter by club reference if specified
            if (isExcludedByClubFilter(clubIdentifier, matchCompetitorDto.getClub())) {
                continue;
            }

            // Update the map of match competitors
            matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorDto);
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

        // Filters out null entries from the match stage competitor DTO list
        Map<UUID, MatchStageCompetitorDto> matchStageCompetitorMap = new HashMap<>();
        List<MatchStageDto> filteredMatchStageDtoList = matchStageMap.values().stream()
                .filter(Objects::nonNull).toList();

        // Initialise and accumulate match competitors from DTOs
        for (MatchStageDto matchStageDto : filteredMatchStageDtoList) {

            // Filter the match stage competitor DTO list by match stage association
            List<MatchStageCompetitorDto> filteredMatchCompetitors = matchStageCompetitorDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(matchStageCompetitorDto ->
                            matchStageCompetitorDto.getMatchStage() != null)
                    .filter(matchStageCompetitorDto ->
                            matchStageCompetitorDto.getMatchStage().getUuid().equals(matchStageDto.getUuid()))
                    .toList();

            for (MatchStageCompetitorDto matchStageCompetitorDto : filteredMatchCompetitors) {
                // Find the competitor entity
                if (isCompetitorMissing(competitorMap, matchStageCompetitorDto.getCompetitor())) {
                    return new HashMap<>();
                }

                // Initialises the match stage competitor entity from DTO or create a new entity
                Optional.ofNullable(matchStageCompetitorDto.getId())
                        .flatMap(matchCompetitorEntityService::findMatchCompetitorById)
                        .ifPresent(matchStageCompetitor -> matchStageCompetitorDto.setId(matchStageCompetitor.getId()));

                // Filter by club reference if specified
                if (isExcludedByClubFilter(clubIdentifier, matchStageCompetitorDto.getClub())) {
                    continue;
                }

                // Update the map of match stage competitors
                matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorDto);
            }
        }

        return matchStageCompetitorMap;
    }

    /**
     * Returns {@code true} when the given DTO's club should be excluded by the active club filter.
     * A DTO is excluded when a non-null, non-ignored {@code clubIdentifier} is provided,
     * and it does not match the DTO's own club value.
     *
     * @param clubIdentifier the filter club identifier, or {@code null} to skip filtering
     * @param dtoClub        the club identifier carried by the DTO being evaluated
     * @return {@code true} if the DTO must be skipped; {@code false} otherwise
     */
    protected boolean isExcludedByClubFilter(ClubIdentifier clubIdentifier, ClubIdentifier dtoClub) {
        return (clubIdentifier != null)
                && (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))
                && (!clubIdentifier.equals(dtoClub));
    }

    /**
     * Returns {@code true} when the competitor is absent from the competitor map,
     * for example, the DTO is null, its UUID is null, or no matching entry exists in the map.
     *
     * @param competitorMap a map of `UUID` to `CompetitorDto` used to resolve competitors by UUID.
     * @param competitorDto the competitor DTO to check.
     * @return {@code true} if the competitor is missing; {@code false} if it exists.
     */
    protected boolean isCompetitorMissing(Map<UUID, CompetitorDto> competitorMap,
                                          CompetitorDto competitorDto) {
        return (competitorDto == null) || (competitorDto.getUuid() == null) ||
                (competitorMap.get(competitorDto.getUuid()) == null);
    }
}
