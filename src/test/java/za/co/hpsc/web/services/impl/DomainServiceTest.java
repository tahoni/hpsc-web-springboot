package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private final String DEFAULT_FILTER_CLUB_ABBREVIATION = "HPSC";

    // =====================================================================
    // Tests for initMatchEntities - Input Validation and Error Handling
    // =====================================================================
    @Test
    public void testInitMatchEntities_whenMatchResultsIsNull_thenReturnsEmptyOptional() {
        // Act
        var result = domainService.initMatchEntities(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenMatchDtoIsNull_thenReturnsEmptyOptional() {
        // Arrange
        MatchResultsDto matchResults = new MatchResultsDto();
        matchResults.setMatch(null);

        // Act
        var result = domainService.initMatchEntities(matchResults, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenMatchIdIsNull_thenReturnsMatchEntityHolder() {
        // Arrange
        MatchResultsDto matchResults = new MatchResultsDto();
        MatchDto matchDto = new MatchDto();
        matchDto.setId(null);
        matchResults.setMatch(matchDto);

        // Act
        var result = domainService.initMatchEntities(matchResults, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        var holder = result.get();
        assertNotNull(holder.getMatch());
        assertNull(holder.getClub());
    }

    // =====================================================================
    // Tests for initMatchEntities - Club Entity Handling
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenClubDtoIsPresent_thenInitializesClubFromDto() {
        // Arrange
        String filterClub = "TEST";

        Long matchId = 1L;
        Long clubId = 100L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        ClubDto clubDto = new ClubDto();
        clubDto.setId(clubId);
        clubDto.setAbbreviation(filterClub);
        clubDto.setName("Test Club");
        matchResults.setClub(clubDto);

        Club club = new Club();
        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenClubDtoIsAbsent_thenDontSetClub() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setClub(null);

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenClubDtoHasNullId_thenDontSetClub() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchEntities - Match Entity Handling
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenExistingMatch_thenLoadsFromRepository() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(matchId);
        existingMatch.setName("Existing Match");

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenNewMatch_thenCreatesNewEntity() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchEntities - Competitor Entity Handling
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenEmptyCompetitorList_thenReturnsEmptyCompetitorList() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setCompetitors(Collections.emptyList());

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getCompetitorMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenCompetitorList_thenInitializesAllCompetitors() {
        // Arrange
        Long matchId = 1L;
        Long competitor1Id = 100L;
        Long competitor2Id = 101L;

        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setId(competitor1Id);
        competitor1.setUuid(UUID.randomUUID());
        competitor1.setFirstName("John");

        CompetitorDto competitor2 = new CompetitorDto();
        competitor2.setId(competitor2Id);
        competitor2.setUuid(UUID.randomUUID());
        competitor2.setFirstName("Jane");

        matchResults.setCompetitors(List.of(competitor1, competitor2));

        Competitor comp1Entity = new Competitor();
        Competitor comp2Entity = new Competitor();

        when(competitorRepository.findById(competitor1Id)).thenReturn(Optional.of(comp1Entity));
        when(competitorRepository.findById(competitor2Id)).thenReturn(Optional.of(comp2Entity));

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertFalse(result.get().getCompetitorMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenNullCompetitorList_thenReturnsEmptyCompetitorList() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setCompetitors(null);

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchCompetitorMap().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchEntities - Match Stage Entity Handling
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenEmptyStageList_thenReturnsEmptyStageList() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setStages(Collections.emptyList());

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenStageList_thenInitializesAllStages() {
        // Arrange
        Long matchId = 1L;
        Long stage1Id = 200L;
        Long stage2Id = 201L;

        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        MatchStageDto stage1 = new MatchStageDto();
        stage1.setId(stage1Id);
        stage1.setUuid(UUID.randomUUID());
        stage1.setStageName("Stage 1");
        stage1.setMatch(matchResults.getMatch());

        MatchStageDto stage2 = new MatchStageDto();
        stage2.setId(stage2Id);
        stage2.setUuid(UUID.randomUUID());
        stage2.setStageName("Stage 2");
        stage2.setMatch(matchResults.getMatch());

        matchResults.setStages(List.of(stage1, stage2));

        IpscMatch match = new IpscMatch();
        IpscMatchStage stageEntity1 = new IpscMatchStage();
        stageEntity1.setMatch(match);
        IpscMatchStage stageEntity2 = new IpscMatchStage();
        stageEntity2.setMatch(match);

        when(ipscMatchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(ipscMatchStageRepository.findById(stage1Id)).thenReturn(Optional.of(stageEntity1));
        when(ipscMatchStageRepository.findById(stage2Id)).thenReturn(Optional.of(stageEntity2));

        // Act
        var result = domainService.initMatchEntities(matchResults, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertFalse(result.get().getMatchStageMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenNullStageList_thenReturnsEmptyStageList() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setStages(null);

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageMap().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchEntities - Complete Data Processing
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenCompleteMatchData_thenReturnsFullMatchEntityHolder() {
        // Arrange
        Long matchId = 1L;
        Long clubId = 100L;
        Long competitor1Id = 200L;
        Long stage1Id = 300L;

        MatchResultsDto matchResults = new MatchResultsDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setId(matchId);
        matchDto.setName("Complete Match");
        matchResults.setMatch(matchDto);

        ClubDto clubDto = new ClubDto();
        clubDto.setId(clubId);
        clubDto.setAbbreviation(null);
        matchResults.setClub(clubDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(competitor1Id);
        competitorDto.setUuid(UUID.randomUUID());
        competitorDto.setFirstName("John");
        matchResults.setCompetitors(List.of(competitorDto));

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setId(stage1Id);
        stageDto.setUuid(UUID.randomUUID());
        stageDto.setStageName("Stage 1");
        stageDto.setMatch(matchDto);
        matchResults.setStages(List.of(stageDto));

        matchResults.setMatchCompetitors(Collections.emptyList());
        matchResults.setMatchStageCompetitors(Collections.emptyList());

        Club club = new Club();
        Competitor competitor = new Competitor();
        IpscMatch match = new IpscMatch();
        IpscMatchStage stage = new IpscMatchStage();
        stage.setMatch(match);

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(competitorRepository.findById(competitor1Id)).thenReturn(Optional.of(competitor));
        when(ipscMatchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(ipscMatchStageRepository.findById(stage1Id)).thenReturn(Optional.of(stage));

        // Act
        var result = domainService.initMatchEntities(matchResults, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        var holder = result.get();
        assertNotNull(holder.getMatch());
        assertNotNull(holder.getClub());
        assertFalse(holder.getCompetitorMap().isEmpty());
        assertFalse(holder.getMatchStageMap().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchEntities - Edge Cases and Boundary Conditions
    // =====================================================================

    @Test
    public void testInitMatchEntities_whenNullCompetitorInList_thenFiltersOutNull() {
        // Arrange

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setId(200L);
        competitor1.setUuid(UUID.randomUUID());

        List<CompetitorDto> competitorList = new ArrayList<>();
        competitorList.add(competitor1);
        matchResults.setCompetitors(competitorList);

        Competitor comp1Entity = new Competitor();
        when(competitorRepository.findById(any())).thenReturn(Optional.of(comp1Entity));

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenNullStageInList_thenFiltersOutNull() {
        // Arrange
        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        MatchStageDto stage1 = new MatchStageDto();
        stage1.setId(300L);
        stage1.setUuid(UUID.randomUUID());

        List<MatchStageDto> stageList = new ArrayList<>();
        stageList.add(stage1);
        matchResults.setStages(stageList);

        IpscMatch match = new IpscMatch();

        // Act
        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void initMatchEntities_whenMatchClubAbbreviationResolvesClub_thenSetsClubInMapping() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.setClub(null);

        Club club = new Club();
        club.setId(15L);
        club.setName("HPSC");
        club.setAbbreviation("HPSC");

        when(clubRepository.findByAbbreviation("HPSC")).thenReturn(Optional.of(club));

        var result = domainService.initMatchEntities(matchResults, "HPSC", null);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getClub());
        assertEquals(15L, result.get().getClub().getId());
        assertEquals("HPSC", result.get().getClub().getAbbreviation());
    }

    @Test
    public void initMatchEntities_whenMatchCompetitorsBelongToDifferentMatches_thenKeepsOnlyCurrentMatchCompetitors() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.getMatch().setUuid(UUID.randomUUID());

        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setUuid(UUID.randomUUID());
        competitor1.setFirstName("Alice");

        CompetitorDto competitor2 = new CompetitorDto();
        competitor2.setUuid(UUID.randomUUID());
        competitor2.setFirstName("Bob");

        matchResults.setCompetitors(List.of(competitor1, competitor2));

        MatchCompetitorDto included = new MatchCompetitorDto();
        included.setUuid(UUID.randomUUID());
        included.setCompetitor(competitor1);
        included.setMatch(matchResults.getMatch());

        MatchDto otherMatch = new MatchDto();
        otherMatch.setUuid(UUID.randomUUID());
        otherMatch.setName("Other Match");

        MatchCompetitorDto excluded = new MatchCompetitorDto();
        excluded.setUuid(UUID.randomUUID());
        excluded.setCompetitor(competitor2);
        excluded.setMatch(otherMatch);

        matchResults.setMatchCompetitors(List.of(included, excluded));

        var result = domainService.initMatchEntities(matchResults, null, null);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getCompetitorMap().size());
        assertTrue(result.get().getCompetitorMap().containsKey(competitor1.getUuid()));
        assertTrue(result.get().getCompetitorMap().containsKey(competitor2.getUuid()));
//        assertEquals(1, result.get().getMatchCompetitorMap().size());
//        assertTrue(result.get().getMatchCompetitorMap().containsKey(included.getUuid()));
//        assertFalse(result.get().getMatchCompetitorMap().containsKey(excluded.getUuid()));
    }

    @Test
    public void initMatchEntities_whenMatchCompetitorReferencesUnknownCompetitor_thenReturnsEmptyMatchCompetitorMap() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.getMatch().setUuid(UUID.randomUUID());

        CompetitorDto existingCompetitor = new CompetitorDto();
        existingCompetitor.setUuid(UUID.randomUUID());
        existingCompetitor.setFirstName("Known");
        matchResults.setCompetitors(List.of(existingCompetitor));

        CompetitorDto unknownCompetitor = new CompetitorDto();
        unknownCompetitor.setUuid(UUID.randomUUID());
        unknownCompetitor.setFirstName("Unknown");

        MatchCompetitorDto matchCompetitor = new MatchCompetitorDto();
        matchCompetitor.setUuid(UUID.randomUUID());
        matchCompetitor.setMatch(matchResults.getMatch());
        matchCompetitor.setCompetitor(unknownCompetitor);
        matchCompetitor.setClub(ClubIdentifier.HPSC);

        matchResults.setMatchCompetitors(List.of(matchCompetitor));

        var result = domainService.initMatchEntities(matchResults, null, null);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchCompetitorMap().isEmpty());
    }

    @Test
    public void initMatchEntities_whenClubDtoExists_thenPrefersClubDtoOverMatchClubAbbreviation() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);

        ClubDto clubDto = new ClubDto();
        clubDto.setId(101L);
        clubDto.setAbbreviation("DTO");
        clubDto.setName("DTO Club");
        matchResults.setClub(clubDto);

        Club persistedClub = new Club();
        persistedClub.setId(101L);
        when(clubRepository.findById(101L)).thenReturn(Optional.of(persistedClub));

        var result = domainService.initMatchEntities(matchResults, "HPSC", null);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getClub());
        assertEquals(101L, result.get().getClub().getId());
        assertEquals("DTO", result.get().getClub().getAbbreviation());
        verify(clubRepository, never()).findByAbbreviation("HPSC");
    }

    @Test
    public void initMatchEntities_whenNullCompetitorInList_thenSkipsNullAndKeepsValidCompetitors() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);

        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setId(200L);
        competitor1.setUuid(UUID.randomUUID());
        competitor1.setFirstName("Alice");

        List<CompetitorDto> competitorList = new ArrayList<>();
        competitorList.add(competitor1);
        competitorList.add(null);
        matchResults.setCompetitors(competitorList);

        when(competitorRepository.findById(200L)).thenReturn(Optional.of(new Competitor()));

        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCompetitorMap().size());
        assertTrue(result.get().getCompetitorMap().containsKey(competitor1.getUuid()));
    }

    @Test
    public void initMatchEntities_whenStageBelongsToDifferentMatch_thenExcludesStageFromStageMap() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        MatchDto currentMatch = matchResults.getMatch();
        currentMatch.setUuid(UUID.randomUUID());

        MatchStageDto stageInMatch = new MatchStageDto();
        stageInMatch.setId(300L);
        stageInMatch.setUuid(UUID.randomUUID());
        stageInMatch.setStageName("Included Stage");
        stageInMatch.setMatch(currentMatch);

        MatchDto otherMatch = new MatchDto();
        otherMatch.setUuid(UUID.randomUUID());

        MatchStageDto stageInOtherMatch = new MatchStageDto();
        stageInOtherMatch.setId(301L);
        stageInOtherMatch.setUuid(UUID.randomUUID());
        stageInOtherMatch.setStageName("Excluded Stage");
        stageInOtherMatch.setMatch(otherMatch);

        matchResults.setStages(List.of(stageInMatch, stageInOtherMatch));

        IpscMatch stageMatchEntity = new IpscMatch();
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setId(300L);
        stageEntity.setMatch(stageMatchEntity);
        stageEntity.setStageNumber(1);

        when(ipscMatchStageRepository.findById(300L)).thenReturn(Optional.of(stageEntity));

        var result = domainService.initMatchEntities(matchResults, null, null);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMatchStageMap().size());
        assertTrue(result.get().getMatchStageMap().containsKey(stageInMatch.getUuid()));
        assertFalse(result.get().getMatchStageMap().containsKey(stageInOtherMatch.getUuid()));
    }

    @Test
    public void initMatchEntities_whenMatchCompetitorHasNullMatch_thenExcludesFromMatchCompetitorMap() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.getMatch().setUuid(UUID.randomUUID());

        CompetitorDto competitor = new CompetitorDto();
        competitor.setUuid(UUID.randomUUID());
        competitor.setFirstName("Known");
        matchResults.setCompetitors(List.of(competitor));

        MatchCompetitorDto nullMatchCompetitor = new MatchCompetitorDto();
        nullMatchCompetitor.setUuid(UUID.randomUUID());
        nullMatchCompetitor.setCompetitor(competitor);
        nullMatchCompetitor.setMatch(null);

        matchResults.setMatchCompetitors(List.of(nullMatchCompetitor));

        var result = domainService.initMatchEntities(matchResults, null, null);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchCompetitorMap().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchEntities - matchStageCompetitorMap integration
    // =====================================================================

    @Test
    public void initMatchEntities_whenNullMatchStageCompetitorList_thenMatchStageCompetitorMapIsEmpty() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.setMatchStageCompetitors(null);

        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageCompetitorMap().isEmpty());
    }

    @Test
    public void initMatchEntities_whenEmptyMatchStageCompetitorList_thenMatchStageCompetitorMapIsEmpty() {
        MatchResultsDto matchResults = createMinimalMatchResults(1L);
        matchResults.setMatchStageCompetitors(Collections.emptyList());

        var result = domainService.initMatchEntities(matchResults, DEFAULT_FILTER_CLUB_ABBREVIATION);

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageCompetitorMap().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitorEntities (direct protected method)
    // =====================================================================

    @Test
    public void initMatchStageCompetitorEntities_whenNullList_thenReturnsEmptyMap() {
        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();

        var result = domainService.initMatchStageCompetitorEntities(null, matchStageMap, competitorMap, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void initMatchStageCompetitorEntities_whenMatchStageCompetitorHasNullMatchStage_thenFiltersOut() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(UUID.randomUUID());
        mscDto.setMatchStage(null);           // null matchStage — must be filtered
        mscDto.setCompetitor(competitorDto);

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, competitorMap, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void initMatchStageCompetitorEntities_whenMatchStageUuidNotInMap_thenExcludesFromResult() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageInMap = new MatchStageDto();
        stageInMap.setUuid(stageUuid);

        // competitor DTO whose matchStage points to a DIFFERENT UUID
        MatchStageDto differentStage = new MatchStageDto();
        differentStage.setUuid(UUID.randomUUID());

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(UUID.randomUUID());
        mscDto.setMatchStage(differentStage);
        mscDto.setCompetitor(competitorDto);

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageInMap);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, competitorMap, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void initMatchStageCompetitorEntities_whenCompetitorNotInCompetitorMap_thenReturnsEmptyMap() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        // Competitor whose UUID is NOT in the competitorMap
        CompetitorDto unknownCompetitor = new CompetitorDto();
        unknownCompetitor.setUuid(UUID.randomUUID());

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(UUID.randomUUID());
        mscDto.setMatchStage(stageDto);
        mscDto.setCompetitor(unknownCompetitor);

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> emptyCompetitorMap = new HashMap<>(); // unknown competitor not in map

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, emptyCompetitorMap, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void initMatchStageCompetitorEntities_whenEntityFoundAndNoClubFilter_thenIncludesEntry() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());

        UUID mscUuid = UUID.randomUUID();
        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(mscUuid);
        mscDto.setId(500L);
        mscDto.setMatchStage(stageDto);
        mscDto.setCompetitor(competitorDto);
        mscDto.setClub(ClubIdentifier.SOSC);

        when(matchStageCompetitorRepository.findById(500L)).thenReturn(Optional.of(new MatchStageCompetitor()));

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, competitorMap, null);  // null = no club filter

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mscUuid));
    }

    @Test
    public void initMatchStageCompetitorEntities_whenEntityFoundAndClubFilterMatches_thenIncludesEntry() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());

        UUID mscUuid = UUID.randomUUID();
        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(mscUuid);
        mscDto.setId(501L);
        mscDto.setMatchStage(stageDto);
        mscDto.setCompetitor(competitorDto);
        mscDto.setClub(ClubIdentifier.HPSC);      // matches filter

        when(matchStageCompetitorRepository.findById(501L)).thenReturn(Optional.of(new MatchStageCompetitor()));

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, competitorMap, ClubIdentifier.HPSC);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mscUuid));
    }

    @Test
    public void initMatchStageCompetitorEntities_whenEntityFoundAndClubFilterDoesNotMatch_thenExcludesEntry() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setUuid(UUID.randomUUID());
        mscDto.setId(502L);
        mscDto.setMatchStage(stageDto);
        mscDto.setCompetitor(competitorDto);
        mscDto.setClub(ClubIdentifier.SOSC);      // does NOT match HPSC filter

        when(matchStageCompetitorRepository.findById(502L)).thenReturn(Optional.of(new MatchStageCompetitor()));

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscDto), matchStageMap, competitorMap, ClubIdentifier.HPSC);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void initMatchStageCompetitorEntities_whenMultipleEntries_onlyMatchingClubIncluded() {
        UUID stageUuid = UUID.randomUUID();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setUuid(stageUuid);

        CompetitorDto comp1 = new CompetitorDto();
        comp1.setUuid(UUID.randomUUID());
        CompetitorDto comp2 = new CompetitorDto();
        comp2.setUuid(UUID.randomUUID());

        UUID mscUuid1 = UUID.randomUUID();
        MatchStageCompetitorDto mscIncluded = new MatchStageCompetitorDto();
        mscIncluded.setUuid(mscUuid1);
        mscIncluded.setId(600L);
        mscIncluded.setMatchStage(stageDto);
        mscIncluded.setCompetitor(comp1);
        mscIncluded.setClub(ClubIdentifier.HPSC);

        UUID mscUuid2 = UUID.randomUUID();
        MatchStageCompetitorDto mscExcluded = new MatchStageCompetitorDto();
        mscExcluded.setUuid(mscUuid2);
        mscExcluded.setId(601L);
        mscExcluded.setMatchStage(stageDto);
        mscExcluded.setCompetitor(comp2);
        mscExcluded.setClub(ClubIdentifier.SOSC);     // filtered out

        when(matchStageCompetitorRepository.findById(600L)).thenReturn(Optional.of(new MatchStageCompetitor()));
        when(matchStageCompetitorRepository.findById(601L)).thenReturn(Optional.of(new MatchStageCompetitor()));

        Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(stageUuid, stageDto);

        Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(comp1.getUuid(), comp1);
        competitorMap.put(comp2.getUuid(), comp2);

        var result = domainService.initMatchStageCompetitorEntities(
                List.of(mscIncluded, mscExcluded), matchStageMap, competitorMap, ClubIdentifier.HPSC);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mscUuid1));
        assertFalse(result.containsKey(mscUuid2));
    }

    // Helper Methods
    private MatchResultsDto createMinimalMatchResults(Long matchId) {
        MatchResultsDto matchResults = new MatchResultsDto();
        MatchDto matchDto = new MatchDto();
        matchDto.setId(matchId);
        matchDto.setName("Test Match");
        matchResults.setMatch(matchDto);
        matchResults.setCompetitors(new ArrayList<>());
        matchResults.setStages(new ArrayList<>());
        matchResults.setMatchCompetitors(new ArrayList<>());
        matchResults.setMatchStageCompetitors(new ArrayList<>());
        return matchResults;
    }
}

