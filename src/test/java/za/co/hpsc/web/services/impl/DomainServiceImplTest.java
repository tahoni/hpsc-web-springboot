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
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DomainServiceImplTest {
    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @InjectMocks
    private DomainServiceImpl domainService;

    private final String DEFAULT_FILTER_CLUB_ABBREVIATION = "HPSC";

    // =====================================================================
    // Tests for initMatchEntities - Match entity initialization
    // =====================================================================

    // Test Group: Null/Empty Input Handling
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
        String filterClub = null;

        MatchResultsDto matchResults = new MatchResultsDto();
        matchResults.setMatch(null);

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenMatchIdIsNull_thenReturnsMatchEntityHolder() {
        // Arrange
        String filterClub = null;

        MatchResultsDto matchResults = new MatchResultsDto();
        MatchDto matchDto = new MatchDto();
        matchDto.setId(null);
        matchResults.setMatch(matchDto);

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        var holder = result.get();
        assertNotNull(holder.getMatch());
        assertNull(holder.getClub());
    }

    // Test Group: Minimal Valid Data
    @Test
    public void testInitMatchEntities_whenMinimalMatchData_thenReturnsMatchEntityHolder() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        Club clubEntity = new Club();
        clubEntity.setId(100L);

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(clubEntity));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        var holder = result.get();
        assertNotNull(holder.getMatch());
        assertNotNull(holder.getClub());
    }

    // Test Group: Club Entity Handling
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
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenClubDtoIsAbsent_thenUsesHpscClub() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setClub(null);

        Club hpscClub = new Club();
        hpscClub.setAbbreviation(filterClub);
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(hpscClub));
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenClubDtoHasNullId_thenUsesHpscAsDefault() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        Club hpscClub = new Club();
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(hpscClub));
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // Test Group: Match Entity Handling
    @Test
    public void testInitMatchEntities_whenExistingMatch_thenLoadsFromRepository() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(matchId);
        existingMatch.setName("Existing Match");

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.of(existingMatch));
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenNewMatch_thenCreatesNewEntity() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // Test Group: Competitor Entity Handling
    @Test
    public void testInitMatchEntities_whenEmptyCompetitorList_thenReturnsEmptyCompetitorList() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setCompetitors(Collections.emptyList());

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getCompetitorMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenCompetitorList_thenInitializesAllCompetitors() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

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
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertFalse(result.get().getCompetitorMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenNullCompetitorList_thenReturnsEmptyCompetitorList() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setCompetitors(null);

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchCompetitorMap().isEmpty());
    }

    // Test Group: Match Stage Entity Handling
    @Test
    public void testInitMatchEntities_whenEmptyStageList_thenReturnsEmptyStageList() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setStages(Collections.emptyList());

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenStageList_thenInitializesAllStages() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        Long stage1Id = 200L;
        Long stage2Id = 201L;

        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        MatchStageDto stage1 = new MatchStageDto();
        stage1.setId(stage1Id);
        stage1.setUuid(UUID.randomUUID());
        stage1.setStageName("Stage 1");

        MatchStageDto stage2 = new MatchStageDto();
        stage2.setId(stage2Id);
        stage2.setUuid(UUID.randomUUID());
        stage2.setStageName("Stage 2");

        matchResults.setStages(List.of(stage1, stage2));

        IpscMatch match = new IpscMatch();
        IpscMatchStage stageEntity1 = new IpscMatchStage();
        IpscMatchStage stageEntity2 = new IpscMatchStage();

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.of(match));
        when(ipscMatchStageRepository.findById(stage1Id)).thenReturn(Optional.of(stageEntity1));
        when(ipscMatchStageRepository.findById(stage2Id)).thenReturn(Optional.of(stageEntity2));
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertFalse(result.get().getMatchStageMap().isEmpty());
    }

    @Test
    public void testInitMatchEntities_whenNullStageList_thenReturnsEmptyStageList() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);
        matchResults.setStages(null);

        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchStageMap().isEmpty());
    }

    // Test Group: Full Data Scenario
    @Test
    public void testInitMatchEntities_whenCompleteMatchData_thenReturnsFullMatchEntityHolder() {
        // Arrange
        String filterClub = "TEST";

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
        clubDto.setAbbreviation(filterClub);
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
        matchResults.setStages(List.of(stageDto));

        matchResults.setMatchCompetitors(Collections.emptyList());
        matchResults.setMatchStageCompetitors(Collections.emptyList());

        Club club = new Club();
        Competitor competitor = new Competitor();
        IpscMatch match = new IpscMatch();
        IpscMatchStage stage = new IpscMatchStage();

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(competitorRepository.findById(competitor1Id)).thenReturn(Optional.of(competitor));
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.of(match));
        when(ipscMatchStageRepository.findById(stage1Id)).thenReturn(Optional.of(stage));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        var holder = result.get();
        assertNotNull(holder.getMatch());
        assertNotNull(holder.getClub());
        assertFalse(holder.getCompetitorMap().isEmpty());
        assertFalse(holder.getMatchStageMap().isEmpty());
    }

    // Test Group: Edge Cases
    @Test
    public void testInitMatchEntities_whenNullCompetitorInList_thenFiltersOutNull() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

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
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntities_whenNullStageInList_thenFiltersOutNull() {
        // Arrange
        String filterClub = DEFAULT_FILTER_CLUB_ABBREVIATION;

        Long matchId = 1L;
        MatchResultsDto matchResults = createMinimalMatchResults(matchId);

        MatchStageDto stage1 = new MatchStageDto();
        stage1.setId(300L);
        stage1.setUuid(UUID.randomUUID());

        List<MatchStageDto> stageList = new ArrayList<>();
        stageList.add(stage1);
        matchResults.setStages(stageList);

        IpscMatch match = new IpscMatch();
        IpscMatchStage stageEntity = new IpscMatchStage();
        when(ipscMatchStageRepository.findById(any())).thenReturn(Optional.of(stageEntity));
        when(ipscMatchRepository.findByIdWithClubStages(matchId)).thenReturn(Optional.of(match));
        when(clubRepository.findByAbbreviation(filterClub)).thenReturn(Optional.of(new Club()));

        // Act
        var result = domainService.initMatchEntities(matchResults, filterClub);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
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

