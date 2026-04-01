package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchCompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageCompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DomainServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private MatchStageCompetitorRepository matchStageCompetitorRepository;

    @InjectMocks
    private DomainServiceImpl domainService;

    private MatchDto buildMatchDto() {
        MatchDto matchDto = new MatchDto();
        matchDto.setName("League Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 3, 31, 8, 0));
        return matchDto;
    }

    private ClubDto buildClubDto(String name, String abbreviation) {
        ClubDto clubDto = new ClubDto();
        clubDto.setName(name);
        clubDto.setAbbreviation(abbreviation);
        return clubDto;
    }

    private CompetitorDto buildCompetitorDto(String firstName, String lastName) {
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(firstName);
        competitorDto.setLastName(lastName);
        competitorDto.setCompetitorNumber(firstName + "-001");
        return competitorDto;
    }

    private MatchStageDto buildMatchStageDto(MatchDto matchDto, int stageNumber) {
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(stageNumber);
        stageDto.setStageName("Stage " + stageNumber);
        return stageDto;
    }

    private MatchCompetitorDto buildMatchCompetitorDto(MatchDto matchDto, CompetitorDto competitorDto) {
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setMatch(matchDto);
        dto.setCompetitor(competitorDto);
        dto.setClub(ClubIdentifier.HPSC);
        return dto;
    }

    private MatchStageCompetitorDto buildMatchStageCompetitorDto(MatchStageDto stageDto,
                                                                 CompetitorDto competitorDto) {
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setMatchStage(stageDto);
        dto.setCompetitor(competitorDto);
        dto.setClub(ClubIdentifier.HPSC);
        return dto;
    }

    // Test Group: initMatchEntities(...) public methods
    @Test
    public void testInitMatchEntitiesOverload_whenMatchResultsNull_thenEmpty() {
        Optional<DtoMapping> result = domainService.initMatchEntities(null, "HPSC");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntitiesOverload_whenMatchNull_thenEmpty() {
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(null);

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, "HPSC");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntitiesOverload_whenValidMinimalInput_thenReturnsMapping() {
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        MatchDto matchDto = buildMatchDto();
        matchResultsDto.setMatch(matchDto);

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, "HPSC");

        assertTrue(result.isPresent());
        assertEquals(matchDto.getUuid(), result.get().getMatch().getUuid());
    }

    @Test
    public void initMatchEntitiesUsesIdentifierNameLookupWhenMatchClubMissing() {
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(buildMatchDto());
        matchResultsDto.setClub(null);

        Club clubEntity = new Club();
        clubEntity.setId(11L);
        clubEntity.setName("Holster Club");
        clubEntity.setAbbreviation("HPSC");
        when(clubRepository.findByAbbreviation(ClubIdentifier.HPSC.getName())).thenReturn(Optional.of(clubEntity));

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, "HPSC", null);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getClub());
        assertEquals(11L, result.get().getClub().getId());
        verify(clubRepository).findByAbbreviation(ClubIdentifier.HPSC.getName());
    }

    @Test
    public void testInitMatchEntitiesThreeArgs_whenFilterClubProvided_filtersMatchCompetitors() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("John", "Doe");

        MatchCompetitorDto included = buildMatchCompetitorDto(matchDto, competitorDto);
        included.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto excluded = buildMatchCompetitorDto(matchDto, competitorDto);
        excluded.setClub(ClubIdentifier.SOSC);

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setCompetitors(List.of(competitorDto));
        matchResultsDto.setMatchCompetitors(List.of(included, excluded));

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, null, "HPSC");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMatchCompetitorMap().size());
        assertTrue(result.get().getMatchCompetitorMap().containsKey(included.getUuid()));
    }

    @Test
    public void testInitMatchEntitiesThreeArgs_whenFilterClubProvided_filtersMatchStageCompetitors() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("Jane", "Doe");
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);

        MatchStageCompetitorDto included = buildMatchStageCompetitorDto(stageDto, competitorDto);
        included.setClub(ClubIdentifier.HPSC);

        MatchStageCompetitorDto excluded = buildMatchStageCompetitorDto(stageDto, competitorDto);
        excluded.setClub(ClubIdentifier.SOSC);

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setCompetitors(List.of(competitorDto));
        matchResultsDto.setStages(List.of(stageDto));
        matchResultsDto.setMatchStageCompetitors(List.of(included, excluded));

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, null, "HPSC");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMatchStageCompetitorMap().size());
        assertTrue(result.get().getMatchStageCompetitorMap().containsKey(included.getUuid()));
    }

    @Test
    public void testInitMatchEntitiesThreeArgs_whenCompetitorMissingForMatchCompetitor_returnsEmptyMapForMatchCompetitors() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorNotInList = buildCompetitorDto("Ghost", "Rider");

        MatchCompetitorDto matchCompetitorDto = buildMatchCompetitorDto(matchDto, competitorNotInList);

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setCompetitors(new ArrayList<>());
        matchResultsDto.setMatchCompetitors(List.of(matchCompetitorDto));

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, null, null);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchCompetitorMap().isEmpty());
    }

    @Test
    public void testInitMatchEntitiesThreeArgs_whenCompetitorMissingForStageCompetitor_returnsEmptyMapForStageCompetitors() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorNotInList = buildCompetitorDto("Ghost", "Rider");

        MatchStageCompetitorDto stageCompetitorDto = buildMatchStageCompetitorDto(stageDto, competitorNotInList);

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setCompetitors(new ArrayList<>());
        matchResultsDto.setStages(List.of(stageDto));
        matchResultsDto.setMatchStageCompetitors(List.of(stageCompetitorDto));

        Optional<DtoMapping> result = domainService.initMatchEntities(matchResultsDto, null, null);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageCompetitorMap().isEmpty());
    }

    // Test Group: initClubEntity(ClubDto)
    @Test
    public void testInitClubEntityFromDto_whenNull_thenEmpty() {
        Optional<ClubDto> result = domainService.initClubEntity((ClubDto) null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitClubEntityFromDto_whenNoId_thenReturnsSameDtoWithoutLookup() {
        ClubDto clubDto = buildClubDto("No Id Club", "NIC");
        clubDto.setId(null);

        Optional<ClubDto> result = domainService.initClubEntity(clubDto);

        assertTrue(result.isPresent());
        assertEquals("No Id Club", result.get().getName());
        verifyNoInteractions(clubRepository);
    }

    @Test
    public void testInitClubEntityFromDto_whenIdFound_thenKeepsResolvedId() {
        ClubDto clubDto = buildClubDto("Known Club", "KC");
        clubDto.setId(10L);

        Club clubEntity = new Club();
        clubEntity.setId(10L);
        when(clubRepository.findById(10L)).thenReturn(Optional.of(clubEntity));

        Optional<ClubDto> result = domainService.initClubEntity(clubDto);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        verify(clubRepository).findById(10L);
    }

    @Test
    public void testInitClubEntityFromDto_whenIdNotFound_thenStillReturnsDto() {
        ClubDto clubDto = buildClubDto("Unknown Club", "UC");
        clubDto.setId(99L);

        when(clubRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ClubDto> result = domainService.initClubEntity(clubDto);

        assertTrue(result.isPresent());
        assertEquals(99L, result.get().getId());
        verify(clubRepository).findById(99L);
    }

    // Test Group: initClubEntity(ClubIdentifier)
    @Test
    public void testInitClubEntityFromIdentifier_whenNull_thenEmpty() {
        Optional<ClubDto> result = domainService.initClubEntity((ClubIdentifier) null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitClubEntityFromIdentifier_whenExcludedUnknown_thenEmpty() {
        Optional<ClubDto> result = domainService.initClubEntity(ClubIdentifier.UNKNOWN);

        assertTrue(result.isEmpty());
    }

    @Test
    public void initClubEntityReturnsClubDtoWhenIdentifierNameIsFound() {
        Club clubEntity = new Club();
        clubEntity.setId(101L);
        clubEntity.setName("Holster Club");
        clubEntity.setAbbreviation("HPSC");

        when(clubRepository.findByAbbreviation(ClubIdentifier.HPSC.getName())).thenReturn(Optional.of(clubEntity));

        Optional<ClubDto> result = domainService.initClubEntity(ClubIdentifier.HPSC);

        assertTrue(result.isPresent());
        assertEquals(101L, result.get().getId());
        assertEquals("Holster Club", result.get().getName());
    }

    @Test
    public void initClubEntityReturnsEmptyWhenIdentifierNameIsNotFound() {
        when(clubRepository.findByAbbreviation(ClubIdentifier.SOSC.getName())).thenReturn(Optional.empty());

        Optional<ClubDto> result = domainService.initClubEntity(ClubIdentifier.SOSC);

        assertTrue(result.isEmpty());
    }

    // Test Group: initMatchEntity
    @Test
    public void testInitMatchEntity_whenNull_thenEmpty() {
        Optional<MatchDto> result = domainService.initMatchEntity(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntity_whenNoId_thenReturnsDtoWithoutLookup() {
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(null);

        Optional<MatchDto> result = domainService.initMatchEntity(matchDto);

        assertTrue(result.isPresent());
        assertEquals(matchDto.getUuid(), result.get().getUuid());
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    public void testInitMatchEntity_whenIdFound_thenSetsResolvedId() {
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(5L);

        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setId(5L);
        when(ipscMatchRepository.findById(5L)).thenReturn(Optional.of(matchEntity));

        Optional<MatchDto> result = domainService.initMatchEntity(matchDto);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
        verify(ipscMatchRepository).findById(5L);
    }

    @Test
    public void testInitMatchEntity_whenIdNotFound_thenStillReturnsDto() {
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(55L);
        when(ipscMatchRepository.findById(55L)).thenReturn(Optional.empty());

        Optional<MatchDto> result = domainService.initMatchEntity(matchDto);

        assertTrue(result.isPresent());
        assertEquals(55L, result.get().getId());
    }

    // Test Group: initCompetitorEntities
    @Test
    public void testInitCompetitorEntities_whenNullList_thenEmptyMap() {
        Map<UUID, CompetitorDto> result = domainService.initCompetitorEntities(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitCompetitorEntities_whenListContainsNull_thenNullFilteredOut() {
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        List<CompetitorDto> list = new ArrayList<>();
        list.add(competitorDto);
        list.add(null);

        Map<UUID, CompetitorDto> result = domainService.initCompetitorEntities(list);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorDto.getUuid()));
    }

    @Test
    public void testInitCompetitorEntities_whenIdFound_thenPerformsRepositoryLookup() {
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        competitorDto.setId(7L);

        Competitor competitorEntity = new Competitor();
        competitorEntity.setId(7L);
        when(competitorRepository.findById(7L)).thenReturn(Optional.of(competitorEntity));

        Map<UUID, CompetitorDto> result = domainService.initCompetitorEntities(List.of(competitorDto));

        assertEquals(1, result.size());
        verify(competitorRepository).findById(7L);
    }

    @Test
    public void testInitCompetitorEntities_whenIdNotFound_thenStillAddsToMap() {
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        competitorDto.setId(77L);
        when(competitorRepository.findById(77L)).thenReturn(Optional.empty());

        Map<UUID, CompetitorDto> result = domainService.initCompetitorEntities(List.of(competitorDto));

        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorDto.getUuid()));
    }

    // Test Group: initMatchStageEntities
    @Test
    public void testInitMatchStageEntities_whenNullList_thenEmptyMap() {
        MatchDto matchDto = buildMatchDto();

        Map<UUID, MatchStageDto> result = domainService.initMatchStageEntities(null, matchDto);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageEntities_whenNullOrMismatchedStages_thenFilteredOut() {
        MatchDto matchDto = buildMatchDto();
        MatchDto otherMatch = buildMatchDto();

        MatchStageDto matchingStage = buildMatchStageDto(matchDto, 1);
        MatchStageDto nullMatchStage = new MatchStageDto();
        nullMatchStage.setMatch(null);
        MatchStageDto mismatchedStage = buildMatchStageDto(otherMatch, 2);

        List<MatchStageDto> input = new ArrayList<>();
        input.add(matchingStage);
        input.add(null);
        input.add(nullMatchStage);
        input.add(mismatchedStage);

        Map<UUID, MatchStageDto> result = domainService.initMatchStageEntities(input, matchDto);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(matchingStage.getUuid()));
    }

    @Test
    public void testInitMatchStageEntities_whenIdFound_thenPerformsRepositoryLookup() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        stageDto.setId(9L);

        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setId(9L);
        when(ipscMatchStageRepository.findById(9L)).thenReturn(Optional.of(stageEntity));

        Map<UUID, MatchStageDto> result = domainService.initMatchStageEntities(List.of(stageDto), matchDto);

        assertEquals(1, result.size());
        verify(ipscMatchStageRepository).findById(9L);
    }

    @Test
    public void testInitMatchStageEntities_whenIdNotFound_thenStillAddsToMap() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        stageDto.setId(19L);
        when(ipscMatchStageRepository.findById(19L)).thenReturn(Optional.empty());

        Map<UUID, MatchStageDto> result = domainService.initMatchStageEntities(List.of(stageDto), matchDto);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(stageDto.getUuid()));
    }

    // Test Group: initMatchCompetitorEntities
    @Test
    public void testInitMatchCompetitorEntities_whenNullList_thenEmptyMap() {
        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                null,
                buildMatchDto(),
                new HashMap<>(),
                null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchCompetitorEntities_whenMissingCompetitorInMap_thenReturnsEmptyMap() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto missingCompetitor = buildCompetitorDto("M", "C");
        MatchCompetitorDto dto = buildMatchCompetitorDto(matchDto, missingCompetitor);

        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                List.of(dto),
                matchDto,
                new HashMap<>(),
                null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchCompetitorEntities_whenValidAndIdFound_thenAddsAndLooksUpEntity() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        Map<UUID, CompetitorDto> competitorMap = Map.of(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto dto = buildMatchCompetitorDto(matchDto, competitorDto);
        dto.setId(31L);

        MatchCompetitor entity = new MatchCompetitor();
        entity.setId(31L);
        when(matchCompetitorRepository.findById(31L)).thenReturn(Optional.of(entity));

        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                List.of(dto),
                matchDto,
                competitorMap,
                null);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(dto.getUuid()));
        verify(matchCompetitorRepository).findById(31L);
    }

    @Test
    public void testInitMatchCompetitorEntities_whenFilterClubSet_thenOnlyMatchingClubIncluded() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        Map<UUID, CompetitorDto> competitorMap = Map.of(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto included = buildMatchCompetitorDto(matchDto, competitorDto);
        included.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto excluded = buildMatchCompetitorDto(matchDto, competitorDto);
        excluded.setClub(ClubIdentifier.SOSC);

        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                List.of(included, excluded),
                matchDto,
                competitorMap,
                ClubIdentifier.HPSC);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(included.getUuid()));
    }

    @Test
    public void testInitMatchCompetitorEntities_whenFilterClubIsExcludedIdentifier_thenNoFilteringApplied() {
        MatchDto matchDto = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        Map<UUID, CompetitorDto> competitorMap = Map.of(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto first = buildMatchCompetitorDto(matchDto, competitorDto);
        first.setClub(ClubIdentifier.HPSC);
        MatchCompetitorDto second = buildMatchCompetitorDto(matchDto, competitorDto);
        second.setClub(ClubIdentifier.SOSC);

        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                List.of(first, second),
                matchDto,
                competitorMap,
                ClubIdentifier.UNKNOWN);

        assertEquals(2, result.size());
    }

    @Test
    public void testInitMatchCompetitorEntities_whenMismatchedMatchUuid_thenFilteredOut() {
        MatchDto matchDto = buildMatchDto();
        MatchDto otherMatch = buildMatchDto();
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");
        Map<UUID, CompetitorDto> competitorMap = Map.of(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto dto = buildMatchCompetitorDto(otherMatch, competitorDto);

        Map<UUID, MatchCompetitorDto> result = domainService.initMatchCompetitorEntities(
                List.of(dto),
                matchDto,
                competitorMap,
                null);

        assertTrue(result.isEmpty());
    }

    // Test Group: initMatchStageCompetitorEntities
    @Test
    public void testInitMatchStageCompetitorEntities_whenNullList_thenEmptyMap() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                null,
                Map.of(stageDto.getUuid(), stageDto),
                new HashMap<>(),
                null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenStageMapHasNullValue_thenIgnored() {
        Map<UUID, MatchStageDto> stageMap = new HashMap<>();
        stageMap.put(UUID.randomUUID(), null);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                new ArrayList<>(),
                stageMap,
                new HashMap<>(),
                null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenMissingCompetitorInMap_thenReturnsEmptyMap() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto dto = buildMatchStageCompetitorDto(stageDto, competitorDto);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(dto),
                Map.of(stageDto.getUuid(), stageDto),
                new HashMap<>(),
                null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenIdFound_thenLooksUpAndAddsEntry() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto dto = buildMatchStageCompetitorDto(stageDto, competitorDto);
        dto.setId(41L);

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(41L);
        when(matchStageCompetitorRepository.findById(41L)).thenReturn(Optional.of(entity));

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(dto),
                Map.of(stageDto.getUuid(), stageDto),
                Map.of(competitorDto.getUuid(), competitorDto),
                null);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(dto.getUuid()));
        verify(matchStageCompetitorRepository).findById(41L);
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenFilterClubSet_thenOnlyMatchingClubIncluded() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto included = buildMatchStageCompetitorDto(stageDto, competitorDto);
        included.setClub(ClubIdentifier.HPSC);

        MatchStageCompetitorDto excluded = buildMatchStageCompetitorDto(stageDto, competitorDto);
        excluded.setClub(ClubIdentifier.SOSC);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(included, excluded),
                Map.of(stageDto.getUuid(), stageDto),
                Map.of(competitorDto.getUuid(), competitorDto),
                ClubIdentifier.HPSC);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(included.getUuid()));
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenFilterClubExcludedIdentifier_thenNoFilteringApplied() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto first = buildMatchStageCompetitorDto(stageDto, competitorDto);
        first.setClub(ClubIdentifier.HPSC);

        MatchStageCompetitorDto second = buildMatchStageCompetitorDto(stageDto, competitorDto);
        second.setClub(ClubIdentifier.SOSC);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(first, second),
                Map.of(stageDto.getUuid(), stageDto),
                Map.of(competitorDto.getUuid(), competitorDto),
                ClubIdentifier.UNKNOWN);

        assertEquals(2, result.size());
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenTwoStages_thenAggregatesBoth() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stage1 = buildMatchStageDto(matchDto, 1);
        MatchStageDto stage2 = buildMatchStageDto(matchDto, 2);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto dto1 = buildMatchStageCompetitorDto(stage1, competitorDto);
        MatchStageCompetitorDto dto2 = buildMatchStageCompetitorDto(stage2, competitorDto);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(dto1, dto2),
                Map.of(stage1.getUuid(), stage1, stage2.getUuid(), stage2),
                Map.of(competitorDto.getUuid(), competitorDto),
                null);

        assertEquals(2, result.size());
    }

    @Test
    public void testInitMatchStageCompetitorEntities_whenMismatchedStageUuid_thenFilteredOut() {
        MatchDto matchDto = buildMatchDto();
        MatchStageDto stageInMap = buildMatchStageDto(matchDto, 1);
        MatchStageDto otherStage = buildMatchStageDto(matchDto, 2);
        CompetitorDto competitorDto = buildCompetitorDto("A", "B");

        MatchStageCompetitorDto dto = buildMatchStageCompetitorDto(otherStage, competitorDto);

        Map<UUID, MatchStageCompetitorDto> result = domainService.initMatchStageCompetitorEntities(
                List.of(dto),
                Map.of(stageInMap.getUuid(), stageInMap),
                Map.of(competitorDto.getUuid(), competitorDto),
                null);

        assertTrue(result.isEmpty());
    }
}

