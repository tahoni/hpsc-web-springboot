package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.models.ipsc.request.MemberRequest;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO: redo some tests
// TODO: add tests for scores not zero/null
// TODO: add tests for scores zero/null
// TODO: add tests for club identifier
@ExtendWith(MockitoExtension.class)
public class IpscMatchResultServiceImplTest {
    @Mock
    private ClubEntityService clubEntityService;
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
}
