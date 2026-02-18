package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.request.MemberRequest;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO: redo some tests
// TODO: add tests for scores not zero/null
// TODO: add tests for scores zero/null
// TODO: add tests for club identifier
@ExtendWith(MockitoExtension.class)
public class IpscMatchResultServiceImplTest {
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private CompetitorRepository competitorRepository;
    @Mock
    private IpscMatchRepository ipscMatchRepository;
    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private ClubEntityService clubEntityService;
    @Mock
    private MatchEntityService matchEntityService;
    @Mock
    private CompetitorEntityService competitorEntityService;
    @Mock
    private MatchStageEntityService matchStageEntityService;
    @Mock
    private MatchCompetitorEntityService matchCompetitorEntityService;
    @Mock
    private MatchStageCompetitorEntityService matchStageCompetitorEntityService;


    @InjectMocks
    private IpscMatchResultServiceImpl ipscMatchResultService;

    private MatchDto matchDto;
    private ClubDto clubDto;
    private Club clubEntity;
    private IpscMatch matchEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        clubEntity = new Club();
        clubEntity.setId(1L);
        clubEntity.setName("Test Club");
        clubEntity.setAbbreviation("TC");

        matchDto = new MatchDto();
        matchDto.setId(100L);
        matchDto.setName("Test Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchDto.setClub(clubDto);

        matchEntity = new IpscMatch();
        matchEntity.setId(100L);
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchEntity.setClub(clubEntity);

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setClub(clubDto);
    }

    @Test
    public void testInitClub_withNullResponse_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubDto> result = ipscMatchResultService.initClub(null, null);

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(clubEntityService);
    }

    @Test
    public void testInitClub_withExistingClub_thenReturnsPopulatedClubDto() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Test Club");
        clubResponse.setClubCode("TC");

        Club existingClub = new Club();
        existingClub.setId(101L);
        existingClub.setName("Test Club");
        existingClub.setAbbreviation("TC");

        when(clubEntityService.findClubByNameOrAbbreviation("Test Club", "TC")).thenReturn(Optional.of(existingClub));

        // Act
        Optional<ClubDto> result = ipscMatchResultService.initClub(clubResponse, null);

        // Assert
        assertTrue(result.isPresent());
        ClubDto clubDto = result.get();
        assertEquals(101L, clubDto.getId());
        assertEquals("Test Club", clubDto.getName());
        assertEquals("TC", clubDto.getAbbreviation());
        verify(clubEntityService, times(1)).findClubByNameOrAbbreviation("Test Club", "TC");
    }

    @Test
    public void testInitClub_withNonExistingClub_thenReturnsClubDtoFromResponse() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Non-existent Club");
        clubResponse.setClubCode("NC");

        when(clubEntityService.findClubByNameOrAbbreviation("Non-existent Club", "NC")).thenReturn(Optional.empty());

        // Act
        Optional<ClubDto> result = ipscMatchResultService.initClub(clubResponse, null);

        // Assert
        assertTrue(result.isPresent());
        ClubDto clubDto = result.get();
        assertNull(clubDto.getId());
        assertEquals("Non-existent Club", clubDto.getName());
        assertEquals("NC", clubDto.getAbbreviation());
        verify(clubEntityService, times(1)).findClubByNameOrAbbreviation("Non-existent Club", "NC");
    }

    @Test
    public void testInitStages_withNullStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(matchStageEntityService);
    }

    @Test
    public void testInitStages_withEmptyStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, List.of());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(matchStageEntityService);
    }

    @Test
    public void testInitStages_withExistingMatchStages_thenReturnsPopulatedMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("Stage 1");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("Stage 2");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        IpscMatch match = new IpscMatch();
        match.setId(1L);

        IpscMatchStage matchStage1 = new IpscMatchStage();
        matchStage1.setId(10L);
        matchStage1.setMatch(match);
        matchStage1.setStageNumber(1);

        IpscMatchStage matchStage2 = new IpscMatchStage();
        matchStage2.setId(20L);
        matchStage2.setMatch(match);
        matchStage2.setStageNumber(2);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage1));
        when(matchStageEntityService.findMatchStage(1L, 2)).thenReturn(Optional.of(matchStage2));

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.getFirst().getId());
        assertEquals(20L, result.get(1).getId());
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 1);
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 2);
    }

    @Test
    public void testInitStages_withNonExistingMatchStages_thenReturnsNewMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("New Stage 1");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("New Stage 2");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(1L, 2)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNull(result.getFirst().getId());
        assertNull(result.get(1).getId());
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 1);
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 2);
    }

    @Test
    public void testInitStages_withMixedExistingAndNonExistingStages_thenReturnsMixedList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("Existing Stage");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("New Stage");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        IpscMatch match = new IpscMatch();
        match.setId(1L);

        IpscMatchStage matchStage1 = new IpscMatchStage();
        matchStage1.setId(10L);
        matchStage1.setMatch(match);
        matchStage1.setStageNumber(1);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage1));
        when(matchStageEntityService.findMatchStage(1L, 2)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.getFirst().getId());
        assertNull(result.get(1).getId());
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 1);
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 2);
    }

    @Test
    public void testInitStages_withSingleStage_thenReturnsSingleStageDto() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName("Single Stage");

        List<StageResponse> stageResponses = List.of(stageResponse);

        IpscMatch match = new IpscMatch();
        match.setId(1L);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(100L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage));

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.getFirst().getId());
        assertEquals(1, result.getFirst().getIndex());
        verify(matchStageEntityService, times(1)).findMatchStage(1L, 1);
    }

    @Test
    public void testInitStages_withStageFieldsPreservation_thenPreservesAllFields() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(5);
        stageResponse.setStageName("Complex Stage");
        stageResponse.setTargetPaper(3);
        stageResponse.setTargetPopper(4);
        stageResponse.setTargetPlates(6);
        stageResponse.setTargetDisappear(2);
        stageResponse.setTargetPenalty(1);
        stageResponse.setMinRounds(10);
        stageResponse.setMaxPoints(150);

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, 5)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageDto resultDto = result.getFirst();

        // Verify all fields are preserved
        assertEquals(5, resultDto.getIndex());
        assertEquals(5, resultDto.getStageNumber());
        assertEquals("Complex Stage", resultDto.getStageName());
        assertEquals(3, resultDto.getTargetPaper());
        assertEquals(4, resultDto.getTargetPopper());
        assertEquals(6, resultDto.getTargetPlates());
        assertEquals(2, resultDto.getTargetDisappear());
        assertEquals(1, resultDto.getTargetPenalty());
        assertEquals(10, resultDto.getMinRounds());
        assertEquals(150, resultDto.getMaxPoints());
        assertEquals(0, resultDto.getRangeNumber());
        assertEquals(matchDto, resultDto.getMatch());
    }

    @Test
    public void testInitStages_withNullStageFields_thenPreservesNullFields() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName("Minimal Stage");
        // All other fields left as null

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageDto resultDto = result.getFirst();

        assertNull(resultDto.getTargetPaper());
        assertNull(resultDto.getTargetPopper());
        assertNull(resultDto.getTargetPlates());
        assertNull(resultDto.getTargetDisappear());
        assertNull(resultDto.getTargetPenalty());
        assertNull(resultDto.getMinRounds());
        assertNull(resultDto.getMaxPoints());
    }

    @Test
    public void testInitStages_withLargeNumberOfStages_thenReturnsAllStages() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        List<StageResponse> stageResponses = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            StageResponse stageResponse = new StageResponse();
            stageResponse.setStageId(i);
            stageResponse.setStageName("Stage " + i);
            stageResponses.add(stageResponse);
        }

        for (int i = 1; i <= 20; i++) {
            when(matchStageEntityService.findMatchStage(1L, i)).thenReturn(Optional.empty());
        }

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(20, result.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i + 1, result.get(i).getStageNumber());
            assertEquals("Stage " + (i + 1), result.get(i).getStageName());
        }
        verify(matchStageEntityService, times(20)).findMatchStage(eq(1L), anyInt());
    }

    @Test
    public void testInitStages_withZeroAndNegativeTargetValues_thenPreservesValues() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName("Zero Values Stage");
        stageResponse.setTargetPaper(0);
        stageResponse.setTargetPopper(0);
        stageResponse.setMinRounds(0);
        stageResponse.setMaxPoints(0);

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageDto resultDto = result.getFirst();

        assertEquals(0, resultDto.getTargetPaper());
        assertEquals(0, resultDto.getTargetPopper());
        assertEquals(0, resultDto.getMinRounds());
        assertEquals(0, resultDto.getMaxPoints());
    }

    @Test
    public void testInitStages_withExistingStagePreservesEntityData_thenVerifiesDataIntegrity() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName("Updated Stage Name");
        stageResponse.setTargetPaper(5);
        stageResponse.setTargetPopper(6);

        List<StageResponse> stageResponses = List.of(stageResponse);

        IpscMatch match = new IpscMatch();
        match.setId(1L);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(50L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);
        matchStage.setStageName("Original Stage Name");
        matchStage.setTargetPaper(3);
        matchStage.setTargetPopper(4);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage));

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageDto resultDto = result.getFirst();

        // Verify that init() is called which updates the fields from stageResponse
        assertEquals(50L, resultDto.getId());
        assertEquals("Updated Stage Name", resultDto.getStageName());
        assertEquals(5, resultDto.getTargetPaper());
        assertEquals(6, resultDto.getTargetPopper());
    }

    @Test
    public void testInitStages_withMultipleStagesVerifiesIndexField_thenVerifiesCorrectIndex() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(10);
        stageResponse1.setStageName("Stage 10");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(20);
        stageResponse2.setStageName("Stage 20");

        StageResponse stageResponse3 = new StageResponse();
        stageResponse3.setStageId(30);
        stageResponse3.setStageName("Stage 30");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2, stageResponse3);

        when(matchStageEntityService.findMatchStage(1L, 10)).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(1L, 20)).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(1L, 30)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        // Index should match stage ID
        assertEquals(10, result.getFirst().getIndex());
        assertEquals(20, result.get(1).getIndex());
        assertEquals(30, result.get(2).getIndex());

        // Stage number should also match stage ID
        assertEquals(10, result.getFirst().getStageNumber());
        assertEquals(20, result.get(1).getStageNumber());
        assertEquals(30, result.get(2).getStageNumber());
    }

    @Test
    public void testInitStages_withSpecialCharactersInStageName_thenPreservesCharacters() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName("Stage & Co. (Test) - v2.0");

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Stage & Co. (Test) - v2.0", result.getFirst().getStageName());
    }

    @Test
    public void testInitStages_withNullStageName_thenPreservesNullName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(1);
        stageResponse.setStageName(null);

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst().getStageName());
    }

    @Test
    public void testInitStages_withLargeStageIds_thenHandlesLargeIds() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(9999);
        stageResponse1.setStageName("Large ID Stage 1");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(10000);
        stageResponse2.setStageName("Large ID Stage 2");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        when(matchStageEntityService.findMatchStage(1L, 9999)).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(1L, 10000)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(9999, result.getFirst().getStageNumber());
        assertEquals(10000, result.get(1).getStageNumber());
    }

    @Test
    public void testInitStages_withAllFieldsAtMaxValues_thenPreservesMaxValues() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(Integer.MAX_VALUE);
        stageResponse.setStageName("Max Values Stage");
        stageResponse.setTargetPaper(Integer.MAX_VALUE);
        stageResponse.setTargetPopper(Integer.MAX_VALUE);
        stageResponse.setTargetPlates(Integer.MAX_VALUE);
        stageResponse.setTargetDisappear(Integer.MAX_VALUE);
        stageResponse.setTargetPenalty(Integer.MAX_VALUE);
        stageResponse.setMinRounds(Integer.MAX_VALUE);
        stageResponse.setMaxPoints(Integer.MAX_VALUE);

        List<StageResponse> stageResponses = List.of(stageResponse);

        when(matchStageEntityService.findMatchStage(1L, Integer.MAX_VALUE)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = ipscMatchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageDto resultDto = result.getFirst();

        assertEquals(Integer.MAX_VALUE, resultDto.getStageNumber());
        assertEquals(Integer.MAX_VALUE, resultDto.getTargetPaper());
        assertEquals(Integer.MAX_VALUE, resultDto.getTargetPopper());
        assertEquals(Integer.MAX_VALUE, resultDto.getTargetPlates());
        assertEquals(Integer.MAX_VALUE, resultDto.getTargetDisappear());
        assertEquals(Integer.MAX_VALUE, resultDto.getTargetPenalty());
        assertEquals(Integer.MAX_VALUE, resultDto.getMinRounds());
        assertEquals(Integer.MAX_VALUE, resultDto.getMaxPoints());
    }

    @Test
    public void testInitScores_withNullIpscResponse_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        // Act
        ipscMatchResultService.initScores(matchResultsDto, null);

        // Assert
        verifyNoInteractions(competitorEntityService, matchCompetitorEntityService, matchStageCompetitorEntityService);
    }

    @Test
    public void testInitScores_withNullScores_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(null);

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        verifyNoInteractions(competitorEntityService, matchCompetitorEntityService, matchStageCompetitorEntityService);
    }

    @Test
    public void testInitScores_withNullMembers_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of());
        ipscResponse.setMembers(null);

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        verifyNoInteractions(competitorEntityService, matchCompetitorEntityService, matchStageCompetitorEntityService);
    }

    @Test
    public void testInitScores_withNoMembersWithScores_thenInitializesEmptyCompetitorsList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(200);
        List<MemberRequest> memberRequests = List.of(memberResponse);
        ipscResponse.setMembers(memberRequests.stream().map(MemberResponse::new).toList());

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertTrue(matchResultsDto.getCompetitors().isEmpty());
        verifyNoInteractions(competitorEntityService, matchCompetitorEntityService, matchStageCompetitorEntityService);
    }

    @Disabled
    @Test
    public void testInitScores_withExistingCompetitorsAndMatchCompetitors_thenInitializesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        scoreResponse.setFinalScore(10);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(100);
        memberResponse.setIcsAlias("ALIAS100");
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        List<MemberRequest> memberRequests = List.of(memberResponse);
        ipscResponse.setMembers(memberRequests.stream().map(MemberResponse::new).toList());

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(10L);
        existingCompetitor.setCompetitorNumber("ALIAS100");

        MatchCompetitor existingMatchCompetitor = new MatchCompetitor();
        existingMatchCompetitor.setId(20L);

        when(competitorEntityService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.of(existingCompetitor));
        when(matchCompetitorEntityService.findMatchCompetitor(10L, 1L))
                .thenReturn(Optional.of(existingMatchCompetitor));

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertEquals(1, matchResultsDto.getCompetitors().size());
        assertEquals(10L, matchResultsDto.getCompetitors().getFirst().getId());
        assertEquals(1, matchResultsDto.getMatchCompetitors().size());
        verify(competitorEntityService, times(1))
                .findCompetitor("ALIAS100", "John", "Doe", null);
        verify(matchCompetitorEntityService, times(1))
                .findMatchCompetitor(10L, 1L);
    }

    @Disabled
    @Test
    public void testInitScores_withNonExistingCompetitorsAndMatchCompetitors_thenCreatesNew() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        scoreResponse.setFinalScore(100);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(100);
        memberResponse.setIcsAlias("ALIAS100");
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        List<MemberRequest> memberRequests = List.of(memberResponse);
        ipscResponse.setMembers(memberRequests.stream().map(MemberResponse::new).toList());

        when(competitorEntityService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(null, 1L))
                .thenReturn(Optional.empty());

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertEquals(1, matchResultsDto.getCompetitors().size());
        assertNull(matchResultsDto.getCompetitors().getFirst().getId());
        assertEquals(1, matchResultsDto.getMatchCompetitors().size());
        verify(competitorEntityService, times(1))
                .findCompetitor("ALIAS100", "John", "Doe", null);
        verify(matchCompetitorEntityService, times(1))
                .findMatchCompetitor(null, 1L);
    }

    @Disabled
    @Test
    public void testInitScores_withMatchStageCompetitorsScores_thenInitializesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setId(50L);
        stageDto.setStageNumber(1);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setStages(List.of(stageDto));

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        scoreResponse.setFinalScore(100);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(100);
        memberRequest.setIcsAlias("ALIAS100");
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        MemberResponse memberResponse = new MemberResponse(memberRequest);
        ipscResponse.setMembers(List.of(memberResponse));

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(10L);

        MatchCompetitor existingMatchCompetitor = new MatchCompetitor();
        existingMatchCompetitor.setId(20L);

        MatchStageCompetitor existingMatchStageCompetitor = new MatchStageCompetitor();
        existingMatchStageCompetitor.setId(30L);

        when(competitorEntityService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.of(existingCompetitor));
        when(matchCompetitorEntityService.findMatchCompetitor(10L, 1L))
                .thenReturn(Optional.of(existingMatchCompetitor));
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(50L, 10L))
                .thenReturn(Optional.of(existingMatchStageCompetitor));

        // Act
        ipscMatchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getMatchStageCompetitors());
        assertEquals(1, matchResultsDto.getMatchStageCompetitors().size());
        assertEquals(30L, matchResultsDto.getMatchStageCompetitors().getFirst().getId());
        verify(matchStageCompetitorEntityService, times(1))
                .findMatchStageCompetitor(50L, 10L);
    }

    @Test
    public void testInitClubEntity_withNullClubDto_returnsEmpty() {
        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(null);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubEntityService, never()).findClubById(anyLong());
    }

    @Test
    public void testInitClubEntity_withNullClubId_returnsEmpty() {
        // Arrange
        ClubDto clubDtoWithoutId = new ClubDto();
        clubDtoWithoutId.setId(null);
        clubDtoWithoutId.setName("Test Club");
        clubDtoWithoutId.setAbbreviation("TC");

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoWithoutId);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubEntityService, never()).findClubById(anyLong());
    }

    @Test
    public void testInitClubEntity_withValidId_returnsInitializedClub() {
        // Arrange
        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals(1L, resultClub.getId());
        assertEquals("Test Club", resultClub.getName());
        assertEquals("TC", resultClub.getAbbreviation());
        verify(clubEntityService, times(1)).findClubById(1L);
    }

    @Test
    public void testInitClubEntity_callsInitMethod() {
        // Arrange
        Club spyClub = spy(clubEntity);
        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(spyClub));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        verify(spyClub, times(1)).init(clubDto);
    }

    @Test
    public void testInitClubEntity_transfersNameFromDtoToEntity() {
        // Arrange
        ClubDto clubDtoWithDifferentName = new ClubDto();
        clubDtoWithDifferentName.setId(1L);
        clubDtoWithDifferentName.setName("New Club Name");
        clubDtoWithDifferentName.setAbbreviation("NC");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Club Name");
        clubEntityForTest.setAbbreviation("OCN");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoWithDifferentName);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("New Club Name", resultClub.getName());
    }

    @Test
    public void testInitClubEntity_transfersAbbreviationFromDtoToEntity() {
        // Arrange
        ClubDto clubDtoWithDifferentAbbr = new ClubDto();
        clubDtoWithDifferentAbbr.setId(1L);
        clubDtoWithDifferentAbbr.setName("Test Club");
        clubDtoWithDifferentAbbr.setAbbreviation("NEW");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Test Club");
        clubEntityForTest.setAbbreviation("OLD");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoWithDifferentAbbr);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("NEW", resultClub.getAbbreviation());
    }

    @Test
    public void testInitClubEntity_clubNotFoundInRepository_returnsEmpty() {
        // Arrange
        when(clubEntityService.findClubById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubEntityService, times(1)).findClubById(1L);
    }

    @Test
    public void testInitClubEntity_withOnlyNameSet_returnsEmpty() {
        // Arrange
        ClubDto clubDtoNameOnly = new ClubDto();
        clubDtoNameOnly.setId(null); // No ID, so repository won't be queried
        clubDtoNameOnly.setName("Test Club");

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoNameOnly);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubEntityService, never()).findClubById(anyLong());
    }

    @Test
    public void testInitClubEntity_withNullAbbreviation_initializesSuccessfully() {
        // Arrange
        ClubDto clubDtoNoAbbr = new ClubDto();
        clubDtoNoAbbr.setId(1L);
        clubDtoNoAbbr.setName("Test Club");
        clubDtoNoAbbr.setAbbreviation(null);

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Name");
        clubEntityForTest.setAbbreviation("OLD");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoNoAbbr);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("Test Club", resultClub.getName());
        assertEquals("OLD", resultClub.getAbbreviation());
    }

    @Test
    public void testInitClubEntity_verifiesRepositoryCallParameters() {
        // Arrange
        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        verify(clubEntityService).findClubById(1L);
    }

    @Test
    public void testInitClubEntity_doesNotModifyDto() {
        // Arrange
        ClubDto clubDtoOriginal = new ClubDto();
        clubDtoOriginal.setId(1L);
        clubDtoOriginal.setName("Test Club");
        clubDtoOriginal.setAbbreviation("TC");

        String originalName = clubDtoOriginal.getName();
        String originalAbbr = clubDtoOriginal.getAbbreviation();

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        ipscMatchResultService.initClubEntity(clubDtoOriginal);

        // Assert
        assertEquals(originalName, clubDtoOriginal.getName());
        assertEquals(originalAbbr, clubDtoOriginal.getAbbreviation());
    }

    @Test
    public void testInitClubEntity_withSameIdAndName_returnsSameEntity() {
        // Arrange
        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        Optional<Club> result1 = ipscMatchResultService.initClubEntity(clubDto);
        Optional<Club> result2 = ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getId(), result2.get().getId());
        verify(clubEntityService, times(2)).findClubById(1L);
    }

    @Test
    public void testInitClubEntity_withDifferentIds_queriesSeparately() {
        // Arrange
        ClubDto clubDto1 = new ClubDto();
        clubDto1.setId(1L);
        clubDto1.setName("Club 1");

        ClubDto clubDto2 = new ClubDto();
        clubDto2.setId(2L);
        clubDto2.setName("Club 2");

        Club club1 = new Club();
        club1.setId(1L);
        club1.setName("Club 1");

        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(club1));
        when(clubEntityService.findClubById(2L)).thenReturn(Optional.of(club2));

        // Act
        Optional<Club> result1 = ipscMatchResultService.initClubEntity(clubDto1);
        Optional<Club> result2 = ipscMatchResultService.initClubEntity(clubDto2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(1L, result1.get().getId());
        assertEquals(2L, result2.get().getId());
        verify(clubEntityService).findClubById(1L);
    }

    @Test
    public void testInitClubEntity_withEmptyStringName_initializesWithEmpty() {
        // Arrange
        ClubDto clubDtoEmptyName = new ClubDto();
        clubDtoEmptyName.setId(1L);
        clubDtoEmptyName.setName("");
        clubDtoEmptyName.setAbbreviation("TC");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Name");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoEmptyName);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("Old Name", resultClub.getName());
    }

    @Test
    public void testInitClubEntity_preservesOtherProperties() {
        // Arrange
        Club clubWithMatches = new Club();
        clubWithMatches.setId(1L);
        clubWithMatches.setName("Test Club");
        clubWithMatches.setAbbreviation("TC");
        clubWithMatches.setMatches(new ArrayList<>()); // Add some other property

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(clubWithMatches));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertNotNull(resultClub.getMatches());
    }

    @Test
    public void testInitClubEntity_withLargeIdValue_returnsOptional() {
        // Arrange
        ClubDto clubDtoLargeId = new ClubDto();
        clubDtoLargeId.setId(Long.MAX_VALUE);
        clubDtoLargeId.setName("Test Club");

        Club clubForLargeId = new Club();
        clubForLargeId.setId(Long.MAX_VALUE);

        when(clubEntityService.findClubById(Long.MAX_VALUE)).thenReturn(Optional.of(clubForLargeId));

        // Act
        Optional<Club> result = ipscMatchResultService.initClubEntity(clubDtoLargeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Long.MAX_VALUE, result.get().getId());
    }

    @Test
    public void testInitMatchEntity_withNullMatchId_createsNewMatch() {
        // Arrange
        MatchDto matchDtoNoId = new MatchDto();
        matchDtoNoId.setId(null);
        matchDtoNoId.setName("New Match");
        matchDtoNoId.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoNoId, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals("New Match", resultMatch.getName());
        assertEquals(LocalDateTime.of(2026, 3, 15, 10, 0), resultMatch.getScheduledDate());
        verify(matchEntityService, never()).findMatchById(anyLong());
    }

    @Test
    public void testInitMatchEntity_withValidId_returnsInitializedMatch() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals(100L, resultMatch.getId());
        assertEquals("Test Match", resultMatch.getName());
        verify(matchEntityService, times(1)).findMatchById(100L);
    }

    @Test
    public void testInitMatchEntity_alwaysReturnsPresent() {
        // Arrange - match not found in repository
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert - still returns present with newly created entity
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchEntity_matchNotFoundInRepository_createsNewMatch() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNotNull(resultMatch);
        verify(matchEntityService, times(1)).findMatchById(100L);
    }

    @Test
    public void testInitMatchEntity_callsInitMethod() {
        // Arrange
        IpscMatch spyMatch = spy(matchEntity);
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(spyMatch));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        verify(spyMatch, times(1)).init(matchDto, clubEntity);
    }

    @Test
    public void testInitMatchEntity_setsClubOnMatch() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals(clubEntity, resultMatch.getClub());
    }

    @Test
    public void testInitMatchEntity_withNullClub_returnsMatchWithoutClub() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, null);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNull(resultMatch.getClub());
    }

    @Test
    public void testInitMatchEntity_transfersNameFromDtoToEntity() {
        // Arrange
        MatchDto matchDtoWithName = new MatchDto();
        matchDtoWithName.setId(100L);
        matchDtoWithName.setName("Updated Match Name");
        matchDtoWithName.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Old Match Name");

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoWithName, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Match Name", result.get().getName());
    }

    @Test
    public void testInitMatchEntity_transfersScheduledDateFromDtoToEntity() {
        // Arrange
        MatchDto matchDtoWithDate = new MatchDto();
        matchDtoWithDate.setId(100L);
        matchDtoWithDate.setName("Test Match");
        matchDtoWithDate.setScheduledDate(LocalDateTime.of(2026, 5, 20, 14, 30));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoWithDate, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(LocalDateTime.of(2026, 5, 20, 14, 30), result.get().getScheduledDate());
    }

    @Test
    public void testInitMatchEntity_withNullScheduledDate_returnsMatch() {
        // Arrange
        MatchDto matchDtoNoDate = new MatchDto();
        matchDtoNoDate.setId(100L);
        matchDtoNoDate.setName("Test Match");
        matchDtoNoDate.setScheduledDate(null);

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoNoDate, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Disabled
    @Test
    public void testInitMatchEntity_verifiesRepositoryCallWithCorrectId() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        verify(matchEntityService).findMatchById(eq(100L));
        verify(ipscMatchRepository, times(1)).findById(100L);
    }

    @Test
    public void testInitMatchEntity_doesNotModifyDto() {
        // Arrange
        MatchDto originalMatchDto = new MatchDto();
        originalMatchDto.setId(100L);
        originalMatchDto.setName("Original Name");
        originalMatchDto.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        String originalName = originalMatchDto.getName();
        LocalDateTime originalDate = originalMatchDto.getScheduledDate();

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        ipscMatchResultService.initMatchEntity(originalMatchDto, clubEntity);

        // Assert
        assertEquals(originalName, originalMatchDto.getName());
        assertEquals(originalDate, originalMatchDto.getScheduledDate());
    }

    @Test
    public void testInitMatchEntity_withSameIdMultipleTimes_returnsConsistently() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result1 = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);
        Optional<IpscMatch> result2 = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getId(), result2.get().getId());
        verify(matchEntityService, times(2)).findMatchById(100L);
    }

    @Test
    public void testInitMatchEntity_withDifferentIds_queriesSeparately() {
        // Arrange
        ClubDto clubDto1 = new ClubDto();
        clubDto1.setId(1L);
        clubDto1.setName("Club 1");

        ClubDto clubDto2 = new ClubDto();
        clubDto2.setId(2L);
        clubDto2.setName("Club 2");

        Club club1 = new Club();
        club1.setId(1L);
        club1.setName("Club 1");

        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        when(clubEntityService.findClubById(1L)).thenReturn(Optional.of(club1));
        when(clubEntityService.findClubById(2L)).thenReturn(Optional.of(club2));

        // Act
        Optional<Club> result1 = ipscMatchResultService.initClubEntity(clubDto1);
        Optional<Club> result2 = ipscMatchResultService.initClubEntity(clubDto2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(1L, result1.get().getId());
        assertEquals(2L, result2.get().getId());
        verify(clubEntityService).findClubById(1L);
        verify(clubEntityService).findClubById(2L);
    }

    @Test
    public void testInitMatchEntity_withDifferentClubs_setsCorrectClub() {
        // Arrange
        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        // Create two different mock match instances to return for each call
        IpscMatch match1 = new IpscMatch();
        match1.setId(100L);
        match1.setClub(clubEntity);

        IpscMatch match2 = new IpscMatch();
        match2.setId(100L);
        match2.setClub(club2);

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(match1)).thenReturn(Optional.of(match2));

        // Act
        Optional<IpscMatch> result1 = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);
        Optional<IpscMatch> result2 = ipscMatchResultService.initMatchEntity(matchDto, club2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        // After init() is called, the club is set via setClub() method
        assertEquals(clubEntity, result1.get().getClub());
        assertEquals(club2, result2.get().getClub());
    }

    @Test
    public void testInitMatchEntity_withLargeIdValue_returnsOptional() {
        // Arrange
        MatchDto matchDtoLargeId = new MatchDto();
        matchDtoLargeId.setId(Long.MAX_VALUE);
        matchDtoLargeId.setName("Test Match");
        matchDtoLargeId.setScheduledDate(LocalDateTime.now());

        IpscMatch matchForLargeId = new IpscMatch();
        matchForLargeId.setId(Long.MAX_VALUE);

        when(matchEntityService.findMatchById(Long.MAX_VALUE)).thenReturn(Optional.of(matchForLargeId));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoLargeId, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Long.MAX_VALUE, result.get().getId());
    }

    @Test
    public void testInitMatchEntity_preservesOtherProperties() {
        // Arrange
        IpscMatch matchWithDetails = spy(new IpscMatch());
        matchWithDetails.setId(100L);
        matchWithDetails.setName("Test Match");
        LocalDateTime createdDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        matchWithDetails.setDateCreated(createdDate);

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchWithDetails));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        // Verify that init was called, which updates the properties
        verify(matchWithDetails).init(eq(matchDto), eq(clubEntity));
        // The match entity should be returned
        assertNotNull(result.get());
    }

    @Test
    public void testInitMatchEntity_withEmptyStringName_returnsMatch() {
        // Arrange
        MatchDto matchDtoEmptyName = new MatchDto();
        matchDtoEmptyName.setId(100L);
        matchDtoEmptyName.setName("");
        matchDtoEmptyName.setScheduledDate(LocalDateTime.now());

        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDtoEmptyName, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().getName());
    }

    @Test
    public void testInitMatchEntity_verifyInitMethodParameters() {
        // Arrange
        IpscMatch spyMatch = spy(new IpscMatch());
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.of(spyMatch));

        // Act
        ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        verify(spyMatch).init(eq(matchDto), eq(clubEntity));
    }

    @Test
    public void testInitMatchEntity_whenRepositoryReturnsEmpty_createsNewInstance() {
        // Arrange
        when(matchEntityService.findMatchById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscMatchResultService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNotNull(resultMatch);
        // Verify it's a fresh instance by checking ID (new instance should have null ID)
        verify(matchEntityService, times(1)).findMatchById(100L);
    }

    @Test
    public void testInitCompetitorEntities_withNullList_returnsEmptyMap() {
        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    public void testInitCompetitorEntities_withEmptyList_returnsEmptyMap() {
        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    public void testInitCompetitorEntities_withSingleCompetitor_returnsMapWithCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C001");

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        assertEquals(competitor, result.get(competitorUuid));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    public void testInitCompetitorEntities_withMultipleCompetitors_returnsMapWithAllCompetitors() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");
        competitorDto1.setLastName("Doe");
        competitorDto1.setCompetitorNumber("C001");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");
        competitorDto2.setLastName("Smith");
        competitorDto2.setCompetitorNumber("C002");

        CompetitorDto competitorDto3 = new CompetitorDto();
        competitorDto3.setId(3L);
        competitorDto3.setUuid(uuid3);
        competitorDto3.setFirstName("Bob");
        competitorDto3.setLastName("Johnson");
        competitorDto3.setCompetitorNumber("C003");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        Competitor competitor3 = new Competitor();
        competitor3.setId(3L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));
        when(competitorRepository.findById(3L)).thenReturn(Optional.of(competitor3));

        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2, competitorDto3));

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        assertTrue(result.containsKey(uuid3));
        assertEquals(competitor1, result.get(uuid1));
        assertEquals(competitor2, result.get(uuid2));
        assertEquals(competitor3, result.get(uuid3));
    }

    @Test
    public void testInitCompetitorEntities_withNullCompetitorId_createsNewCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    public void testInitCompetitorEntities_competitorNotInRepository_createsNewCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    public void testInitCompetitorEntities_usesUuidAsKey() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));

        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertEquals(competitor1, result.get(uuid1));
        assertEquals(competitor2, result.get(uuid2));
        assertNull(result.get(UUID.randomUUID())); // Different UUID not in map
    }

    @Test
    public void testInitCompetitorEntities_callsInitMethod() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        Competitor spyCompetitor = spy(new Competitor());
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(spyCompetitor));

        // Act
        ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        verify(spyCompetitor, times(1)).init(competitorDto);
    }

    @Test
    public void testInitCompetitorEntities_transfersDataFromDtoToEntity() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(1L);
        existingCompetitor.setFirstName("Old");
        existingCompetitor.setLastName("Name");

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existingCompetitor));

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        Competitor resultCompetitor = result.get(competitorUuid);
        assertNotNull(resultCompetitor);
        assertEquals("John", resultCompetitor.getFirstName());
        assertEquals("Doe", resultCompetitor.getLastName());
        assertEquals("C001", resultCompetitor.getCompetitorNumber());
    }

    @Test
    public void testInitCompetitorEntities_withMixedNullAndNonNullIds() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(null);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));

        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    public void testInitCompetitorEntities_preservesOtherProperties() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setCompetitorMatches(new ArrayList<>()); // Other property

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        Competitor resultCompetitor = result.get(competitorUuid);
        assertNotNull(resultCompetitor.getCompetitorMatches());
    }

    @Test
    public void testInitCompetitorEntities_withLargeNumberOfCompetitors_processesAll() {
        // Arrange
        List<CompetitorDto> competitorDtos = new ArrayList<>();
        int numCompetitors = 100;

        for (int i = 0; i < numCompetitors; i++) {
            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setId((long) i);
            competitorDto.setUuid(UUID.randomUUID());
            competitorDto.setFirstName("Competitor");
            competitorDto.setLastName("" + i);
            competitorDtos.add(competitorDto);

            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            when(competitorRepository.findById((long) i)).thenReturn(Optional.of(competitor));
        }

        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(competitorDtos);

        // Assert
        assertEquals(numCompetitors, result.size());
    }

    @Test
    public void testInitCompetitorEntities_verifiesRepositoryCallsForEachCompetitor() {
        // Arrange
        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(UUID.randomUUID());

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(UUID.randomUUID());

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));

        // Act
        ipscMatchResultService.initCompetitorEntities(Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        verify(competitorRepository).findById(1L);
        verify(competitorRepository).findById(2L);
        verify(competitorRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testInitCompetitorEntities_withDuplicateIds_lastOneWins() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("First");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(1L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Second");

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result = ipscMatchResultService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        // Both should exist with same competitor instance
        assertEquals(result.get(uuid1), result.get(uuid2));
    }

    @Test
    public void testInitCompetitorEntities_withSpecialCharactersInNames() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("Jean-Paul");
        competitorDto.setLastName("O'Brien");
        competitorDto.setCompetitorNumber("C-001");

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscMatchResultService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertEquals(1, result.size());
        Competitor resultCompetitor = result.get(competitorUuid);
        assertEquals("Jean-Paul", resultCompetitor.getFirstName());
        assertEquals("O'Brien", resultCompetitor.getLastName());
    }

    @Test
    public void testInitCompetitorEntities_returnsNewMapInstance() {
        // Act
        Map<UUID, Competitor> result1 = ipscMatchResultService.initCompetitorEntities(null);
        Map<UUID, Competitor> result2 = ipscMatchResultService.initCompetitorEntities(null);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2); // Different instances
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    public void testInitMatchCompetitorEntities_withNullList_returnsEmptyMap() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Test
    public void testInitMatchCompetitorEntities_withEmptyList_returnsEmptyMap() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                new ArrayList<>(), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withSingleCompetitor_returnsMapWithCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(competitorUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(competitorUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
        assertEquals(matchCompetitor, result.get(mcUuid));
        verify(matchCompetitorRepository, times(1)).findById(20L);
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withMultipleCompetitors_returnsMapWithAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertTrue(result.containsKey(mc2Uuid));
        assertEquals(matchCompetitor1, result.get(mc1Uuid));
        assertEquals(matchCompetitor2, result.get(mc2Uuid));
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_filtersByClubReference() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC); // Should be included

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClubName(ClubIdentifier.SOSC); // Should be excluded

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertFalse(result.containsKey(mc2Uuid));
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withUnknownClubReference_includesAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClubName(ClubIdentifier.SOSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.UNKNOWN);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertTrue(result.containsKey(mc2Uuid));
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withNullClubReference_includesAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClubName(ClubIdentifier.SOSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap, null);

        // Assert
        assertEquals(2, result.size());
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withNullId_createsNewMatchCompetitor() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(null);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_notFoundInRepository_createsNew() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.empty());

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_usesUuidAsKey() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto);
        matchCompetitorDto2.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(matchCompetitor1, result.get(mc1Uuid));
        assertEquals(matchCompetitor2, result.get(mc2Uuid));
        assertNull(result.get(UUID.randomUUID()));
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_callsInitMethod() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor spyMatchCompetitor = spy(new MatchCompetitor());
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(spyMatchCompetitor));

        // Act
        ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        verify(spyMatchCompetitor, times(1)).init(matchCompetitorDto, matchEntity, competitor);
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_linksBothMatchAndCompetitor() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        MatchCompetitor resultMC = result.get(mcUuid);
        assertEquals(matchEntity, resultMC.getMatch());
        assertEquals(competitor, resultMC.getCompetitor());
    }

    @Test
    public void testInitMatchCompetitorEntities_competitorNotInMap_skips() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();
        UUID unknownUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(unknownUuid); // Not in map

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withLargeNumber_processesAll() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        List<MatchCompetitorDto> matchCompetitorDtos = new ArrayList<>();
        int numCompetitors = 50;

        for (int i = 0; i < numCompetitors; i++) {
            UUID cUuid = UUID.randomUUID();
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitorMap.put(cUuid, competitor);

            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setUuid(cUuid);

            MatchCompetitorDto mcDto = new MatchCompetitorDto();
            mcDto.setId(100L + i);
            mcDto.setUuid(UUID.randomUUID());
            mcDto.setCompetitor(competitorDto);
            mcDto.setClubName(ClubIdentifier.HPSC);
            matchCompetitorDtos.add(mcDto);

            MatchCompetitor mc = new MatchCompetitor();
            mc.setId(100L + i);
            when(matchCompetitorRepository.findById(100L + i)).thenReturn(Optional.of(mc));
        }

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                matchCompetitorDtos, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(numCompetitors, result.size());
    }

    @Test
    public void testInitMatchCompetitorEntities_verifiesRepositoryCalls() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        ipscMatchResultService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        verify(matchCompetitorRepository, times(1)).findById(20L);
    }

    @Test
    public void testInitMatchCompetitorEntities_returnsNewMapInstance() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result1 = ipscMatchResultService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);
        Map<UUID, MatchCompetitor> result2 = ipscMatchResultService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Disabled
    @Test
    public void testInitMatchCompetitorEntities_withDuplicateIds() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto);
        matchCompetitorDto1.setClubName(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(20L); // Same ID
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto);
        matchCompetitorDto2.setClubName(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscMatchResultService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(2, result.size());
        assertEquals(matchCompetitor, result.get(mc1Uuid));
        assertEquals(matchCompetitor, result.get(mc2Uuid));
    }
}
