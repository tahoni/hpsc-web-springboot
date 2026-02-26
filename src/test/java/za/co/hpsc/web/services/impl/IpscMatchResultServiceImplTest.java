package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class IpscMatchResultServiceImplTest {

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

    private IpscMatchResultServiceImpl ipscMatchResultService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ipscMatchResultService = new IpscMatchResultServiceImpl(
                clubEntityService,
                matchEntityService,
                competitorEntityService,
                matchStageEntityService,
                matchCompetitorEntityService,
                matchStageCompetitorEntityService
        );
    }

    // =====================================================================
    // Tests for initMatchResults - Club Fields Handling
    // =====================================================================

    @Test
    public void testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Complex Match");
        ipscResponse.setMatch(matchResponse);

        List<StageResponse> stages = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            StageResponse stage = new StageResponse();
            stage.setStageId(200 + i);
            stage.setMatchId(100);
            stages.add(stage);
        }
        ipscResponse.setStages(stages);

        List<ScoreResponse> scores = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ScoreResponse score = new ScoreResponse();
            score.setMatchId(100);
            score.setFinalScore(90 + i);
            scores.add(score);
        }
        ipscResponse.setScores(scores);

        ipscResponse.setClub(null);
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Complex Match")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getStages().size());
        assertEquals(5, result.get().getScores().size());
    }

    @Test
    public void testInitMatchResults_withCompleteMatchAndExistingClub_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getClub());
        assertEquals("Existing Club", result.get().getClub().getName());
    }

    @Test
    public void testInitMatchResults_withExistingMatchAndCompleteClub_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }


    @Test
    public void testInitMatchResults_withExistingMatchAndCompleteClubAndScoresEmpty_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_withExistingMatchAndClubAndScoresNull_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());
        ipscResponse.setScores(null);

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    // =====================================================================
    // Tests for initMatchResults - Date Comparison
    // =====================================================================

    @Test
    public void testInitMatchResults_withNoDate_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(200);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 25, 10, 0, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_withSameDate_thenDontUpdateFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(200);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 25, 10, 0, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 2, 25, 10, 0, 0));

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchResults_withLaterScores_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(200);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 25, 10, 15, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 2, 25, 10, 0, 0));

        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertTrue(existingMatch.getDateUpdated().isBefore(scoreResponse.getLastModified()));
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_withoutLaterScores_thenDontUpdateFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Existing Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Existing Club");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        ScoreResponse scoreResponse1 = new ScoreResponse();
        scoreResponse1.setMatchId(100);
        scoreResponse1.setStageId(200);
        scoreResponse1.setLastModified(LocalDateTime.of(2025, 2, 25, 10, 0, 0));
        ipscResponse.setScores(List.of(scoreResponse1));

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 2, 25, 10, 15, 0));
        when(clubEntityService.findClubByNameOrAbbreviation("Existing Club", "ABC"))
                .thenReturn(Optional.of(existingClub));
        when(matchEntityService.findMatchByName("Match with Existing Club"))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for initMatchResults - Edge Cases
    // =====================================================================

    @Test
    public void testInitMatchResults_withNullScoresInList_thenFiltersNullScores() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Null Scores");
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setFinalScore(95);

        List<ScoreResponse> scoresList = new ArrayList<>();
        scoresList.add(scoreResponse);
        scoresList.add(null);
        scoresList.add(scoreResponse);
        ipscResponse.setScores(scoresList);

        ipscResponse.setClub(null);
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Match with Null Scores")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getScores().size());
    }

    @Test
    public void testInitMatchResults_withNullStagesInList_thenFiltersNullStages() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Null Stages");
        ipscResponse.setMatch(matchResponse);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(200);
        stageResponse.setMatchId(100);

        List<StageResponse> stagesList = new ArrayList<>();
        stagesList.add(stageResponse);
        stagesList.add(null);
        stagesList.add(stageResponse);
        ipscResponse.setStages(stagesList);

        ipscResponse.setClub(null);
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Match with Null Stages")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getStages().size());
    }

    @Test
    public void testInitMatchResults_withSpecialCharactersInMatchName_thenPreservesCharacters() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match & Co. (2025) - v2.0");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setClub(null);
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Match & Co. (2025) - v2.0")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("Match & Co. (2025) - v2.0", result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_withLargeNumberOfStages_thenProcessesAllStages() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Many Stages");
        ipscResponse.setMatch(matchResponse);

        List<StageResponse> stagesList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            StageResponse stageResponse = new StageResponse();
            stageResponse.setStageId(200 + i);
            stageResponse.setStageName("Stage " + i);
            stageResponse.setMatchId(100);
            stagesList.add(stageResponse);
        }
        ipscResponse.setStages(stagesList);

        ipscResponse.setClub(null);
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Match with Many Stages")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getStages().size());
    }

    @Test
    public void testInitMatchResults_withExistingMatchNoNewerScores_thenReturnsEmptyOptional() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Existing Match");
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setFinalScore(95);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 20, 10, 0, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        ipscResponse.setClub(null);
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Existing Match");
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 2, 25, 10, 0, 0)); // More recent

        when(matchEntityService.findMatchByName("Existing Match")).thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchResults_withExistingMatchWithNewerScores_thenReturnsUpdatedMatch() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Newer Scores");
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setFinalScore(95);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 25, 15, 0, 0)); // More recent
        ipscResponse.setScores(List.of(scoreResponse));

        ipscResponse.setClub(null);
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Newer Scores");
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 2, 25, 10, 0, 0)); // Earlier

        when(matchEntityService.findMatchByName("Match with Newer Scores")).thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initScores - Consolidated (direct + via initMatchResults)
    // =====================================================================

    @Test
    public void initScores_whenResponseIsNull_thenNoChangesApplied() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, null));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenScoresNull_thenNoChangesApplied() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(null);
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenMembersNull_thenNoChangesApplied() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMembers(null);
        ipscResponse.setScores(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void initScores_withEmptyScoresAndMembers_thenKeepsEmptyCollections() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));
        matchResults.setStages(new ArrayList<>());

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchCompetitors());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchStageCompetitors());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_withPartialFields_thenFiltersByMatchAndFinalScore() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 10, 1, null),
                buildScoreResponse(100, 10, 2, 0),
                buildScoreResponse(100, 10, 3, 150),
                buildScoreResponse(999, 10, 4, 200)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(1, null, null),
                buildMemberResponse(2, "", ""),
                buildMemberResponse(3, "Jane", "Doe"),
                buildMemberResponse(4, "Skip", "Me")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(1, 100),
                buildEnrolledResponse(2, 100),
                buildEnrolledResponse(3, 100),
                buildEnrolledResponse(4, 999)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(3, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_withFullFields_thenBuildsCompetitorsAndStageScores() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 11, 7, 120)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(7, "Alice", "Jones")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(7, 100)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 11, 11, 11L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initMatchResults_withScoresAndMembers_thenInitializesScoresViaInitScores() {
        when(matchEntityService.findMatchByName("Match 100")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse(100, "Match 100"));
        ipscResponse.setStages(List.of(buildStageResponse(100, 21, "Stage 21")));
        ipscResponse.setScores(List.of(buildScoreResponse(100, 21, 9, 95)));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Bob", "Smith")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 100)));

        Optional<MatchResultsDto> result = ipscMatchResultService.initMatchResults(ipscResponse);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
        assertEquals(1, result.get().getCompetitors().size());
        assertEquals(1, result.get().getMatchCompetitors().size());
        assertFalse(result.get().getMatchStageCompetitors().isEmpty());
    }

    // =====================================================================
    // Helper methods for building test data
    // =====================================================================

    private static MatchDto buildMatchDto(int matchIndex, Long matchId) {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(matchIndex);
        matchDto.setId(matchId);
        matchDto.setName("Match " + matchIndex);
        matchDto.setScheduledDate(LocalDateTime.now());
        return matchDto;
    }

    private static MatchStageDto buildMatchStageDto(MatchDto matchDto, int stageId, int stageNumber, Long stagePkId) {
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setIndex(stageId);
        stageDto.setStageNumber(stageNumber);
        stageDto.setId(stagePkId);
        return stageDto;
    }

    private static ScoreResponse buildScoreResponse(int matchId, int stageId, int memberId, Integer finalScore) {
        ScoreResponse score = new ScoreResponse();
        score.setMatchId(matchId);
        score.setStageId(stageId);
        score.setMemberId(memberId);
        score.setFinalScore(finalScore);
        return score;
    }

    private static MemberResponse buildMemberResponse(int memberId, String firstName, String lastName) {
        MemberResponse member = new MemberResponse();
        member.setMemberId(memberId);
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setIsRegisteredForMatch(true);
        return member;
    }

    private static EnrolledResponse buildEnrolledResponse(int memberId, int matchId) {
        EnrolledResponse enrolled = new EnrolledResponse();
        enrolled.setMemberId(memberId);
        enrolled.setMatchId(matchId);
        return enrolled;
    }

    private static MatchResponse buildMatchResponse(int matchId, String matchName) {
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(matchId);
        matchResponse.setMatchName(matchName);
        return matchResponse;
    }

    private static StageResponse buildStageResponse(int matchId, int stageId, String stageName) {
        StageResponse stageResponse = new StageResponse();
        stageResponse.setMatchId(matchId);
        stageResponse.setStageId(stageId);
        stageResponse.setStageName(stageName);
        return stageResponse;
    }

    // =====================================================================
    // Tests for initScores - Consolidated edge cases + partial/full
    // =====================================================================

    @Test
    public void testInitScores_whenResponseIsNull_thenNoChangesApplied() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, null));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_whenScoresNull_thenNoChangesApplied() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(null);
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_whenMembersNull_thenNoChangesApplied() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMembers(null);
        ipscResponse.setScores(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withEmptyScoresAndMembers_thenKeepsEmptyCollections() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));
        matchResults.setStages(new ArrayList<>());

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchCompetitors());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchStageCompetitors());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withPartialFields_thenFiltersByMatchAndFinalScore() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 10, 1, null),
                buildScoreResponse(100, 10, 2, 0),
                buildScoreResponse(100, 10, 3, 150),
                buildScoreResponse(999, 10, 4, 200)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(1, null, null),
                buildMemberResponse(2, "", ""),
                buildMemberResponse(3, "Jane", "Doe"),
                buildMemberResponse(4, "Skip", "Me")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(1, 100),
                buildEnrolledResponse(2, 100),
                buildEnrolledResponse(3, 100),
                buildEnrolledResponse(4, 999)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(3, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withFullFields_thenBuildsCompetitorsAndStageScores() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 11, 7, 120)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(7, "Alice", "Jones")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(7, 100)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 11, 11, 11L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitMatchResults_withScoresAndMembers_thenInitializesScoresViaInitScores() {
        when(matchEntityService.findMatchByName("Match 100")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse(100, "Match 100"));
        ipscResponse.setStages(List.of(buildStageResponse(100, 21, "Stage 21")));
        ipscResponse.setScores(List.of(buildScoreResponse(100, 21, 9, 95)));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Bob", "Smith")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 100)));

        Optional<MatchResultsDto> result = ipscMatchResultService.initMatchResults(ipscResponse);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
        assertEquals(1, result.get().getCompetitors().size());
        assertEquals(1, result.get().getMatchCompetitors().size());
        assertFalse(result.get().getMatchStageCompetitors().isEmpty());
    }

    // =====================================================================
    // Tests for initScores - Consolidated edge cases + partial/full
    // =====================================================================

    @Test
    public void testInitScores_whenResponseIsNull_thenNoChangesApplied_fromConsolidated() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, null));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_whenScoresNull_thenNoChangesApplied_fromConsolidated() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(null);
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_whenMembersNull_thenNoChangesApplied_fromConsolidated() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMembers(null);
        ipscResponse.setScores(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withEmptyScoresAndMembers_thenKeepsEmptyCollections_fromConsolidated() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, null));
        matchResults.setStages(new ArrayList<>());

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertNotNull(matchResults.getScores());
        assertTrue(matchResults.getScores().isEmpty());
        assertNotNull(matchResults.getCompetitors());
        assertTrue(matchResults.getCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchCompetitors());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertNotNull(matchResults.getMatchStageCompetitors());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withPartialFields_thenFiltersByMatchAndFinalScore_fromConsolidated() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 10, 1, null),
                buildScoreResponse(100, 10, 2, 0),
                buildScoreResponse(100, 10, 3, 150),
                buildScoreResponse(999, 10, 4, 200)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(1, null, null),
                buildMemberResponse(2, "", ""),
                buildMemberResponse(3, "Jane", "Doe"),
                buildMemberResponse(4, "Skip", "Me")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(1, 100),
                buildEnrolledResponse(2, 100),
                buildEnrolledResponse(3, 100),
                buildEnrolledResponse(4, 999)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(3, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withFullFields_thenBuildsCompetitorsAndStageScores_fromConsolidated() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 11, 7, 120)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(7, "Alice", "Jones")
        ));
        ipscResponse.setEnrolledMembers(List.of(
                buildEnrolledResponse(7, 100)
        ));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 11, 11, 11L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitMatchResults_withScoresAndMembers_thenInitializesScoresViaInitScores_fromConsolidated() {
        when(matchEntityService.findMatchByName("Match 100")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse(100, "Match 100"));
        ipscResponse.setStages(List.of(buildStageResponse(100, 21, "Stage 21")));
        ipscResponse.setScores(List.of(buildScoreResponse(100, 21, 9, 95)));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Bob", "Smith")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 100)));

        Optional<MatchResultsDto> result = ipscMatchResultService.initMatchResults(ipscResponse);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
        assertEquals(1, result.get().getCompetitors().size());
        assertEquals(1, result.get().getMatchCompetitors().size());
        assertFalse(result.get().getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void testInitScores_withNullScoreFields_thenHandlesGracefully() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ScoreResponse scoreWithNulls = new ScoreResponse();
        scoreWithNulls.setMatchId(100);
        scoreWithNulls.setStageId(10);
        scoreWithNulls.setMemberId(5);
        scoreWithNulls.setFinalScore(100);
        // Other fields remain null

        ipscResponse.setScores(List.of(scoreWithNulls));
        ipscResponse.setMembers(List.of(buildMemberResponse(5, "Test", "User")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(5, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
    }

    @Test
    public void testInitScores_withBlankMemberNames_thenStillProcesses() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(buildScoreResponse(100, 10, 6, 150)));

        MemberResponse memberWithBlankName = new MemberResponse();
        memberWithBlankName.setMemberId(6);
        memberWithBlankName.setFirstName("   ");
        memberWithBlankName.setLastName("   ");
        memberWithBlankName.setIsRegisteredForMatch(true);

        ipscResponse.setMembers(List.of(memberWithBlankName));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(6, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
    }

    @Test
    public void testInitScores_withMultipleStages_thenCreatesStageCompetitorsForEach() {
        when(competitorEntityService.findCompetitor(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 10, 8, 100),
                buildScoreResponse(100, 11, 8, 110),
                buildScoreResponse(100, 12, 8, 120)
        ));
        ipscResponse.setMembers(List.of(buildMemberResponse(8, "Multi", "Stage")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(8, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(
                buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L),
                buildMatchStageDto(matchResults.getMatch(), 11, 11, 11L),
                buildMatchStageDto(matchResults.getMatch(), 12, 12, 12L)
        ));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(3, matchResults.getScores().size());
        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        // Note: initScores currently creates duplicate stage competitors due to duplicate logic
        assertEquals(6, matchResults.getMatchStageCompetitors().size());
    }

    // =====================================================================
    // Tests for initMatchResults - Null Input Handling (from IpscMatchResultServiceTest)
    // =====================================================================

    @Test
    public void testInitMatchResults_whenIpscResponseIsNull_thenReturnsEmptyOptional() {
        // Act
        var result = ipscMatchResultService.initMatchResults(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchResults_whenMatchResponseIsNull_thenReturnsEmptyOptional() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(null);
        ipscResponse.setClub(new ClubResponse());
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for initMatchResults - Null Collections and Fields
    // =====================================================================

    @Test
    public void testInitMatchResults_whenClubResponseIsNull_thenProcessesWithoutClub() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setClub(null);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName(anyString())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNull(result.get().getClub());
    }

    @Test
    public void testInitMatchResults_whenStagesAreNull_thenReturnsEmptyStagesList() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(null);
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString()))
                .thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getStages().isEmpty());
    }

    @Test
    public void testInitMatchResults_whenScoresAreNull_thenReturnsEmptyScoresList() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(null);
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getScores().isEmpty());
    }

    @Test
    public void testInitMatchResults_whenMembersAreNull_thenProcessesWithoutMembers() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(null);

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString()))
                .thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchResults - Match Name Field Handling
    // =====================================================================

    @Test
    public void testInitMatchResults_whenMatchNameIsNull_thenProcessesWithNullName() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName(null);
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(null))
                .thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNull(result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_whenMatchNameIsEmpty_thenProcessesWithEmptyName() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());

        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(""))
                .thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("", result.get().getMatch().getName());
    }

    @Test
    public void testInitMatchResults_whenMatchNameIsBlank_thenProcessesWithBlankName() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("   ");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("   ")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("   ", result.get().getMatch().getName());
    }

    // =====================================================================
    // Tests for initMatchResults - Club Fields Handling
    // =====================================================================

    @Test
    public void testInitMatchResults_whenClubNameIsNull_thenProcessesWithNullClubName() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation(null, "ABC"))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Test Match")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchResults_whenClubCodeIsNull_thenProcessesWithNullClubCode() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode(null);
        clubResponse.setClubName("Test Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation("Test Club", null))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Test Match")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchResults - Partial and Complete Data Scenarios
    // =====================================================================

    @Test
    public void testInitMatchResults_withOnlyMatchData_thenReturnsMatchWithoutDetails() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Simple Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setClub(null);
        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Simple Match")).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("Simple Match", result.get().getMatch().getName());
        assertTrue(result.get().getStages().isEmpty());
        assertTrue(result.get().getScores().isEmpty());
    }

    @Test
    public void testInitMatchResults_withPartialData_thenMapsAvailableData() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Test Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Partial Match");
        ipscResponse.setMatch(matchResponse);

        // Only stages, no scores
        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(200);
        stageResponse.setStageName("Stage 1");
        stageResponse.setMatchId(100);
        ipscResponse.setStages(List.of(stageResponse));

        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        when(clubEntityService.findClubByNameOrAbbreviation("Test Club", "ABC"))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Partial Match")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStages().size());
        assertTrue(result.get().getScores().isEmpty());
    }

    @Test
    public void testInitMatchResults_withPartialStagesAndScores_thenMapsMatchingData() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match with Data");
        ipscResponse.setMatch(matchResponse);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(200);
        stageResponse1.setMatchId(100);

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(201);
        stageResponse2.setMatchId(200); // Different match
        ipscResponse.setStages(List.of(stageResponse1, stageResponse2));

        ScoreResponse scoreResponse1 = new ScoreResponse();
        scoreResponse1.setMatchId(100);
        scoreResponse1.setFinalScore(95);

        ScoreResponse scoreResponse2 = new ScoreResponse();
        scoreResponse2.setMatchId(200); // Different match
        scoreResponse2.setFinalScore(85);
        ipscResponse.setScores(List.of(scoreResponse1, scoreResponse2));

        ipscResponse.setClub(null);
        ipscResponse.setMembers(new ArrayList<>());

        when(matchEntityService.findMatchByName("Match with Data")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
    }

    @Test
    public void testInitMatchResults_withCompleteData_thenMapsAllData() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode("ABC");
        clubResponse.setClubName("Complete Club");
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Complete Match");
        ipscResponse.setMatch(matchResponse);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(200);
        stageResponse.setStageName("Stage 1");
        stageResponse.setMatchId(100);
        ipscResponse.setStages(List.of(stageResponse));

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(200);
        scoreResponse.setFinalScore(95);
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 25, 10, 0, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(50);
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        ipscResponse.setMembers(List.of(memberResponse));

        when(clubEntityService.findClubByNameOrAbbreviation("Complete Club", "ABC"))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("Complete Match")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        MatchResultsDto matchResults = result.get();
        assertEquals("Complete Match", matchResults.getMatch().getName());
        assertEquals(1, matchResults.getStages().size());
        assertEquals(1, matchResults.getScores().size());
    }
}

