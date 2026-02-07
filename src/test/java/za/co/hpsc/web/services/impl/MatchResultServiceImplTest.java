package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.match.ClubDto;
import za.co.hpsc.web.models.match.MatchDto;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link MatchResultServiceImpl} class.
 * This class focuses on testing the business logic related to initializing match results,
 * specifically the {@code initClub} method.
 */
@ExtendWith(MockitoExtension.class)
public class MatchResultServiceImplTest {

    @Mock
    private ClubService clubService;

    @Mock
    private MatchService matchService;

    @Mock
    private CompetitorService competitorService;

    @Mock
    private MatchStageService matchStageService;

    @Mock
    private MatchCompetitorService matchCompetitorService;

    @Mock
    private MatchStageCompetitorService matchStageCompetitorService;

    @InjectMocks
    private MatchResultServiceImpl matchResultService;

    /**
     * Test case to verify that the {@code initClub} method returns an empty {@link Optional}
     * when the provided {@link ClubResponse} is null.
     */
    @Test
    public void testInitClub_withNullResponse_thenReturnsEmptyOptional() {
        // Arrange - No additional setup needed as we pass null directly

        // Act
        Optional<ClubDto> result = matchResultService.initClub(null);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty when ClubResponse is null");
        verifyNoInteractions(clubService);
    }

    /**
     * Test case to verify that the {@code initClub} method returns a {@link ClubDto} populated
     * with information from an existing {@link Club} when the club is found by the {@link ClubService}.
     */
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

        when(clubService.findClub("Test Club", "TC")).thenReturn(Optional.of(existingClub));

        // Act
        Optional<ClubDto> result = matchResultService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent(), "Result should be present when club exists");
        ClubDto clubDto = result.get();
        assertEquals(101L, clubDto.getId());
        assertEquals("Test Club", clubDto.getName());
        assertEquals("TC", clubDto.getAbbreviation());
        verify(clubService, times(1)).findClub("Test Club", "TC");
    }

    /**
     * Test case to verify that the {@code initClub} method returns a {@link ClubDto} populated
     * with information from the {@link ClubResponse} when the club is not found by the {@link ClubService}.
     */
    @Test
    public void testInitClub_withNonExistingClub_thenReturnsClubDtoFromResponse() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Non-existent Club");
        clubResponse.setClubCode("NC");

        when(clubService.findClub("Non-existent Club", "NC")).thenReturn(Optional.empty());

        // Act
        Optional<ClubDto> result = matchResultService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent(), "Result should be present with a ClubDto");
        ClubDto clubDto = result.get();
        assertNull(clubDto.getId(), "ID should be null for a new ClubDto");
        assertEquals("Non-existent Club", clubDto.getName(), "Name should be populated from response");
        assertEquals("NC", clubDto.getAbbreviation(), "Abbreviation should be populated from response");
        verify(clubService, times(1)).findClub("Non-existent Club", "NC");
    }

    /**
     * Test case to verify that the {@code initMatch} method returns an empty {@link Optional}
     * when the match exists but there are no newer scores in the {@link IpscResponse}.
     */
    @Test
    public void testInitMatch_withExistingMatchAndNoNewerScores_thenReturnsEmptyOptional() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        za.co.hpsc.web.models.ipsc.response.MatchResponse matchResponse = new za.co.hpsc.web.models.ipsc.response.MatchResponse();
        matchResponse.setMatchName("Existing Match");
//        matchResponse.setMatchDate(java.time.LocalDate.of(2025, 1, 15));
        ipscResponse.setMatch(matchResponse);

        za.co.hpsc.web.models.ipsc.response.ScoreResponse scoreResponse = new za.co.hpsc.web.models.ipsc.response.ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 10, 10, 0));
        ipscResponse.setScores(java.util.List.of(scoreResponse));

        Match existingMatch = new Match();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(java.time.LocalDate.of(2025, 1, 15));
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

//        when(matchService.findMatch("Existing Match", java.time.LocalDate.of(2025, 1, 15)))
//                .thenReturn(Optional.of(existingMatch));

        ClubDto clubDto = new ClubDto(null, "Test Club", "TC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty when match exists but has no newer scores");
//        verify(matchService, times(1)).findMatch("Existing Match", java.time.LocalDate.of(2025, 1, 15));
    }

    /**
     * Test case to verify that the {@code initMatch} method returns a populated {@link MatchDto}
     * when the match exists and there are newer scores in the {@link IpscResponse}.
     */
    @Test
    public void testInitMatch_withExistingMatchAndNewerScores_thenReturnsPopulatedMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        za.co.hpsc.web.models.ipsc.response.MatchResponse matchResponse = new za.co.hpsc.web.models.ipsc.response.MatchResponse();
        matchResponse.setMatchName("Existing Match");
//        matchResponse.setMatchDate(java.time.LocalDate.of(2025, 1, 15));
        ipscResponse.setMatch(matchResponse);

        za.co.hpsc.web.models.ipsc.response.ScoreResponse scoreResponse = new za.co.hpsc.web.models.ipsc.response.ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 25, 10, 0));
        ipscResponse.setScores(java.util.List.of(scoreResponse));

        Match existingMatch = new Match();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(java.time.LocalDate.of(2025, 1, 15));
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

//        when(matchService.findMatch("Existing Match", java.time.LocalDate.of(2025, 1, 15)))
//                .thenReturn(Optional.of(existingMatch));

        ClubDto clubDto = new ClubDto(1L, "Test Club", "TC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent(), "Result should be present when match exists with newer scores");
        MatchDto matchDto = result.get();
        assertEquals(1L, matchDto.getId());
        assertEquals(clubDto, matchDto.getClub());
//        verify(matchService, times(1)).findMatch("Existing Match", java.time.LocalDate.of(2025, 1, 15));
    }

    /**
     * Test case to verify that the {@code initMatch} method returns a new {@link MatchDto}
     * when the match does not exist in the database.
     */
    @Test
    public void testInitMatch_withNonExistingMatch_thenReturnsNewMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        za.co.hpsc.web.models.ipsc.response.MatchResponse matchResponse = new za.co.hpsc.web.models.ipsc.response.MatchResponse();
        matchResponse.setMatchName("New Match");
//        matchResponse.setMatchDate(java.time.LocalDate.of(2025, 2, 1));
        ipscResponse.setMatch(matchResponse);

        za.co.hpsc.web.models.ipsc.response.ScoreResponse scoreResponse = new za.co.hpsc.web.models.ipsc.response.ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 1, 10, 0));
        ipscResponse.setScores(java.util.List.of(scoreResponse));

//        when(matchService.findMatch("New Match", java.time.LocalDate.of(2025, 2, 1)))
//                .thenReturn(Optional.empty());

        ClubDto clubDto = new ClubDto(null, "New Club", "NC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent(), "Result should be present for a new match");
        MatchDto matchDto = result.get();
        assertNull(matchDto.getId(), "ID should be null for a new match");
        assertEquals(clubDto, matchDto.getClub());
//        verify(matchService, times(1)).findMatch("New Match", java.time.LocalDate.of(2025, 2, 1));
    }

    /**
     * Test case to verify that the {@code initStages} method returns an empty list
     * when the provided stage responses list is null.
     */
    @Test
    public void testInitStages_withNullStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        java.util.List<za.co.hpsc.web.models.match.MatchStageDto> result = matchResultService.initStages(matchDto, null);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty when stage responses is null");
        verifyNoInteractions(matchStageService);
    }

    /**
     * Test case to verify that the {@code initStages} method returns an empty list
     * when the provided stage responses list is empty.
     */
    @Test
    public void testInitStages_withEmptyStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        java.util.List<za.co.hpsc.web.models.match.MatchStageDto> result = matchResultService.initStages(matchDto, java.util.List.of());

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty when stage responses is empty");
        verifyNoInteractions(matchStageService);
    }

    /**
     * Test case to verify that the {@code initStages} method returns a list of {@link za.co.hpsc.web.models.match.MatchStageDto}
     * with existing match stages when stages are found by the {@link MatchStageService}.
     */
    @Test
    public void testInitStages_withExistingMatchStages_thenReturnsPopulatedMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        za.co.hpsc.web.models.ipsc.response.StageResponse stageResponse1 = new za.co.hpsc.web.models.ipsc.response.StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("Stage 1");

        za.co.hpsc.web.models.ipsc.response.StageResponse stageResponse2 = new za.co.hpsc.web.models.ipsc.response.StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("Stage 2");

        java.util.List<za.co.hpsc.web.models.ipsc.response.StageResponse> stageResponses = java.util.List.of(stageResponse1, stageResponse2);

        Match match = new Match();
        match.setId(1L);

        za.co.hpsc.web.domain.MatchStage matchStage1 = new za.co.hpsc.web.domain.MatchStage();
        matchStage1.setId(10L);
        matchStage1.setMatch(match);
        matchStage1.setStageNumber(1);

        za.co.hpsc.web.domain.MatchStage matchStage2 = new za.co.hpsc.web.domain.MatchStage();
        matchStage2.setId(20L);
        matchStage2.setMatch(match);
        matchStage2.setStageNumber(2);

        when(matchStageService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage1));
        when(matchStageService.findMatchStage(1L, 2)).thenReturn(Optional.of(matchStage2));

        // Act
        java.util.List<za.co.hpsc.web.models.match.MatchStageDto> result = matchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result should contain 2 stages");
        assertEquals(10L, result.get(0).getId(), "First stage ID should match");
        assertEquals(20L, result.get(1).getId(), "Second stage ID should match");
        verify(matchStageService, times(1)).findMatchStage(1L, 1);
        verify(matchStageService, times(1)).findMatchStage(1L, 2);
    }

    /**
     * Test case to verify that the {@code initStages} method returns a list of {@link za.co.hpsc.web.models.match.MatchStageDto}
     * with new match stages when stages are not found by the {@link MatchStageService}.
     */
    @Test
    public void testInitStages_withNonExistingMatchStages_thenReturnsNewMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        za.co.hpsc.web.models.ipsc.response.StageResponse stageResponse1 = new za.co.hpsc.web.models.ipsc.response.StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("New Stage 1");

        za.co.hpsc.web.models.ipsc.response.StageResponse stageResponse2 = new za.co.hpsc.web.models.ipsc.response.StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("New Stage 2");

        java.util.List<za.co.hpsc.web.models.ipsc.response.StageResponse> stageResponses = java.util.List.of(stageResponse1, stageResponse2);

        when(matchStageService.findMatchStage(1L, 1)).thenReturn(Optional.empty());
        when(matchStageService.findMatchStage(1L, 2)).thenReturn(Optional.empty());

        // Act
        java.util.List<za.co.hpsc.web.models.match.MatchStageDto> result = matchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result should contain 2 stages");
        assertNull(result.get(0).getId(), "First stage ID should be null for new stage");
        assertNull(result.get(1).getId(), "Second stage ID should be null for new stage");
        verify(matchStageService, times(1)).findMatchStage(1L, 1);
        verify(matchStageService, times(1)).findMatchStage(1L, 2);
    }
}
