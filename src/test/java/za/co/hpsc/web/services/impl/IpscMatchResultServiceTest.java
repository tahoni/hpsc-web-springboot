package za.co.hpsc.web.services.impl;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class IpscMatchResultServiceTest {

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
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        ipscMatchResultService = new IpscMatchResultServiceImpl(
                clubEntityService,
                matchEntityService,
                competitorEntityService,
                matchStageEntityService,
                matchCompetitorEntityService,
                matchStageCompetitorEntityService
        );
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
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
    }

    @Test
    public void testInitMatchResults_withCompleteMatchAndExistingClub_thenUpdatesFromDatabase() {
        // Arrange
        IpscResponse ipscResponse = getIpscResponse("ABC", "Existing Club", "Match with Existing Club");

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
        IpscResponse ipscResponse = getIpscResponse("ABC", "Existing Club", "Match with Existing Club");

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
        IpscResponse ipscResponse = getIpscResponse(new ArrayList<>());

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
        IpscResponse ipscResponse = getIpscResponse(null);

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
    // Tests for initStages
    // =====================================================================

    @Test
    public void initStages_whenMatchOrStagesAreNull_thenReturnsEmptyList() {
        assertTrue(ipscMatchResultService.initStages(null, List.of(new StageResponse())).isEmpty());
        assertTrue(ipscMatchResultService.initStages(buildMatchDto(100, 1L), null).isEmpty());
    }

    @Test
    public void initStages_whenStageResponsesContainNullEntries_thenMapsOnlyValidStages() {
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());

        MatchDto matchDto = buildMatchDto(100, 1L);
        StageResponse stageResponse = new StageResponse();
        stageResponse.setMatchId(100);
        stageResponse.setStageId(21);
        stageResponse.setStageName("Stage 21");
        stageResponse.setMaxPoints(120);

        List<StageResponse> stageResponses = new ArrayList<>();
        stageResponses.add(stageResponse);
        stageResponses.add(null);

        List<MatchStageDto> stages = ipscMatchResultService.initStages(matchDto, stageResponses);

        assertEquals(1, stages.size());
        assertEquals(21, stages.getFirst().getIndex());
        assertEquals(21, stages.getFirst().getStageNumber());
        assertEquals("Stage 21", stages.getFirst().getStageName());
        assertEquals(120, stages.getFirst().getMaxPoints());
        assertSame(matchDto, stages.getFirst().getMatch());
    }

    // =====================================================================
    // Tests for initCompetitors
    // =====================================================================

    @Test
    public void initCompetitors_whenResponseOrMatchIsMissing_thenReturnsEmptyList() {
        MatchResultsDto matchResultsWithoutMatch = new MatchResultsDto(null);
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        assertTrue(ipscMatchResultService.initCompetitors(matchResults, null).isEmpty());
        assertTrue(ipscMatchResultService.initCompetitors(matchResultsWithoutMatch, new IpscResponse()).isEmpty());
    }

    @Test
    public void initCompetitors_whenScoresContainDifferentMatchesOrNonPositiveValues_thenReturnsOnlyScoredCompetitorsForCurrentMatch() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 21, 1, null),
                buildScoreResponse(100, 21, 2, 0),
                buildScoreResponse(100, 21, 3, 95),
                buildScoreResponse(999, 21, 4, 80),
                buildScoreResponse(100, 21, 5, 70)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(1, "Ignored", "Null"),
                buildMemberResponse(2, "Ignored", "Zero"),
                buildMemberResponse(3, "Jane", "Doe"),
                buildMemberResponse(4, "Skip", "Me")
        ));

        List<CompetitorDto> competitors = ipscMatchResultService.initCompetitors(
                new MatchResultsDto(buildMatchDto(100, 1L)),
                ipscResponse
        );

        assertEquals(1, competitors.size());
        assertEquals(3, competitors.getFirst().getIndex());
        assertEquals("Jane", competitors.getFirst().getFirstName());
        assertEquals("Doe", competitors.getFirst().getLastName());
    }

    @Test
    public void initCompetitors_whenScoresOrMembersAreNull_thenReturnsEmptyList() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        IpscResponse nullScoresResponse = new IpscResponse();
        nullScoresResponse.setMatch(buildMatchResponse());
        nullScoresResponse.setScores(null);
        nullScoresResponse.setMembers(List.of(buildMemberResponse(1, "Jane", "Doe")));

        IpscResponse nullMembersResponse = new IpscResponse();
        nullMembersResponse.setMatch(buildMatchResponse());
        nullMembersResponse.setScores(List.of(buildScoreResponse(100, 21, 1, 80)));
        nullMembersResponse.setMembers(null);

        assertTrue(ipscMatchResultService.initCompetitors(matchResults, nullScoresResponse).isEmpty());
        assertTrue(ipscMatchResultService.initCompetitors(matchResults, nullMembersResponse).isEmpty());
    }

    @Test
    public void initCompetitors_whenScoresAndMembersAreEmpty_thenReturnsEmptyList() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        List<CompetitorDto> competitors = ipscMatchResultService.initCompetitors(matchResults, ipscResponse);

        assertNotNull(competitors);
        assertTrue(competitors.isEmpty());
    }

    @Test
    public void initCompetitors_whenMemberHasMultipleValidScores_thenReturnsCompetitorOnce() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 21, 9, 120),
                buildScoreResponse(100, 22, 9, 130)
        ));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Sam", "Shooter")));

        List<CompetitorDto> competitors = ipscMatchResultService.initCompetitors(matchResults, ipscResponse);

        assertEquals(1, competitors.size());
        assertEquals(9, competitors.getFirst().getIndex());
        assertEquals("Sam", competitors.getFirst().getFirstName());
        assertEquals("Shooter", competitors.getFirst().getLastName());
    }

    @Test
    public void initCompetitors_whenMatchResponseIsNullWithPopulatedScoresAndMembers_thenThrowsNullPointerException() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(null);
        ipscResponse.setScores(List.of(buildScoreResponse(100, 21, 1, 90)));
        ipscResponse.setMembers(List.of(buildMemberResponse(1, "Jane", "Doe")));

        assertThrows(NullPointerException.class,
                () -> ipscMatchResultService.initCompetitors(matchResults, ipscResponse));
    }

    // =====================================================================
    // Tests for initScores
    // =====================================================================

    @Test
    public void initScores_whenResponseIsNull_thenLeavesCollectionsEmpty() {
        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, null));

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenScoresAndMembersAreEmpty_thenKeepsCollectionsEmpty() {
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(new ArrayList<>());

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenScoreFieldsArePartial_thenBuildsDtosFromAvailableValues() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(
                buildScoreResponse(100, 10, 1, null),
                buildScoreResponse(100, 10, 2, 0),
                buildScoreResponse(100, 10, 3, 150),
                buildScoreResponse(999, 10, 4, 200)
        ));
        ipscResponse.setMembers(List.of(
                buildMemberResponse(1, "Ignored", "Null"),
                buildMemberResponse(2, "Ignored", "Zero"),
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
        MatchStageDto stageDto = buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L);
        stageDto.setMaxPoints(200);
        matchResults.setStages(List.of(stageDto));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals("Jane", matchResults.getCompetitors().getFirst().getFirstName());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertEquals(150, matchResults.getMatchCompetitors().getFirst().getMatchPoints().intValue());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        assertEquals(150, matchResults.getMatchStageCompetitors().getFirst().getPoints());
        assertEquals(150, matchResults.getMatchStageCompetitors().getFirst().getStagePoints().intValue());
    }

    @Test
    public void initScores_whenScoreFieldsAreNull_thenUsesZeroValuesWhereApplicable() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(5);
        scoreResponse.setFinalScore(100);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(buildMemberResponse(5, "Test", "User")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(5, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        MatchStageDto stageDto = buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L);
        stageDto.setMaxPoints(150);
        matchResults.setStages(List.of(stageDto));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getTime().compareTo(BigDecimal.ZERO));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getHitFactor().compareTo(BigDecimal.ZERO));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getDeductionPercentage().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void initScores_whenFieldsAreComplete_thenBuildsMatchAndStageScores() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        LocalDateTime lastModified = LocalDateTime.of(2025, 2, 25, 10, 15, 0);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(21);
        scoreResponse.setMemberId(9);
        scoreResponse.setScoreA(10);
        scoreResponse.setScoreB(8);
        scoreResponse.setScoreC(2);
        scoreResponse.setScoreD(0);
        scoreResponse.setMisses(1);
        scoreResponse.setPenalties(2);
        scoreResponse.setProcedurals(3);
        scoreResponse.setTime("12.50");
        scoreResponse.setDeduction(true);
        scoreResponse.setDeductionPercentage("5.5");
        scoreResponse.setHitFactor("6.40");
        scoreResponse.setFinalScore(95);
        scoreResponse.setLastModified(lastModified);

        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(9);
        memberResponse.setFirstName("Alice");
        memberResponse.setLastName("Jones");
        memberResponse.setDateOfBirth(LocalDateTime.of(1990, 1, 15, 0, 0));
        memberResponse.setIcsAlias("1500");
        memberResponse.setIsRegisteredForMatch(true);

        EnrolledResponse enrolledResponse = buildEnrolledResponse(9, 100);
        enrolledResponse.setRefNo("BBB");

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(memberResponse));
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        MatchStageDto stageDto = buildMatchStageDto(matchResults.getMatch(), 21, 21, 21L);
        stageDto.setMaxPoints(100);
        matchResults.setStages(List.of(stageDto));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals("Alice", matchResults.getCompetitors().getFirst().getFirstName());
        assertEquals("Jones", matchResults.getCompetitors().getFirst().getLastName());
        assertEquals("1990-01-15", matchResults.getCompetitors().getFirst().getDateOfBirth().toString());
        assertEquals("1500", matchResults.getCompetitors().getFirst().getCompetitorNumber());
        assertEquals(1500, matchResults.getCompetitors().getFirst().getSapsaNumber());
        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertEquals(95, matchResults.getMatchCompetitors().getFirst().getMatchPoints().intValue());
        assertEquals(lastModified, matchResults.getMatchCompetitors().getFirst().getDateEdited());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        assertEquals(95, matchResults.getMatchStageCompetitors().getFirst().getPoints());
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getTime().compareTo(new BigDecimal("12.50")));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getHitFactor().compareTo(new BigDecimal("6.40")));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getStagePercentage().compareTo(new BigDecimal("95")));
        assertEquals(lastModified, matchResults.getMatchStageCompetitors().getFirst().getDateEdited());
    }

    @Test
    public void initScores_whenMemberNamesAreBlank_thenCreatesCompetitorWithTrimmedBlankNames() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(6);
        memberResponse.setFirstName("   ");
        memberResponse.setLastName("   ");
        memberResponse.setIsRegisteredForMatch(true);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(buildScoreResponse(100, 10, 6, 150)));
        ipscResponse.setMembers(List.of(memberResponse));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(6, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        assertDoesNotThrow(() -> ipscMatchResultService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals("", matchResults.getCompetitors().getFirst().getFirstName());
        assertEquals("", matchResults.getCompetitors().getFirst().getLastName());
    }

    @Test
    public void initScores_whenMemberHasMultipleStageScores_thenAggregatesMatchPointsAndUsesLatestEditDate() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        LocalDateTime earlier = LocalDateTime.of(2025, 2, 25, 10, 0, 0);
        LocalDateTime later = LocalDateTime.of(2025, 2, 25, 10, 30, 0);

        ScoreResponse stageOneScore = new ScoreResponse();
        stageOneScore.setMatchId(100);
        stageOneScore.setStageId(10);
        stageOneScore.setMemberId(9);
        stageOneScore.setFinalScore(100);
        stageOneScore.setLastModified(earlier);

        ScoreResponse stageTwoScore = new ScoreResponse();
        stageTwoScore.setMatchId(100);
        stageTwoScore.setStageId(11);
        stageTwoScore.setMemberId(9);
        stageTwoScore.setFinalScore(80);
        stageTwoScore.setLastModified(later);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(stageOneScore, stageTwoScore));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Sam", "Shooter")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(
                buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L),
                buildMatchStageDto(matchResults.getMatch(), 11, 11, 11L)
        ));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getMatchCompetitors().size());
        assertEquals(180, matchResults.getMatchCompetitors().getFirst().getMatchPoints().intValue());
        assertEquals(later, matchResults.getMatchCompetitors().getFirst().getDateEdited());
        assertTrue(matchResults.getMatchStageCompetitors().stream()
                .anyMatch(sc -> sc.getMatchStageIndex().equals(10) && Integer.valueOf(100).equals(sc.getPoints())));
        assertTrue(matchResults.getMatchStageCompetitors().stream()
                .anyMatch(sc -> sc.getMatchStageIndex().equals(11) && Integer.valueOf(80).equals(sc.getPoints())));
    }

    @Test
    public void initScores_whenScoresBelongToDifferentMatch_thenLeavesScoreCollectionsEmpty() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(buildScoreResponse(999, 10, 9, 100)));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Sam", "Shooter")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 999)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        matchResults.setStages(List.of(buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L)));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenNumericScoreFieldsAreInvalid_thenDefaultsNumericOutputsToZero() {
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(5);
        scoreResponse.setFinalScore(50);
        scoreResponse.setTime("bad-time");
        scoreResponse.setHitFactor("bad-hf");
        scoreResponse.setDeductionPercentage("bad-deduction");

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(buildMemberResponse(5, "Casey", "Edge")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(5, 100)));

        MatchResultsDto matchResults = new MatchResultsDto(buildMatchDto(100, 1L));
        MatchStageDto stageDto = buildMatchStageDto(matchResults.getMatch(), 10, 10, 10L);
        stageDto.setMaxPoints(100);
        matchResults.setStages(List.of(stageDto));

        ipscMatchResultService.initScores(matchResults, ipscResponse);

        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        MatchStageCompetitorDto competitorDto = matchResults.getMatchStageCompetitors().getFirst();
        assertEquals(0, competitorDto.getTime().compareTo(BigDecimal.ZERO));
        assertEquals(0, competitorDto.getHitFactor().compareTo(BigDecimal.ZERO));
        assertEquals(0, competitorDto.getDeductionPercentage().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void initMatchResults_whenScoresMembersAndEnrollmentProvided_thenBuildsCompetitorAndScoreCollections() {
        when(matchEntityService.findMatchByName("Match 100")).thenReturn(Optional.empty());
        when(matchStageEntityService.findMatchStage(any(), any())).thenReturn(Optional.empty());
        when(matchCompetitorEntityService.findMatchCompetitor(any(), any())).thenReturn(Optional.empty());
        when(matchStageCompetitorEntityService.findMatchStageCompetitor(any(), any())).thenReturn(Optional.empty());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(21);
        scoreResponse.setMemberId(9);
        scoreResponse.setFinalScore(95);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(buildMatchResponse());
        ipscResponse.setStages(List.of(buildStageResponse()));
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(buildMemberResponse(9, "Bob", "Smith")));
        ipscResponse.setEnrolledMembers(List.of(buildEnrolledResponse(9, 100)));

        Optional<MatchResultsDto> result = ipscMatchResultService.initMatchResults(ipscResponse);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCompetitors().size());
        assertEquals(1, result.get().getMatchCompetitors().size());
        assertFalse(result.get().getMatchStageCompetitors().isEmpty());
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
        IpscResponse ipscResponse = getIpscResponse();

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
        IpscResponse ipscResponse = getResponse();

        when(clubEntityService.findClubByNameOrAbbreviation(isNull(), anyString()))
                .thenReturn(Optional.empty());
        when(matchEntityService.findMatchByName(anyString())).thenReturn(Optional.empty());

        // Act
        var result = ipscMatchResultService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchResults_whenMembersAreNull_thenProcessesWithoutMembers() {
        // Arrange
        IpscResponse ipscResponse = getIpscResponse1();

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
        IpscResponse ipscResponse = getIpscResponse2(null, new ArrayList<>());

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
        IpscResponse ipscResponse = getIpscResponse2("", new ArrayList<>());

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
        IpscResponse ipscResponse = getIpscResponse2("   ", new ArrayList<>());

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
        IpscResponse ipscResponse = getIpscResponse("ABC", null, "Test Match");

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
        IpscResponse ipscResponse = getIpscResponse(null, "Test Club", "Test Match");

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
    }

    @Test
    public void testInitMatchResults_withPartialData_thenMapsAvailableData() {
        // Arrange
        IpscResponse ipscResponse = getIpscResponse2();

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
    }


    // =====================================================================
    // Helper Methods
    // =====================================================================

    private static @NonNull IpscResponse getIpscResponse(String ABC, String Existing_Club, String Match_with_Existing_Club) {
        return buildIpscResponse(ABC, Existing_Club, Match_with_Existing_Club, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private static @NonNull IpscResponse getIpscResponse(ArrayList<ScoreResponse> scores) {
        return buildIpscResponse("ABC", "Existing Club", "Match with Existing Club", new ArrayList<>(), scores, new ArrayList<>());
    }

    private static @NonNull IpscResponse getIpscResponse() {
        return getIpscResponse2("Test Match", null);
    }

    private static @NonNull IpscResponse getResponse() {
        return getIpscResponse2("Test Match", new ArrayList<>());
    }

    private static @NonNull IpscResponse getIpscResponse1() {
        return getIpscResponse2("Test Match", new ArrayList<>());
    }

    private static @NonNull IpscResponse getIpscResponse2(String matchName, ArrayList<StageResponse> stages) {
        return buildIpscResponse("ABC", null, matchName, stages, null, null);
    }

    private static @NonNull IpscResponse getIpscResponse2() {
        // Only stages, no scores
        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(200);
        stageResponse.setStageName("Stage 1");
        stageResponse.setMatchId(100);
        return buildIpscResponse("ABC", "Test Club", "Partial Match", List.of(stageResponse), new ArrayList<>(), new ArrayList<>());
    }

    private static @NonNull IpscResponse buildIpscResponse(
            String clubCode,
            String clubName,
            String matchName,
            List<StageResponse> stages,
            List<ScoreResponse> scores,
            List<MemberResponse> members
    ) {
        IpscResponse ipscResponse = new IpscResponse();

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubCode(clubCode);
        clubResponse.setClubName(clubName);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName(matchName);
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(stages);
        ipscResponse.setScores(scores);
        ipscResponse.setMembers(members);
        return ipscResponse;
    }

    private static MatchDto buildMatchDto(Long matchId) {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(matchId);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        return matchDto;
    }

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
        enrolled.setCompetitorId(memberId);
        enrolled.setMatchId(matchId);
        return enrolled;
    }

    private static MatchResponse buildMatchResponse() {
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        return matchResponse;
    }

    private static StageResponse buildStageResponse() {
        StageResponse stageResponse = new StageResponse();
        stageResponse.setMatchId(100);
        stageResponse.setStageId(21);
        stageResponse.setStageName("Stage 21");
        return stageResponse;
    }

}



