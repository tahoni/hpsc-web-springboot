package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    // Tests for initMatchResults - Null Input Handling
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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString())).thenReturn(Optional.empty());

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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString())).thenReturn(Optional.empty());

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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(null)).thenReturn(Optional.empty());

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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName("")).thenReturn(Optional.empty());

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

        when(clubEntityService.findClubByNameOrAbbreviation(anyString(), anyString()))
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
}

