package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceTest {
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

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchService;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        ipscMatchService = new IpscMatchServiceImpl(
                clubEntityService,
                matchEntityService,
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
    // Tests for createBasicMatch - Valid Data and Filtering
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenValidData_thenReturnsIpscResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setMatchDate(LocalDateTime.of(2025, 9, 6, 0, 0, 0));

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(2);
        tagRequest.setTagName("CRO");
        TagRequest tagRequest1 = new TagRequest();
        tagRequest1.setTagId(1);
        tagRequest1.setTagName("RO");
        List<TagRequest> tags = List.of(
                tagRequest1,
                tagRequest
        );
        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(3);
        stageRequest.setMatchId(200);
        stageRequest.setStageName("Stage 3");
        StageRequest stageRequest1 = new StageRequest();
        stageRequest1.setStageId(2);
        stageRequest1.setMatchId(100);
        stageRequest1.setStageName("Stage 2");
        StageRequest stageRequest2 = new StageRequest();
        stageRequest2.setStageId(1);
        stageRequest2.setMatchId(100);
        stageRequest2.setStageName("Stage 1");
        List<StageRequest> stages = List.of(
                stageRequest2,
                stageRequest1,
                stageRequest
        );
        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(52);
        enrolledRequest.setMatchId(200);
        EnrolledRequest enrolledRequest1 = new EnrolledRequest();
        enrolledRequest1.setMemberId(51);
        enrolledRequest1.setMatchId(100);
        enrolledRequest1.setCompetitorId(501);
        EnrolledRequest enrolledRequest2 = new EnrolledRequest();
        enrolledRequest2.setMemberId(50);
        enrolledRequest2.setMatchId(100);
        enrolledRequest2.setCompetitorId(500);
        List<EnrolledRequest> enrolledMembers = List.of(
                enrolledRequest2,
                enrolledRequest1,
                enrolledRequest
        );
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(200);
        scoreRequest.setStageId(3);
        scoreRequest.setMemberId(52);
        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setStageId(2);
        scoreRequest1.setMemberId(51);
        scoreRequest1.setFinalScore(95);
        ScoreRequest scoreRequest2 = new ScoreRequest();
        scoreRequest2.setMatchId(100);
        scoreRequest2.setStageId(1);
        scoreRequest2.setMemberId(50);
        scoreRequest2.setFinalScore(100);
        List<ScoreRequest> scores = List.of(
                scoreRequest2,
                scoreRequest1,
                scoreRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(tags);
        ipscRequestHolder.setStages(stages);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(enrolledMembers);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals(100, result.getMatch().getMatchId());
        assertEquals("Test Match", result.getMatch().getMatchName());

        // Assert all tags are included
        assertEquals(2, result.getTags().size());
        TagResponse tagResponse1 = result.getTags().stream()
                .filter(tag -> tag.getTagId().equals(1)).findFirst().orElse(null);
        TagResponse tagResponse2 = result.getTags().stream()
                .filter(tag -> tag.getTagId().equals(2)).findFirst().orElse(null);
        assertNotNull(tagResponse1);
        assertNotNull(tagResponse2);
        assertEquals(1, tagResponse1.getTagId());
        assertEquals("RO", tagResponse1.getTagName());
        assertEquals(2, tagResponse2.getTagId());
        assertEquals("CRO", tagResponse2.getTagName());

        // Assert that only stages for match 100 are included
        assertEquals(2, result.getStages().size());
        assertTrue(result.getStages().stream().allMatch(stage -> stage.getMatchId().equals(100)));
        StageResponse stageResponse1 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(1)).findFirst().orElse(null);
        StageResponse stageResponse2 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(2)).findFirst().orElse(null);
        StageResponse stageResponse3 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(3)).findFirst().orElse(null);
        assertNotNull(stageResponse1);
        assertNotNull(stageResponse2);
        assertNull(stageResponse3);
        assertEquals(1, stageResponse1.getStageId());
        assertEquals("Stage 1", stageResponse1.getStageName());
        assertEquals(100, stageResponse1.getMatchId());
        assertEquals(2, stageResponse2.getStageId());
        assertEquals("Stage 2", stageResponse2.getStageName());
        assertEquals(100, stageResponse2.getMatchId());

        // Assert only enrolled members for match 100 are included
        assertEquals(2, result.getEnrolledMembers().size());
        assertTrue(result.getEnrolledMembers().stream()
                .allMatch(enrolled -> enrolled.getMatchId().equals(100)));
        EnrolledResponse enrolledResponse1 = result.getEnrolledMembers().stream()
                .filter(enrolled -> enrolled.getMemberId().equals(50)).findFirst().orElse(null);
        EnrolledResponse enrolledResponse2 = result.getEnrolledMembers().stream()
                .filter(enrolled -> enrolled.getMemberId().equals(51)).findFirst().orElse(null);
        EnrolledResponse enrolledResponse3 = result.getEnrolledMembers().stream()
                .filter(enrolled -> enrolled.getMemberId().equals(52)).findFirst().orElse(null);
        assertNotNull(enrolledResponse1);
        assertNotNull(enrolledResponse2);
        assertNull(enrolledResponse3);
        assertEquals(50, enrolledResponse1.getMemberId());
        assertEquals(100, enrolledResponse1.getMatchId());
        assertEquals(500, enrolledResponse1.getCompetitorId());
        assertEquals(51, enrolledResponse2.getMemberId());
        assertEquals(100, enrolledResponse2.getMatchId());
        assertEquals(501, enrolledResponse2.getCompetitorId());

        // Assert that only scores for match 100 are included
        assertEquals(2, result.getScores().size());
        assertTrue(result.getScores().stream().allMatch(score -> score.getMatchId().equals(100)));
        ScoreResponse scoreResponse1 = result.getScores().stream()
                .filter(score -> score.getMemberId().equals(50)).findFirst().orElse(null);
        ScoreResponse scoreResponse2 = result.getScores().stream()
                .filter(score -> score.getMemberId().equals(51)).findFirst().orElse(null);
        ScoreResponse scoreResponse3 = result.getScores().stream()
                .filter(score -> score.getMemberId().equals(52)).findFirst().orElse(null);
        assertNotNull(scoreResponse1);
        assertNotNull(scoreResponse2);
        assertNull(scoreResponse3);
        assertEquals(100, scoreResponse1.getMatchId());
        assertEquals(1, scoreResponse1.getStageId());
        assertEquals(50, scoreResponse1.getMemberId());
        assertEquals(100, scoreResponse1.getFinalScore());
        assertEquals(100, scoreResponse2.getMatchId());
        assertEquals(100, scoreResponse2.getMatchId());
        assertEquals(2, scoreResponse2.getStageId());
        assertEquals(51, scoreResponse2.getMemberId());
        assertEquals(95, scoreResponse2.getFinalScore());
    }

    @Test
    public void testCreateBasicMatch_whenNoMatchingStages_thenReturnsEmptyStagesList() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(1);
        stageRequest.setMatchId(200); // Different match ID

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stageRequest));
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getStages().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenNoMatchingEnrolledMembers_thenReturnsEmptyEnrolledList() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(50);
        enrolledRequest.setMatchId(200); // Different match ID

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(List.of(enrolledRequest));
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getEnrolledMembers().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenNoMatchingScores_thenReturnsEmptyScoresList() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(200); // Different match ID
        scoreRequest.setStageId(1);
        scoreRequest.setMemberId(50);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenEmptyRequestHolder_thenReturnsResponseWithEmptyLists() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals(100, result.getMatch().getMatchId());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenMultipleTags_thenIncludesAllTags() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(3);
        tagRequest.setTagName("SO");
        TagRequest tagRequest1 = new TagRequest();
        tagRequest1.setTagId(2);
        tagRequest1.setTagName("CRO");
        TagRequest tagRequest2 = new TagRequest();
        tagRequest2.setTagId(1);
        tagRequest2.setTagName("RO");
        List<TagRequest> tags = List.of(
                tagRequest2,
                tagRequest1,
                tagRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(tags);
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());
        ipscRequestHolder.setClubs(null);

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTags().size());
        TagResponse tagResponse1 = result.getTags().stream()
                .filter(tag -> tag.getTagId().equals(1)).findFirst().orElse(null);
        TagResponse tagResponse2 = result.getTags().stream()
                .filter(tag -> tag.getTagId().equals(2)).findFirst().orElse(null);
        TagResponse tagResponse3 = result.getTags().stream()
                .filter(tag -> tag.getTagId().equals(3)).findFirst().orElse(null);
        assertNotNull(tagResponse1);
        assertNotNull(tagResponse2);
        assertNotNull(tagResponse3);
        assertEquals(1, tagResponse1.getTagId());
        assertEquals("RO", tagResponse1.getTagName());
        assertEquals(2, tagResponse2.getTagId());
        assertEquals("CRO", tagResponse2.getTagName());
        assertEquals(3, tagResponse3.getTagId());
        assertEquals("SO", tagResponse3.getTagName());
    }

    @Test
    public void testCreateBasicMatch_whenMixedMatchIds_thenFiltersCorrectly() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(3);
        stageRequest.setMatchId(100);
        StageRequest stageRequest1 = new StageRequest();
        stageRequest1.setStageId(2);
        stageRequest1.setMatchId(200);
        StageRequest stageRequest2 = new StageRequest();
        stageRequest2.setStageId(1);
        stageRequest2.setMatchId(100);
        List<StageRequest> stages = List.of(
                stageRequest2,
                stageRequest1,
                stageRequest
        );
        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(51);
        enrolledRequest.setMatchId(300);
        EnrolledRequest enrolledRequest1 = new EnrolledRequest();
        enrolledRequest1.setMemberId(50);
        enrolledRequest1.setMatchId(100);
        List<EnrolledRequest> enrolledMembers = List.of(
                enrolledRequest1,
                enrolledRequest
        );
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(400);
        scoreRequest.setMemberId(52);
        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setMemberId(50);
        List<ScoreRequest> scores = List.of(
                scoreRequest1,
                scoreRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(stages);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(enrolledMembers);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getMatch().getMatchId());

        // Only stages with matchId 100
        assertEquals(2, result.getStages().size());
        StageResponse stageResponse1 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(1)).findFirst().orElse(null);
        StageResponse stageResponse2 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(2)).findFirst().orElse(null);
        StageResponse stageResponse3 = result.getStages().stream()
                .filter(stage -> stage.getStageId().equals(3)).findFirst().orElse(null);
        assertNotNull(stageResponse1);
        assertNull(stageResponse2);
        assertNotNull(stageResponse3);
        assertEquals(1, stageResponse1.getStageId());
        assertEquals(100, stageResponse1.getMatchId());
        assertEquals(3, stageResponse3.getStageId());
        assertEquals(100, stageResponse3.getMatchId());

        // Only enrolled with matchId 100
        assertEquals(1, result.getEnrolledMembers().size());
        EnrolledResponse enrolledResponse1 = result.getEnrolledMembers().stream()
                .filter(member -> member.getMemberId().equals(50)).findFirst().orElse(null);
        EnrolledResponse enrolledResponse2 = result.getEnrolledMembers().stream()
                .filter(member -> member.getMemberId().equals(51)).findFirst().orElse(null);
        assertNotNull(enrolledResponse1);
        assertNull(enrolledResponse2);
        assertEquals(50, enrolledResponse1.getMemberId());
        assertEquals(100, enrolledResponse1.getMatchId());

        // Only scores with matchId 100
        assertEquals(1, result.getScores().size());
        ScoreResponse scoreResponse1 = result.getScores().stream()
                .filter(score -> score.getMemberId().equals(50)).findFirst().orElse(null);
        ScoreResponse scoreResponse2 = result.getScores().stream()
                .filter(score -> score.getMemberId().equals(52)).findFirst().orElse(null);
        assertNotNull(scoreResponse1);
        assertNull(scoreResponse2);
        assertEquals(50, scoreResponse1.getMemberId());
        assertEquals(100, scoreResponse1.getMatchId());
    }

    @Test
    public void testCreateBasicMatch_whenNullMatchId_thenReturnsNull() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(null);
        matchRequest.setMatchName("Test Match");

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(1);
        stageRequest.setMatchId(100);

        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(50);
        enrolledRequest.setMatchId(100);
        List<EnrolledRequest> enrolledMembers = List.of(enrolledRequest);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setMemberId(50);
        List<ScoreRequest> scores = List.of(scoreRequest);
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stageRequest));
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(enrolledMembers);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNull(result);
    }

    // =====================================================================
    // Tests for addClubToMatch - Club Matching and Null Handling
    // =====================================================================

    @Test
    public void testAddClubToMatch_whenMatchingClub_thenSetsClubOnResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");
        clubRequest.setContact("John Doe");
        clubRequest.setEmail("test@club.com");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertEquals("Test Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenNoMatchingClub_thenSetsDefaultClubResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(2); // Different club ID
        clubRequest.setClubCode("XYZ");
        clubRequest.setClubName("Other Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        // Default club response should only have club ID set
        assertNull(ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenEmptyClubsList_thenSetsDefaultClubResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(new ArrayList<>());

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertNull(ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenMultipleClubs_thenSetsCorrectClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(2);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(3);
        clubRequest.setClubCode("GHI");
        clubRequest.setClubName("Third Club");
        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(2);
        clubRequest1.setClubCode("DEF");
        clubRequest1.setClubName("Second Club");
        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(1);
        clubRequest2.setClubCode("ABC");
        clubRequest2.setClubName("First Club");
        List<ClubRequest> clubs = List.of(
                clubRequest2,
                clubRequest1,
                clubRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(clubs);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("DEF", ipscResponse.getClub().getClubCode());
        assertEquals("Second Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenClubIdNull_thenSetsNullClubResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(null);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNull(ipscResponse.getClub());
    }

    @Test
    public void testAddClubToMatch_whenClubIdZero_thenFindsMatchingClubOrDefault() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(0);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(0);
        clubRequest.setClubCode("DEF");
        clubRequest.setClubName("Default Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(0, ipscResponse.getClub().getClubId());
        assertEquals("DEF", ipscResponse.getClub().getClubCode());
        assertEquals("Default Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenCompleteClubData_thenSetsAllClubFields() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Alpha Club");
        clubRequest.setContact("John Doe");
        clubRequest.setEmail("info@alphaclub.co.za");
        clubRequest.setAddress1("123 Main St");
        clubRequest.setAddress2("Suite 100");
        clubRequest.setCity("Cape Town");
        clubRequest.setProvince("Western Cape");
        clubRequest.setCountryId("ZA");
        clubRequest.setPostalCode("8001");
        clubRequest.setOfficePhoneNumber("0211234567");
        clubRequest.setAlternativePhoneNumber("0827654321");
        clubRequest.setFaxNumber("0211234568");
        clubRequest.setWebsite("https://alphaclub.co.za");
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertEquals("Alpha Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenNullClubsList_thenDoesNothing() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(null);  // Null clubs list

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNull(ipscResponse.getClub());  // No club should be set when the clubs list is null
    }

    @Test
    public void testAddClubToMatch_whenAllNullClubsInList_thenSetsDefaultClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        List<ClubRequest> clubList = new ArrayList<>();
        clubList.add(null);
        clubList.add(null);
        clubList.add(null);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(clubList);  // List with all nulls

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertNull(ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenEmptyClubCode_thenPreservesEmptyString() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("");  // Empty code
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("", ipscResponse.getClub().getClubCode());
        assertEquals("Test Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenEmptyClubName_thenPreservesEmptyString() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("");  // Empty name

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertEquals("", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenWhitespaceClubCode_thenPreservesWhitespace() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("  \t  ");  // Whitespace with tab
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("  \t  ", ipscResponse.getClub().getClubCode());
    }

    @Test
    public void testAddClubToMatch_whenOnlyClubId_thenSetsOnlyClubId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        // No code, name, or other fields set

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertNull(ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenIdAndCodeOnly_thenSetsIdAndCode() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        // Name is not set

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenCompleteClubDataAllFields_thenSetsAllFields() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Alpha Club");
        clubRequest.setContact("John Doe");
        clubRequest.setEmail("info@alphaclub.co.za");
        clubRequest.setAddress1("123 Main St");
        clubRequest.setAddress2("Suite 100");
        clubRequest.setCity("Cape Town");
        clubRequest.setProvince("Western Cape");
        clubRequest.setCountryId("ZA");
        clubRequest.setPostalCode("8001");
        clubRequest.setOfficePhoneNumber("0211234567");
        clubRequest.setAlternativePhoneNumber("0827654321");
        clubRequest.setFaxNumber("0211234568");
        clubRequest.setWebsite("https://alphaclub.co.za");
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertEquals("Alpha Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenMultipleClubsAllComplete_thenSelectsCorrectOne() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(2);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(3);
        clubRequest.setClubCode("GHI");
        clubRequest.setClubName("Third Club");
        clubRequest.setContact("Contact 3");
        clubRequest.setEmail("club3@test.com");
        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(2);
        clubRequest1.setClubCode("DEF");
        clubRequest1.setClubName("Second Club");
        clubRequest1.setContact("Contact 2");
        clubRequest1.setEmail("club2@test.com");
        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(1);
        clubRequest2.setClubCode("ABC");
        clubRequest2.setClubName("First Club");
        clubRequest2.setContact("Contact 1");
        clubRequest2.setEmail("club1@test.com");
        List<ClubRequest> clubs = List.of(
                clubRequest2,
                clubRequest1,
                clubRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(clubs);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("DEF", ipscResponse.getClub().getClubCode());
        assertEquals("Second Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenNullIpscResponse_thenDoesNothing() {
        // Arrange
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");

        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> ipscMatchService.addClubToMatch(null, ipscRequestHolder));
    }

    @Test
    public void testAddClubToMatch_whenNullIpscRequestHolder_thenDoesNothing() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> ipscMatchService.addClubToMatch(ipscResponse, null));
    }

    @Test
    public void testAddClubToMatch_whenBothNullParameters_thenDoesNothing() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> ipscMatchService.addClubToMatch(null, null));
    }

    @Test
    public void testAddClubToMatch_whenNullMatch_thenDoesNothing() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(null);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert - club should not be set since match is null
        assertNull(ipscResponse.getClub());
    }

    @Test
    public void testAddClubToMatch_whenLargeClubIdNumber_thenFindsCorrectClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(999999);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(999999);
        clubRequest.setClubCode("XYZ");
        clubRequest.setClubName("Large ID Club");
        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(100);
        clubRequest1.setClubCode("ABC");
        clubRequest1.setClubName("Club One");
        List<ClubRequest> clubs = List.of(
                clubRequest1,
                clubRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(clubs);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(999999, ipscResponse.getClub().getClubId());
        assertEquals("XYZ", ipscResponse.getClub().getClubCode());
        assertEquals("Large ID Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenNegativeClubId_thenHandlesNegativeId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(-1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(-1);
        clubRequest.setClubCode("NEG");
        clubRequest.setClubName("Negative Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(-1, ipscResponse.getClub().getClubId());
        assertEquals("NEG", ipscResponse.getClub().getClubCode());
        assertEquals("Negative Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenDuplicateClubIds_thenUsesFirstMatch() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("SECOND");
        clubRequest.setClubName("Second Club");
        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(1);
        clubRequest1.setClubCode("FIRST");
        clubRequest1.setClubName("First Club");
        List<ClubRequest> clubs = List.of(
                clubRequest1,
                clubRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(null);
        ipscRequestHolder.setClubs(clubs);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert - should use first match
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("FIRST", ipscResponse.getClub().getClubCode());
        assertEquals("First Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenPartialClubData_thenSetsAvailableFields() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        // Club name is not set - null

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("ABC", ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenClubCodeAndNameNull_thenSetsClubIdOnly() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode(null);
        clubRequest.setClubName(null);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertNull(ipscResponse.getClub().getClubCode());
        assertNull(ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenVeryLongClubCode_thenPreservesFullCode() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        String longCode = "A".repeat(500);  // 500 characters code

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode(longCode);
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(longCode, ipscResponse.getClub().getClubCode());
        assertEquals(500, ipscResponse.getClub().getClubCode().length());
    }

    @Test
    public void testAddClubToMatch_whenVeryLongClubName_thenPreservesFullName() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        String longName = "Club With Very Long Name ".repeat(20);  // Very long name

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName(longName);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(longName, ipscResponse.getClub().getClubName());
        assertTrue(ipscResponse.getClub().getClubName().length() > 100);
    }

    @Test
    public void testAddClubToMatch_whenSpecialCharactersInCode_thenPreservesCharacters() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC-123_XYZ");
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("ABC-123_XYZ", ipscResponse.getClub().getClubCode());
    }

    @Test
    public void testAddClubToMatch_whenPunctuationInClubName_thenPreservesCharacters() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Smith's Club & Associates, Inc.");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("Smith's Club & Associates, Inc.", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenUnicodeInClubCode_thenPreservesUnicode() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ÅBC");
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("ÅBC", ipscResponse.getClub().getClubCode());
    }

    @Test
    public void testAddClubToMatch_whenEmojiInClubName_thenPreservesEmoji() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Club 🎯 Target");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals("Club 🎯 Target", ipscResponse.getClub().getClubName());
    }

    // =====================================================================
    // Tests for addClubToMatch - Edge Cases: Extreme Club IDs
    // =====================================================================

    @Test
    public void testAddClubToMatch_whenMaxIntClubId_thenProcessesSuccessfully() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(Integer.MAX_VALUE);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(Integer.MAX_VALUE);
        clubRequest.setClubCode("MAX");
        clubRequest.setClubName("Max Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(Integer.MAX_VALUE, ipscResponse.getClub().getClubId());
        assertEquals("MAX", ipscResponse.getClub().getClubCode());
        assertEquals("Max Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenMinIntClubId_thenProcessesSuccessfully() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(Integer.MIN_VALUE);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(Integer.MIN_VALUE);
        clubRequest.setClubCode("MIN");
        clubRequest.setClubName("Min Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(Integer.MIN_VALUE, ipscResponse.getClub().getClubId());
        assertEquals("MIN", ipscResponse.getClub().getClubCode());
        assertEquals("Min Club", ipscResponse.getClub().getClubName());
    }

    // =====================================================================
    // Tests for addClubToMatch - Edge Cases: Collections with Nulls
    // =====================================================================

    @Test
    public void testAddClubToMatch_whenNullElementsInClubList_thenFiltersAndFindsMatch() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(2);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest club1 = new ClubRequest();
        club1.setClubId(1);
        club1.setClubCode("AAA");
        club1.setClubName("First Club");

        ClubRequest club2 = new ClubRequest();
        club2.setClubId(2);
        club2.setClubCode("BBB");
        club2.setClubName("Second Club");

        List<ClubRequest> clubList = new ArrayList<>();
        clubList.add(club1);
        clubList.add(null);
        clubList.add(club2);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(clubList);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("BBB", ipscResponse.getClub().getClubCode());
        assertEquals("Second Club", ipscResponse.getClub().getClubName());
    }

    // =====================================================================
    // Tests for addClubToMatch - Edge Cases: Overwriting Existing Club
    // =====================================================================

    @Test
    public void testAddClubToMatch_whenExistingClubThenNewData_thenCompletelyOverwrites() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(2);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        // Pre-set an old club
        ClubResponse oldClub = new ClubResponse();
        oldClub.setClubId(1);
        oldClub.setClubCode("OLD");
        oldClub.setClubName("Old Club");
        ipscResponse.setClub(oldClub);

        ClubRequest newClub = new ClubRequest();
        newClub.setClubId(2);
        newClub.setClubCode("NEW");
        newClub.setClubName("New Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(newClub));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("NEW", ipscResponse.getClub().getClubCode());
        assertEquals("New Club", ipscResponse.getClub().getClubName());
    }

    // =====================================================================
    // Tests for addClubToMatch - Edge Cases: Large Club Lists
    // =====================================================================

    @Test
    public void testAddClubToMatch_whenLargeClubListManyElements_thenFindsCorrectClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(50);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        List<ClubRequest> clubList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ClubRequest club = new ClubRequest();
            club.setClubId(i);
            club.setClubCode("CLUB" + i);
            club.setClubName("Club " + i);
            clubList.add(club);
        }

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(clubList);

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(50, ipscResponse.getClub().getClubId());
        assertEquals("CLUB50", ipscResponse.getClub().getClubCode());
        assertEquals("Club 50", ipscResponse.getClub().getClubName());
    }

    // =====================================================================
    // Tests for addMembersToMatch - Member Collection and Filtering
    // =====================================================================

    @Test
    public void testAddMembersToMatch_whenMatchingMembers_thenSetsMembersOnResponse() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest1 = new MemberRequest();
        memberRequest1.setMemberId(50);
        memberRequest1.setFirstName("John");
        memberRequest1.setLastName("Doe");
        MemberRequest memberRequest2 = new MemberRequest();
        memberRequest2.setMemberId(51);
        memberRequest2.setFirstName("Jane");
        memberRequest2.setLastName("Smith");
        List<MemberRequest> members = List.of(memberRequest1, memberRequest2);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);
        scoreRequest.setMemberId(51);
        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setStageId(1);
        scoreRequest1.setMemberId(50);
        List<ScoreRequest> scores = List.of(
                scoreRequest1,
                scoreRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(members);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertEquals(2, ipscResponse.getMembers().size());
        MemberResponse memberResponse1 = ipscResponse.getMembers().stream()
                .filter(member -> member.getMemberId().equals(50)).findFirst().orElse(null);
        MemberResponse memberResponse2 = ipscResponse.getMembers().stream()
                .filter(member -> member.getMemberId().equals(51)).findFirst().orElse(null);
        assertNotNull(memberResponse1);
        assertNotNull(memberResponse2);
        assertEquals(50, memberResponse1.getMemberId());
        assertEquals("John", memberResponse1.getFirstName());
        assertEquals("Doe", memberResponse1.getLastName());
        assertEquals(51, memberResponse2.getMemberId());
        assertEquals("Jane", memberResponse2.getFirstName());
        assertEquals("Smith", memberResponse2.getLastName());

    }

    @Test
    public void testAddMembersToMatch_whenSingleScore_thenSetsMatchingMember() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        memberRequest.setIcsAlias("12345");
        List<MemberRequest> members = List.of(memberRequest);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);
        scoreRequest.setMemberId(50);
        scoreRequest.setFinalScore(100);
        List<ScoreRequest> scores = List.of(scoreRequest);
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(members);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertEquals(1, ipscResponse.getMembers().size());
        assertEquals(50, ipscResponse.getMembers().getFirst().getMemberId());
        assertEquals("John", ipscResponse.getMembers().getFirst().getFirstName());
        assertEquals("Doe", ipscResponse.getMembers().getFirst().getLastName());
        assertEquals("12345", ipscResponse.getMembers().getFirst().getIcsAlias());
    }

    @Test
    public void testAddMembersToMatch_whenNoMatchingMembers_thenSetsEmptyList() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(99); // Different member ID
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest));
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertTrue(ipscResponse.getMembers().isEmpty());
    }

    @Test
    public void testAddMembersToMatch_whenNoScores_thenDoesNotSetMembers() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest));
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        // Since there are no scores, forEach is never executed and members remain an empty list
        assertNotNull(ipscResponse.getMembers());
        assertTrue(ipscResponse.getMembers().isEmpty());
    }

    @Test
    public void testAddMembersToMatch_whenNoMembers_thenSetsEmptyList() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(50);
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(new ArrayList<>());
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertTrue(ipscResponse.getMembers().isEmpty());
    }

    @Test
    public void testAddMembersToMatch_whenMultipleMembersAndScores_thenSetsLastMatchingMembers() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest1 = new MemberRequest();
        memberRequest1.setMemberId(50);
        memberRequest1.setFirstName("John");
        memberRequest1.setLastName("Doe");
        MemberRequest memberRequest2 = new MemberRequest();
        memberRequest2.setMemberId(51);
        memberRequest2.setFirstName("Jane");
        memberRequest2.setLastName("Smith");
        MemberRequest memberRequest3 = new MemberRequest();
        memberRequest3.setMemberId(52);
        memberRequest3.setFirstName("Bob");
        memberRequest3.setLastName("Johnson");
        List<MemberRequest> members = List.of(memberRequest1, memberRequest2, memberRequest3);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setMemberId(52);
        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setMemberId(51);
        ScoreRequest scoreRequest2 = new ScoreRequest();
        scoreRequest2.setMatchId(100);
        scoreRequest2.setMemberId(50);
        List<ScoreRequest> scores = List.of(
                scoreRequest2,
                scoreRequest1,
                scoreRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(members);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertEquals(3, ipscResponse.getMembers().size());
        MemberResponse memberResponse1 = ipscResponse.getMembers().stream()
                .filter(member -> member.getMemberId() == 50).findFirst().orElse(null);
        MemberResponse memberResponse2 = ipscResponse.getMembers().stream()
                .filter(member -> member.getMemberId() == 51).findFirst().orElse(null);
        MemberResponse memberResponse3 = ipscResponse.getMembers().stream()
                .filter(member -> member.getMemberId() == 52).findFirst().orElse(null);
        assertNotNull(memberResponse1);
        assertNotNull(memberResponse2);
        assertNotNull(memberResponse3);
        assertEquals(50, memberResponse1.getMemberId());
        assertEquals("John", memberResponse1.getFirstName());
        assertEquals("Doe", memberResponse1.getLastName());
        assertEquals(51, memberResponse2.getMemberId());
        assertEquals("Jane", memberResponse2.getFirstName());
        assertEquals("Smith", memberResponse2.getLastName());
        assertEquals(52, memberResponse3.getMemberId());
        assertEquals("Bob", memberResponse3.getFirstName());
        assertEquals("Johnson", memberResponse3.getLastName());
    }

    @Test
    public void testAddMembersToMatch_whenMixedMatchingAndNonMatchingMembers_thenSetsMatchingMembers() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest1 = new MemberRequest();
        memberRequest1.setMemberId(50);
        memberRequest1.setFirstName("John");
        memberRequest1.setLastName("Doe");

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(52);
        memberRequest.setFirstName("Bob");
        memberRequest.setLastName("Johnson");
        MemberRequest memberRequest2 = new MemberRequest();
        memberRequest2.setMemberId(51);
        memberRequest2.setFirstName("Jane");
        memberRequest2.setLastName("Smith");
        List<MemberRequest> members = List.of(
                memberRequest1,
                memberRequest2,
                memberRequest
        );
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);
        scoreRequest.setMemberId(51);
        List<ScoreRequest> scores = List.of(scoreRequest);
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(null);
        ipscRequestHolder.setStages(null);
        ipscRequestHolder.setMembers(members);
        ipscRequestHolder.setEnrolledMembers(null);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertEquals(1, ipscResponse.getMembers().size());
        assertEquals(51, ipscResponse.getMembers().getFirst().getMemberId());
        assertEquals("Jane", ipscResponse.getMembers().getFirst().getFirstName());
        assertEquals("Smith", ipscResponse.getMembers().getFirst().getLastName());
    }

    @Test
    public void testAddMembersToMatch_whenNullMemberId_thenHandlesGracefully() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(null);
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest));
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertTrue(ipscResponse.getMembers().isEmpty());
    }

    @Test
    public void testAddMembersToMatch_whenCompleteMemberData_thenSetsAllMemberFields() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        memberRequest.setIcsAlias("12345");
        memberRequest.setIsRegisteredForMatch(true);
        memberRequest.setDateOfBirth(LocalDateTime.of(1980, 5, 15, 0, 0));
        memberRequest.setInactive(false);
        memberRequest.setFemale(false);

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(50);
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest));
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        ipscMatchService.addMembersToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getMembers());
        assertEquals(1, ipscResponse.getMembers().size());
        MemberResponse result = ipscResponse.getMembers().getFirst();
        assertEquals(50, result.getMemberId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("12345", result.getIcsAlias());
        assertTrue(result.getIsRegisteredForMatch());
        assertEquals(LocalDateTime.of(1980, 5, 15, 0, 0), result.getDateOfBirth());
        assertFalse(result.getFemale());
    }

    // =====================================================================
    // Tests for createBasicMatch - Null Input Handling
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenNullMatch_thenReturnsEmptyOptional() {
        // Arrange
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateBasicMatch_whenNullIpscRequestHolder_thenThrowsNullPointerException() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                ipscMatchService.createBasicMatch(null, matchRequest));
    }

    @Test
    public void testCreateBasicMatch_whenNullMatchId_thenReturnsEmptyOptional() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(null);  // Null match ID
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateBasicMatch_whenNullStages_thenReturnsEmptyOptional() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(null);  // Null stages
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateBasicMatch_whenNullEnrolledMembers_thenReturnsEmptyOptional() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(null);  // Null enrolled members
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateBasicMatch_whenNullScores_thenReturnsEmptyOptional() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(null);  // Null scores

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for createBasicMatch - Null/Empty/Blank String Fields
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenBlankMatchName_thenProcesses() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("");  // Blank name

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100, result.get().getMatch().getMatchId());
        assertEquals("", result.get().getMatch().getMatchName());
    }

    @Test
    public void testCreateBasicMatch_whenNullMatchName_thenProcesses() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName(null);  // Null name

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100, result.get().getMatch().getMatchId());
        assertNull(result.get().getMatch().getMatchName());
    }

    @Test
    public void testCreateBasicMatch_whenWhitespaceMatchName_thenProcesses() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("   ");  // Whitespace only

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("   ", result.get().getMatch().getMatchName());
    }

    // =====================================================================
    // Tests for createBasicMatch - Partial Input (Empty Collections)
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenEmptyTags_thenReturnsResponseWithEmptyTags() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());  // Empty tags
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getTags().isEmpty());
        assertEquals(100, result.get().getMatch().getMatchId());
    }

    @Test
    public void testCreateBasicMatch_whenEmptyStages_thenReturnsResponseWithEmptyStages() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());  // Empty stages
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getStages().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenEmptyEnrolledMembers_thenReturnsResponseWithEmptyEnrolled() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());  // Empty enrolled members
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getEnrolledMembers().isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenEmptyScores_thenReturnsResponseWithEmptyScores() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());  // Empty scores

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getScores().isEmpty());
    }

    // =====================================================================
    // Tests for createBasicMatch - Partial Input (Only Some Collections)
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenOnlyStages_thenFiltersStagesByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(2);
        stageRequest.setMatchId(200);
        stageRequest.setStageName("Stage 2");
        StageRequest stageRequest1 = new StageRequest();
        stageRequest1.setStageId(1);
        stageRequest1.setMatchId(100);
        stageRequest1.setStageName("Stage 1");
        List<StageRequest> stages = List.of(
                stageRequest1,
                stageRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(stages);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStages().size());
        assertEquals(1, result.get().getStages().getFirst().getStageId());
        assertEquals(100, result.get().getStages().getFirst().getMatchId());
    }

    @Test
    public void testCreateBasicMatch_whenOnlyEnrolledMembers_thenFiltersEnrolledByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(51);
        enrolledRequest.setMatchId(200);
        EnrolledRequest enrolledRequest1 = new EnrolledRequest();
        enrolledRequest1.setMemberId(50);
        enrolledRequest1.setMatchId(100);
        List<EnrolledRequest> enrolledMembers = List.of(
                enrolledRequest1,
                enrolledRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(enrolledMembers);
        ipscRequestHolder.setScores(new ArrayList<>());
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getEnrolledMembers().size());
        assertEquals(50, result.get().getEnrolledMembers().getFirst().getMemberId());
    }

    @Test
    public void testCreateBasicMatch_whenOnlyScores_thenFiltersScoresByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(200);
        scoreRequest.setMemberId(51);
        scoreRequest.setFinalScore(95);
        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setMemberId(50);
        scoreRequest1.setFinalScore(100);
        List<ScoreRequest> scores = List.of(
                scoreRequest1,
                scoreRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
        assertEquals(100, result.get().getScores().getFirst().getMatchId());
    }

    @Test
    public void testCreateBasicMatch_whenStagesAndScores_thenFiltersCorrectly() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(1);
        stageRequest.setMatchId(100);
        List<StageRequest> stages = List.of(stageRequest);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setMemberId(50);
        List<ScoreRequest> scores = List.of(scoreRequest);
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(stages);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStages().size());
        assertEquals(1, result.get().getScores().size());
        assertTrue(result.get().getEnrolledMembers().isEmpty());
    }

    // =====================================================================
    // Tests for createBasicMatch - Full Input (All Collections Populated)
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenFullData_thenReturnsCompleteResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Full Match Data");
        matchRequest.setMatchDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(1);
        tagRequest.setTagName("RO");
        List<TagRequest> tags = List.of(tagRequest);
        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(1);
        stageRequest.setMatchId(100);
        stageRequest.setStageName("Stage 1");
        List<StageRequest> stages = List.of(stageRequest);
        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(50);
        enrolledRequest.setMatchId(100);
        List<EnrolledRequest> enrolledMembers = List.of(enrolledRequest);
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setMemberId(50);
        scoreRequest.setFinalScore(100);
        List<ScoreRequest> scores = List.of(scoreRequest);
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(tags);
        ipscRequestHolder.setStages(stages);
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(enrolledMembers);
        ipscRequestHolder.setScores(scores);
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        IpscResponse response = result.get();
        assertEquals(100, response.getMatch().getMatchId());
        assertEquals("Full Match Data", response.getMatch().getMatchName());
        assertEquals(1, response.getTags().size());
        assertEquals(1, response.getStages().size());
        assertEquals(1, response.getEnrolledMembers().size());
        assertEquals(1, response.getScores().size());
    }

    @Test
    public void testCreateBasicMatch_whenMultipleTags_thenIncludesAll() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(3);
        tagRequest.setTagName("SO");
        TagRequest tagRequest1 = new TagRequest();
        tagRequest1.setTagId(2);
        tagRequest1.setTagName("CRO");
        TagRequest tagRequest2 = new TagRequest();
        tagRequest2.setTagId(1);
        tagRequest2.setTagName("RO");
        List<TagRequest> tags = List.of(
                tagRequest2,
                tagRequest1,
                tagRequest
        );
        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(tags);
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setMembers(null);
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());
        ipscRequestHolder.setClubs(null);

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getTags().size());
    }

    // =====================================================================
    // Tests for createBasicMatch - Edge Cases: Null Elements in Collections
    // =====================================================================

    @Test
    public void testCreateBasicMatch_whenNullStagesInCollection_thenFiltersNulls() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(1);
        stage1.setMatchId(100);

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(2);
        stage2.setMatchId(100);

        List<StageRequest> stageList = new ArrayList<>();
        stageList.add(stage1);
        stageList.add(null);
        stageList.add(stage2);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(stageList);  // Contains null
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getStages().size());  // Null filtered out
    }

    @Test
    public void testCreateBasicMatch_whenNullScoresInCollection_thenFiltersNulls() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        ScoreRequest score1 = new ScoreRequest();
        score1.setMatchId(100);
        score1.setMemberId(50);

        ScoreRequest score2 = new ScoreRequest();
        score2.setMatchId(100);
        score2.setMemberId(51);

        List<ScoreRequest> scoreList = new ArrayList<>();
        scoreList.add(score1);
        scoreList.add(null);
        scoreList.add(score2);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(scoreList);  // Contains null

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getScores().size());  // Null filtered out
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenEmptyList_thenReturnsEmptyHolder() {
        // Arrange
        List<IpscMatch> ipscMatchEntityList = new ArrayList<>();

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(ipscMatchEntityList);

        // Assert
        assertNotNull(result);
        assertNotNull(result.matches());
        assertTrue(result.matches().isEmpty());
    }

    // =====================================================================
    // Tests for generateIpscMatchRecordHolder - Single and Multiple Matches
    // =====================================================================

    @Test
    public void testGenerateIpscMatchRecordHolder_whenSingleMatch_thenReturnsSingleRecord() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());
        assertEquals("Test Match", result.matches().getFirst().name());
//        assertEquals("HPSC", result.matches().getFirst().clubName());
        assertEquals("Handgun", result.matches().getFirst().matchFirearmType());
        assertEquals("Club Shoot", result.matches().getFirst().matchCategory());
        assertEquals("2025-09-05 10:00", result.matches().getFirst().scheduledDate());
        assertEquals("2025-09-06 15:00", result.matches().getFirst().dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenMultipleMatches_thenReturnsMultipleRecords() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.SOSC.getName());
        club.setAbbreviation(ClubIdentifier.SOSC.getName());

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 4, 15, 0, 0));
        match2.setClub(club);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 5, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match, match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(2, result.matches().size());
        assertEquals("Match 1", result.matches().getFirst().name());
        assertEquals("Match 2", result.matches().get(1).name());
        assertEquals("SOSC", result.matches().getFirst().clubName());
        assertEquals("SOSC", result.matches().get(1).clubName());
        assertEquals("Handgun", result.matches().getFirst().matchFirearmType());
        assertEquals("Rifle", result.matches().get(1).matchFirearmType());
        assertEquals("League", result.matches().getFirst().matchCategory());
        assertEquals("Club Shoot", result.matches().get(1).matchCategory());
        assertEquals("2025-09-05 10:00", result.matches().getFirst().scheduledDate());
        assertEquals("2025-09-04 15:00", result.matches().get(1).scheduledDate());
        assertEquals("2025-09-06 10:00", result.matches().getFirst().dateEdited());
        assertEquals("2025-09-05 15:00", result.matches().get(1).dateEdited());
    }

    // =====================================================================
    // Tests for generateIpscMatchRecordHolder - Null Field Handling
    // =====================================================================

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullMatchStages_thenProcessesWithoutStages() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.HPSC.getName());
        club.setAbbreviation(ClubIdentifier.HPSC.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN_22);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 15, 30));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());
        assertEquals("Test Match", result.matches().getFirst().name());
        assertEquals("HPSC", result.matches().getFirst().clubName());
        assertEquals("Handgun .22", result.matches().getFirst().matchFirearmType());
        assertEquals("League", result.matches().getFirst().matchCategory());
        assertEquals("2025-09-05 10:00", result.matches().getFirst().scheduledDate());
        assertEquals("2025-09-06 15:15", result.matches().getFirst().dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullMatchCompetitors_thenProcessesWithoutCompetitors() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.SOSC.getName());
        club.setAbbreviation(ClubIdentifier.SOSC.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.SHOTGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 30));

        List<IpscMatch> matchList = List.of(match);

        // Act & Assert
        IpscMatchRecordHolder result = assertDoesNotThrow(() ->
                ipscMatchService.generateIpscMatchRecordHolder(matchList));

        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());
        assertEquals("Test Match", result.matches().getFirst().name());
        assertEquals("SOSC", result.matches().getFirst().clubName());
        assertEquals("Shotgun", result.matches().getFirst().matchFirearmType());
        assertEquals("League", result.matches().getFirst().matchCategory());
        assertEquals("2025-09-05 10:00", result.matches().getFirst().scheduledDate());
        assertEquals("2025-09-06 15:00", result.matches().getFirst().dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenMatchStagesButNoCompetitors_thenReturnsEmptyCompetitors() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.UNKNOWN.getName());
        club.setAbbreviation(ClubIdentifier.UNKNOWN.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.PCC);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 30, 0));

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatch(match);
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");
        stage.setMatchStageCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullDateFields_thenHandlesNullDates() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.SOSC.getName());
        club.setAbbreviation(ClubIdentifier.SOSC.getName());

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.MINI_RIFLE);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(null);

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertEquals("Test Match", result.matches().getFirst().name());
        assertEquals("", result.matches().getFirst().scheduledDate());
        assertEquals("", result.matches().getFirst().dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullMatchNameAndCategory_thenProcesses() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.UNKNOWN.getName());
        club.setAbbreviation(ClubIdentifier.UNKNOWN.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName(null);
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertEquals("", result.matches().getFirst().name());
        assertEquals("", result.matches().getFirst().matchCategory());
    }

    // =====================================================================
    // Tests for generateIpscMatchRecordHolder - Edge Cases and Special Data
    // =====================================================================

    @Test
    public void testGenerateIpscMatchRecordHolder_whenLargeNumberOfMatches_thenProcessesAll() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.UNKNOWN.getName());
        club.setAbbreviation(ClubIdentifier.UNKNOWN.getName());

        List<IpscMatch> matchList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            IpscMatch match = new IpscMatch();
            match.setId((long) i);
            match.setName("Match " + i);
            match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
            match.setClub(club);
            match.setMatchFirearmType(FirearmType.HANDGUN);
            match.setMatchCategory(MatchCategory.CLUB_SHOOT);
            match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
            matchList.add(match);
        }

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.matches().size());
        for (int i = 0; i < 10; i++) {
            assertEquals("Match " + (i + 1), result.matches().get(i).name());
        }
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenSpecialCharactersInNames_thenPreservesCharacters() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match & Co. (2025) - v2.0");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
//        match.setClubName(ClubIdentifier.SOSC);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals("Test Match & Co. (2025) - v2.0", result.matches().getFirst().name());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenEmptyMatchStagesList_thenProcessesCorrectly() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.PMPSC.getName());
        club.setAbbreviation(ClubIdentifier.PMPSC.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 11, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.MINI_RIFLE);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 12, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenEmptyCompetitorsList_thenProcessesCorrectly() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.SOSC.getName());
        club.setAbbreviation(ClubIdentifier.SOSC.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN_22);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenMatchContainsAllFields_thenVerifiesDataIntegrity() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.HPSC.getName());
        club.setAbbreviation(ClubIdentifier.HPSC.getName());
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Complete Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 30, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.RIFLE);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 16, 45, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        var record = result.matches().getFirst();
        assertEquals("Complete Match", record.name());
        assertEquals("HPSC", record.clubName());
        assertEquals("Rifle", record.matchFirearmType());
        assertEquals("Club Shoot", record.matchCategory());
        assertTrue(record.competitors().isEmpty());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Null Input Handling
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenNullMatch_thenReturnsEmptyOptional() {
        // Arrange
        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(null, competitors);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitIpscMatchResponse_whenNullCompetitors_thenReturnsEmptyOptional() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitIpscMatchResponse_whenBothNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Null/Blank/Empty String Handling
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenNullMatchName_thenReturnsNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName(null);
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().name());
    }

    @Test
    public void testInitIpscMatchResponse_whenBlankMatchName_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().name());
    }

    @Test
    public void testInitIpscMatchResponse_whenWhitespaceMatchName_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("   ");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("   ", result.get().name());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Partial Input (Only Some Fields)
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenOnlyMatchName_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().name());
        assertEquals("", result.get().clubName());
        assertTrue(result.get().matchFirearmType().isEmpty());
        assertTrue(result.get().matchCategory().isEmpty());
    }

    @Test
    public void testInitIpscMatchResponse_whenOnlyClub_thenSetsClubName() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");
        club.setAbbreviation("TC");

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(club);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().clubName());
        assertFalse(result.get().clubName().isEmpty());
    }

    @Test
    public void testInitIpscMatchResponse_whenOnlyScheduledDate_thenFormatsDate() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 30, 45));
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().scheduledDate());
        assertTrue(result.get().scheduledDate().contains("2025"));
        assertTrue(result.get().scheduledDate().contains("09"));
    }

    @Test
    public void testInitIpscMatchResponse_whenNullScheduledDate_thenHandlesNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().scheduledDate());
    }

    @Test
    public void testInitIpscMatchResponse_whenNullDateEdited_thenHandles() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateEdited());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Full Input (All Fields)
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenCompleteData_thenReturnsCompleteRecord() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Alpha Club");
        club.setAbbreviation("AC");
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Complete Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        CompetitorMatchRecord competitor = new CompetitorMatchRecord("John", "Doe", "Middle",
                "1980-05-15", 12345, "ALIAS001", null, new ArrayList<>());
        List<CompetitorMatchRecord> competitors = List.of(competitor);

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Complete Match", result.get().name());
        assertNotNull(result.get().scheduledDate());
        assertNotNull(result.get().clubName());
        assertNotNull(result.get().matchFirearmType());
        assertNotNull(result.get().matchCategory());
        assertNotNull(result.get().dateEdited());
        assertEquals(1, result.get().competitors().size());
    }

    @Test
    public void testInitIpscMatchResponse_whenMultipleCompetitors_thenIncludesAll() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CompetitorMatchRecord competitor = new CompetitorMatchRecord("FirstName" + i, "LastName" + i,
                    "Middle", "1980-05-15", i, "ALIAS00" + i, null, new ArrayList<>());
            competitors.add(competitor);
        }

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().competitors().size());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Enum Handling
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenAllFirearmTypes_thenProcesses() {
        // Arrange
        FirearmType[] firearmTypes = {
                FirearmType.HANDGUN,
                FirearmType.HANDGUN_22,
                FirearmType.RIFLE,
                FirearmType.SHOTGUN,
                FirearmType.PCC,
                FirearmType.MINI_RIFLE
        };

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act & Assert
        for (FirearmType type : firearmTypes) {
            IpscMatch match = new IpscMatch();
            match.setId(1L);
            match.setName("Test Match");
            match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
            match.setClub(null);
            match.setMatchFirearmType(type);
            match.setMatchCategory(MatchCategory.LEAGUE);
            match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

            Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

            assertTrue(result.isPresent());
            assertNotNull(result.get().matchFirearmType());
            assertFalse(result.get().matchFirearmType().isEmpty());
        }
    }

    @Test
    public void testInitIpscMatchResponse_whenAllMatchCategories_thenProcesses() {
        // Arrange
        MatchCategory[] categories = {
                MatchCategory.LEAGUE,
                MatchCategory.CLUB_SHOOT
        };

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act & Assert
        for (MatchCategory category : categories) {
            IpscMatch match = new IpscMatch();
            match.setId(1L);
            match.setName("Test Match");
            match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
            match.setClub(null);
            match.setMatchFirearmType(FirearmType.HANDGUN);
            match.setMatchCategory(category);
            match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

            Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

            assertTrue(result.isPresent());
            assertNotNull(result.get().matchCategory());
            assertFalse(result.get().matchCategory().isEmpty());
        }
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Null Enum Values
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenNullFirearmType_thenHandlesGracefully() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().matchFirearmType().isEmpty());
    }

    @Test
    public void testInitIpscMatchResponse_whenNullMatchCategory_thenHandlesGracefully() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(null);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().matchCategory().isEmpty());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Extreme Dates
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenHistoricalDate_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Historical Match");
        match.setScheduledDate(LocalDateTime.of(1900, 1, 1, 0, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(1900, 1, 1, 0, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().scheduledDate());
        assertNotNull(result.get().dateEdited());
    }

    @Test
    public void testInitIpscMatchResponse_whenFutureDate_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Future Match");
        match.setScheduledDate(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2099, 12, 31, 23, 59, 59));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().scheduledDate());
        assertNotNull(result.get().dateEdited());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: String Length
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenVeryLongMatchName_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("A".repeat(500));
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(500, result.get().name().length());
    }

    @Test
    public void testInitIpscMatchResponse_whenUnicodeInMatchName_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Internatønal Mätch™ Ñ-你好-мир-🏆");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Internatønal Mätch™ Ñ-你好-мир-🏆", result.get().name());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Empty Competitors List
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenEmptyCompetitorsList_thenIncludesEmpty() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().competitors().isEmpty());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenSpecialCharactersInName_thenPreserves() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match & Co. (2025) - v2.0");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Match & Co. (2025) - v2.0", result.get().name());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Club with Null Name
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenClubNullName_thenHandlesGracefully() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(null);
        club.setAbbreviation("C");

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().clubName());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Large Competitor Lists
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenLargeCompetitorList_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Large Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<CompetitorMatchRecord> competitors = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CompetitorMatchRecord competitor = new CompetitorMatchRecord("FirstName" + i, "LastName" + i,
                    "Middle", "1980-05-15", i, "ALIAS" + i, null, new ArrayList<>());
            competitors.add(competitor);
        }

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100, result.get().competitors().size());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: Both Date Fields Null
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenBothDateFieldsNull_thenHandles() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().scheduledDate());
        assertNotNull(result.get().dateEdited());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Edge Cases: All Null Optional Fields
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_whenAllOptionalFieldsNull_thenProcesses() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(null);

        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(match, competitors);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().name());
        assertEquals("", result.get().clubName());
        assertTrue(result.get().matchFirearmType().isEmpty());
        assertTrue(result.get().matchCategory().isEmpty());
        assertNotNull(result.get().scheduledDate());
        assertNotNull(result.get().dateEdited());
    }

    // =====================================================================
    // Tests for initCompetitor - Null Input Handling
    // =====================================================================

    @Test
    public void testInitCompetitor_whenNullCompetitor_thenReturnsEmptyOptional() {
        // Arrange
        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(null, matchRecord, stages);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitCompetitor_whenNullMatchCompetitorRecord_thenReturnsEmptyOptional() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, null, stages);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitCompetitor_whenNullStagesList_thenReturnsEmptyOptional() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitCompetitor_whenAllNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(null, null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for initCompetitor - Null/Blank/Empty String Handling
    // =====================================================================

    @Test
    public void testInitCompetitor_whenNullFirstName_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName(null);
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().firstName());
    }

    @Test
    public void testInitCompetitor_whenEmptyFirstName_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().firstName());
    }

    @Test
    public void testInitCompetitor_whenNullLastName_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName(null);
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().lastName());
    }

    @Test
    public void testInitCompetitor_whenNullMiddleNames_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames(null);
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().middleNames());
    }

    @Test
    public void testInitCompetitor_whenNullDateOfBirth_thenFormatsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(null);
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateOfBirth());
    }

    // =====================================================================
    // Tests for initCompetitor - Partial Input (Only Some Fields)
    // =====================================================================

    @Test
    public void testInitCompetitor_whenOnlyRequiredFields_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames(null);
        competitor.setDateOfBirth(null);
        competitor.setSapsaNumber(null);
        competitor.setCompetitorNumber(null);

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertEquals("Doe", result.get().lastName());
    }

    @Test
    public void testInitCompetitor_whenEmptyStagesList_thenIncludesEmptyList() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().stages().isEmpty());
    }

    // =====================================================================
    // Tests for initCompetitor - Full Input (All Fields)
    // =====================================================================

    @Test
    public void testInitCompetitor_whenCompleteData_thenReturnsCompleteRecord() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Alpha Club", "Handgun", "Open",
                "Major", "A", "95.50", "85.25", "2025-09-06 15:30");

        MatchStageCompetitorRecord stage1 = new MatchStageCompetitorRecord("Handgun", "Open",
                "Major", "A", 10, 9, 8, 7, 34, 0, 0, 0,
                "12.50", "2.72", "34.00", "100.00", "100.00", "2025-09-06 10:00");
        MatchStageCompetitorRecord stage2 = new MatchStageCompetitorRecord("Handgun", "Open",
                "Major", "A", 9, 8, 7, 6, 30, 0, 0, 0,
                "13.50", "2.22", "30.00", "88.24", "88.24", "2025-09-06 11:00");

        List<MatchStageCompetitorRecord> stages = List.of(stage1, stage2);

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertEquals("Doe", result.get().lastName());
        assertEquals("Michael", result.get().middleNames());
        assertNotNull(result.get().dateOfBirth());
        assertEquals(12345, result.get().sapsaNumber());
        assertEquals("COMP001", result.get().competitorNumber());
        assertEquals(2, result.get().stages().size());
    }

    @Test
    public void testInitCompetitor_whenMultipleStages_thenIncludesAllStages() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Smith");
        competitor.setMiddleNames("Elizabeth");
        competitor.setDateOfBirth(LocalDate.of(1990, 3, 20));
        competitor.setSapsaNumber(67890);
        competitor.setCompetitorNumber("COMP002");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Beta Club", "Rifle", "Standard",
                "Minor", "B", "120.00", "90.00", "2025-09-06 16:00");

        List<MatchStageCompetitorRecord> stages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MatchStageCompetitorRecord stage = new MatchStageCompetitorRecord("Rifle", "Standard",
                    "Minor", "B", 10, 9, 8, 7, 34, 0, 0, 0,
                    "12.50", "2.72", "34.00", "100.00", "100.00", "2025-09-06 1" + i + ":00");
            stages.add(stage);
        }

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().stages().size());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: String Length
    // =====================================================================

    @Test
    public void testInitCompetitor_whenVeryLongFirstName_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("A".repeat(300));
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(300, result.get().firstName().length());
    }

    @Test
    public void testInitCompetitor_whenUnicodeInNames_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("José");
        competitor.setLastName("García");
        competitor.setMiddleNames("Müller");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("José", result.get().firstName());
        assertEquals("García", result.get().lastName());
        assertEquals("Müller", result.get().middleNames());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testInitCompetitor_whenSpecialCharactersInNames_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John-Paul");
        competitor.setLastName("O'Connor");
        competitor.setMiddleNames("Van Der Berg");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John-Paul", result.get().firstName());
        assertEquals("O'Connor", result.get().lastName());
        assertEquals("Van Der Berg", result.get().middleNames());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Extreme Numbers
    // =====================================================================

    @Test
    public void testInitCompetitor_whenLargeSapsaNumber_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(999999999);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(999999999, result.get().sapsaNumber());
    }

    @Test
    public void testInitCompetitor_whenNullSapsaNumber_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(null);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().sapsaNumber());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Extreme Dates
    // =====================================================================

    @Test
    public void testInitCompetitor_whenHistoricalDateOfBirth_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1900, 1, 1));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateOfBirth());
    }

    @Test
    public void testInitCompetitor_whenRecentDateOfBirth_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(2020, 12, 31));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateOfBirth());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Null Fields with Full Data
    // =====================================================================

    @Test
    public void testInitCompetitor_whenAllFieldsExceptSapsa_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(null);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertNull(result.get().sapsaNumber());
    }

    @Test
    public void testInitCompetitor_whenAllFieldsExceptNumber_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber(null);

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertNull(result.get().competitorNumber());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: MatchCompetitorRecord Null Fields
    // =====================================================================

    @Test
    public void testInitCompetitor_whenNullFieldsInMatchRecord_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord(null, null, null,
                null, null, null, null, null);
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertNotNull(result.get().stages());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testInitCompetitor_whenLargeStageList_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");

        List<MatchStageCompetitorRecord> stages = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            MatchStageCompetitorRecord stage = new MatchStageCompetitorRecord("Handgun", "Open",
                    "Major", "A", 10, 9, 8, 7, 34, 0, 0, 0,
                    "12.50", "2.72", "34.00", "100.00", "100.00", "2025-09-06 10:00");
            stages.add(stage);
        }

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(50, result.get().stages().size());
    }

    // =====================================================================
    // Tests for initCompetitor - Edge Cases: Empty String Fields
    // =====================================================================

    @Test
    public void testInitCompetitor_whenEmptyCompetitorNumber_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("");

        MatchCompetitorRecord matchRecord = new MatchCompetitorRecord("Club", "Handgun", "Open",
                "Major", "A", "100.00", "50.00", "2025-09-06 10:00");
        List<MatchStageCompetitorRecord> stages = new ArrayList<>();

        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(competitor, matchRecord, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().competitorNumber());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Null Input Handling
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenNullCompetitor_thenReturnsEmptyOptional() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(null, matchCompetitorList);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_whenNullMatchCompetitorList_thenReturnsEmptyOptional() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_whenBothNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_whenEmptyMatchCompetitorList_thenReturnsEmptyOptional() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_whenNoMatchingCompetitor_thenReturnsEmptyOptional() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        Competitor otherCompetitor = new Competitor();
        otherCompetitor.setId(2L);
        otherCompetitor.setFirstName("Jane");
        otherCompetitor.setLastName("Smith");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(otherCompetitor);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Null/Blank/Empty Field Handling
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenNullMatch_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(null);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().clubName());
    }

    @Test
    public void testInitMatchCompetitor_whenNullClub_thenSetsEmptyClubName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setClub(null);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().clubName());
    }

    @Test
    public void testInitMatchCompetitor_whenNullFirearmType_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setFirearmType(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().firearmType().isEmpty());
    }

    @Test
    public void testInitMatchCompetitor_whenNullDivision_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setDivision(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().division().isEmpty());
    }

    @Test
    public void testInitMatchCompetitor_whenNullPowerFactor_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setPowerFactor(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().powerFactor().isEmpty());
    }

    @Test
    public void testInitMatchCompetitor_whenNullCategory_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setCompetitorCategory(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().competitorCategory().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Partial Input (Only Some Fields)
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenOnlyFirearmType_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(null);
        matchCompetitor.setPowerFactor(null);
        matchCompetitor.setCompetitorCategory(null);
        matchCompetitor.setMatchPoints(null);
        matchCompetitor.setMatchRanking(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().firearmType());
        assertFalse(result.get().firearmType().isEmpty());
    }

    @Test
    public void testInitMatchCompetitor_whenOnlyDivision_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setFirearmType(null);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Open Division", result.get().division());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Full Input (All Fields)
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenCompleteData_thenReturnsCompleteRecord() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");

        Club club = new Club();
        club.setId(1L);
        club.setName("Alpha Club");
        club.setAbbreviation("AC");

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setClub(club);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.SENIOR);
        matchCompetitor.setMatchPoints(java.math.BigDecimal.valueOf(95.50));
        matchCompetitor.setMatchRanking(java.math.BigDecimal.valueOf(85.25));
        matchCompetitor.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 30, 0));

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Alpha Club", result.get().clubName());
        assertNotNull(result.get().firearmType());
        assertEquals("Open Division", result.get().division());
        assertEquals("Major", result.get().powerFactor());
        assertEquals("Senior", result.get().competitorCategory());
        assertNotNull(result.get().matchPoints());
        assertNotNull(result.get().matchRanking());
        assertNotNull(result.get().dateEdited());
    }

    @Test
    public void testInitMatchCompetitor_whenMultipleCompetitorsInList_thenFindsCorrectOne() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");
        competitor1.setLastName("Doe");

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        competitor2.setLastName("Smith");

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(1L);
        matchCompetitor1.setCompetitor(competitor1);
        matchCompetitor1.setDivision(Division.OPEN);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(2L);
        matchCompetitor2.setCompetitor(competitor2);
        matchCompetitor2.setDivision(Division.STANDARD);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor1, matchCompetitor2);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor2, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Standard Division", result.get().division());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: Enum Values
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenAllFirearmTypes_thenProcesses() {
        // Arrange
        FirearmType[] firearmTypes = {
                FirearmType.HANDGUN,
                FirearmType.HANDGUN_22,
                FirearmType.RIFLE,
                FirearmType.SHOTGUN,
                FirearmType.PCC,
                FirearmType.MINI_RIFLE
        };

        // Act & Assert
        for (FirearmType type : firearmTypes) {
            Competitor competitor = new Competitor();
            competitor.setId(1L);
            competitor.setFirstName("John");
            competitor.setLastName("Doe");

            MatchCompetitor matchCompetitor = new MatchCompetitor();
            matchCompetitor.setId(1L);
            matchCompetitor.setCompetitor(competitor);
            matchCompetitor.setFirearmType(type);

            List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

            Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

            assertTrue(result.isPresent());
            assertNotNull(result.get().firearmType());
            assertFalse(result.get().firearmType().isEmpty());
        }
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: Numeric Values
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenZeroMatchPoints_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchPoints(java.math.BigDecimal.ZERO);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().matchPoints());
    }

    @Test
    public void testInitMatchCompetitor_whenNegativeMatchPoints_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchPoints(java.math.BigDecimal.valueOf(-10.5));

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().matchPoints());
    }

    @Test
    public void testInitMatchCompetitor_whenVeryLargeMatchPoints_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchPoints(java.math.BigDecimal.valueOf(999999.99));

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().matchPoints());
    }

    @Test
    public void testInitMatchCompetitor_whenNullMatchPoints_thenFormatsAsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchPoints(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().matchPoints());
    }

    @Test
    public void testInitMatchCompetitor_whenNullMatchRanking_thenFormatsAsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchRanking(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().matchRanking());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: Date Handling
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenNullDateEdited_thenFormatsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setDateEdited(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateEdited());
    }

    @Test
    public void testInitMatchCompetitor_whenHistoricalDate_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setDateEdited(LocalDateTime.of(1900, 1, 1, 0, 0, 0));

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateEdited());
    }

    @Test
    public void testInitMatchCompetitor_whenFutureDate_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setDateEdited(LocalDateTime.of(2099, 12, 31, 23, 59, 59));

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().dateEdited());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: List with Nulls
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenNullsInList_thenFiltersAndFinds() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setDivision(Division.OPEN);

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorList.add(null);
        matchCompetitorList.add(matchCompetitor);
        matchCompetitorList.add(null);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Open Division", result.get().division());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: Large List
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenLargeList_thenFindsCorrectOne() {
        // Arrange
        Competitor targetCompetitor = new Competitor();
        targetCompetitor.setId(50L);
        targetCompetitor.setFirstName("Target");
        targetCompetitor.setLastName("Competitor");

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("Competitor" + i);
            competitor.setLastName("Test");

            MatchCompetitor matchCompetitor = new MatchCompetitor();
            matchCompetitor.setId((long) i);
            matchCompetitor.setCompetitor(competitor);

            matchCompetitorList.add(matchCompetitor);
        }

        MatchCompetitor targetMatchCompetitor = new MatchCompetitor();
        targetMatchCompetitor.setId(50L);
        targetMatchCompetitor.setCompetitor(targetCompetitor);
        matchCompetitorList.set(50, targetMatchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(targetCompetitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initMatchCompetitor - Edge Cases: All Fields Null
    // =====================================================================

    @Test
    public void testInitMatchCompetitor_whenAllFieldsNull_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(1L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(null);
        matchCompetitor.setFirearmType(null);
        matchCompetitor.setDivision(null);
        matchCompetitor.setPowerFactor(null);
        matchCompetitor.setCompetitorCategory(null);
        matchCompetitor.setMatchPoints(null);
        matchCompetitor.setMatchRanking(null);
        matchCompetitor.setDateEdited(null);

        List<MatchCompetitor> matchCompetitorList = List.of(matchCompetitor);

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(competitor, matchCompetitorList);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().clubName());
        assertTrue(result.get().firearmType().isEmpty());
        assertTrue(result.get().division().isEmpty());
        assertTrue(result.get().powerFactor().isEmpty());
        assertTrue(result.get().competitorCategory().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Null Input Handling
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenNullCompetitor_thenReturnsEmptyList() {
        // Arrange
        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(null, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullMatchStageCompetitorList_thenReturnsEmptyList() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenBothNullParameters_thenReturnsEmptyList() {
        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenEmptyList_thenReturnsEmptyList() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNoMatchingCompetitor_thenReturnsEmptyList() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");
        competitor1.setLastName("Doe");

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        competitor2.setLastName("Smith");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor2);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor1, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Null Field Handling
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenNullFirearmType_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setFirearmType(null);
        msc.setScoreA(10);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().firearmType().isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullDivision_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setDivision(null);
        msc.setScoreA(10);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().division().isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullPowerFactor_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setPowerFactor(null);
        msc.setScoreA(10);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().powerFactor().isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullCompetitorCategory_thenSetsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setCompetitorCategory(null);
        msc.setScoreA(10);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().competitorCategory().isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullScores_thenIncludesNullScores() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(null);
        msc.setScoreB(null);
        msc.setScoreC(null);
        msc.setScoreD(null);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst().scoreA());
        assertNull(result.getFirst().scoreB());
        assertNull(result.getFirst().scoreC());
        assertNull(result.getFirst().scoreD());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullPenalties_thenIncludesNullPenalties() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setPoints(null);
        msc.setMisses(null);
        msc.setPenalties(null);
        msc.setProcedurals(null);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst().points());
        assertNull(result.getFirst().misses());
        assertNull(result.getFirst().penalties());
        assertNull(result.getFirst().procedurals());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullBigDecimals_thenFormatsAsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setTime(null);
        msc.setHitFactor(null);
        msc.setStagePoints(null);
        msc.setStagePercentage(null);
        msc.setStageRanking(null);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().time());
        assertNotNull(result.getFirst().hitFactor());
        assertNotNull(result.getFirst().stagePoints());
        assertNotNull(result.getFirst().stagePercentage());
        assertNotNull(result.getFirst().stageRanking());
    }

    @Test
    public void testInitMatchStageCompetitor_whenNullDateEdited_thenFormatsAsEmpty() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setDateEdited(null);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().dateEdited());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Partial Input
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenOnlyScores_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(10);
        msc.setScoreB(8);
        msc.setScoreC(5);
        msc.setScoreD(2);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.getFirst().scoreA());
        assertEquals(8, result.getFirst().scoreB());
        assertEquals(5, result.getFirst().scoreC());
        assertEquals(2, result.getFirst().scoreD());
    }

    @Test
    public void testInitMatchStageCompetitor_whenOnlyPerformanceMetrics_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setPoints(25);
        msc.setMisses(2);
        msc.setPenalties(1);
        msc.setProcedurals(0);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(25, result.getFirst().points());
        assertEquals(2, result.getFirst().misses());
        assertEquals(1, result.getFirst().penalties());
        assertEquals(0, result.getFirst().procedurals());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Full Input
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenCompleteData_thenReturnsCompleteRecord() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setFirearmType(FirearmType.HANDGUN);
        msc.setDivision(Division.OPEN);
        msc.setPowerFactor(PowerFactor.MAJOR);
        msc.setCompetitorCategory(CompetitorCategory.LADY);
        msc.setScoreA(10);
        msc.setScoreB(9);
        msc.setScoreC(8);
        msc.setScoreD(7);
        msc.setPoints(34);
        msc.setMisses(0);
        msc.setPenalties(0);
        msc.setProcedurals(0);
        msc.setTime(java.math.BigDecimal.valueOf(12.50));
        msc.setHitFactor(java.math.BigDecimal.valueOf(2.72));
        msc.setStagePoints(java.math.BigDecimal.valueOf(100.00));
        msc.setStagePercentage(java.math.BigDecimal.valueOf(100.00));
        msc.setStageRanking(java.math.BigDecimal.valueOf(100.00));
        msc.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().firearmType());
        assertEquals("Open Division", result.getFirst().division());
        assertEquals("Major", result.getFirst().powerFactor());
        assertEquals("Lady", result.getFirst().competitorCategory());
        assertEquals(10, result.getFirst().scoreA());
        assertEquals(9, result.getFirst().scoreB());
        assertEquals(8, result.getFirst().scoreC());
        assertEquals(7, result.getFirst().scoreD());
        assertEquals(34, result.getFirst().points());
        assertNotNull(result.getFirst().time());
        assertNotNull(result.getFirst().hitFactor());
        assertNotNull(result.getFirst().dateEdited());
    }

    @Test
    public void testInitMatchStageCompetitor_whenMultipleStages_thenReturnsAll() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);
            msc.setScoreA(10 - i);
            msc.setScoreB(9 - i);
            msc.setScoreC(8 - i);
            msc.setScoreD(7 - i);
            matchStageCompetitorList.add(msc);
        }

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: Enum Values
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenAllFirearmTypes_thenProcesses() {
        // Arrange
        FirearmType[] firearmTypes = {
                FirearmType.HANDGUN,
                FirearmType.HANDGUN_22,
                FirearmType.RIFLE,
                FirearmType.SHOTGUN,
                FirearmType.PCC,
                FirearmType.MINI_RIFLE
        };

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        // Act & Assert
        for (FirearmType type : firearmTypes) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId(1L);
            msc.setCompetitor(competitor);
            msc.setFirearmType(type);

            List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);
            List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.getFirst().firearmType());
            assertFalse(result.getFirst().firearmType().isEmpty());
        }
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: Numeric Values
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenZeroScores_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(0);
        msc.setScoreB(0);
        msc.setScoreC(0);
        msc.setScoreD(0);
        msc.setPoints(0);
        msc.setMisses(0);
        msc.setPenalties(0);
        msc.setProcedurals(0);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.getFirst().scoreA());
        assertEquals(0, result.getFirst().points());
    }

    @Test
    public void testInitMatchStageCompetitor_whenLargeScores_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(100);
        msc.setScoreB(100);
        msc.setScoreC(100);
        msc.setScoreD(100);
        msc.setPoints(500);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.getFirst().scoreA());
        assertEquals(500, result.getFirst().points());
    }

    @Test
    public void testInitMatchStageCompetitor_whenZeroBigDecimalValues_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setTime(java.math.BigDecimal.ZERO);
        msc.setHitFactor(java.math.BigDecimal.ZERO);
        msc.setStagePoints(java.math.BigDecimal.ZERO);
        msc.setStagePercentage(java.math.BigDecimal.ZERO);
        msc.setStageRanking(java.math.BigDecimal.ZERO);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().time());
        assertNotNull(result.getFirst().hitFactor());
    }

    @Test
    public void testInitMatchStageCompetitor_whenLargeBigDecimalValues_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setTime(java.math.BigDecimal.valueOf(999.99));
        msc.setHitFactor(java.math.BigDecimal.valueOf(99.9999));
        msc.setStagePoints(java.math.BigDecimal.valueOf(9999.99));
        msc.setStagePercentage(java.math.BigDecimal.valueOf(100.00));
        msc.setStageRanking(java.math.BigDecimal.valueOf(100.00));

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().time());
        assertNotNull(result.getFirst().hitFactor());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: Date Handling
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenHistoricalDate_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setDateEdited(LocalDateTime.of(1900, 1, 1, 0, 0, 0));

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().dateEdited());
    }

    @Test
    public void testInitMatchStageCompetitor_whenFutureDate_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setDateEdited(LocalDateTime.of(2099, 12, 31, 23, 59, 59));

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.getFirst().dateEdited());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: List Filtering
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenNullsInList_thenFiltersAndFinds() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(10);

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        matchStageCompetitorList.add(null);
        matchStageCompetitorList.add(msc);
        matchStageCompetitorList.add(null);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.getFirst().scoreA());
    }

    @Test
    public void testInitMatchStageCompetitor_whenMixedCompetitors_thenFiltersCorrectly() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");
        competitor1.setLastName("Doe");

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        competitor2.setLastName("Smith");

        MatchStageCompetitor msc1 = new MatchStageCompetitor();
        msc1.setId(1L);
        msc1.setCompetitor(competitor1);
        msc1.setScoreA(10);

        MatchStageCompetitor msc2 = new MatchStageCompetitor();
        msc2.setId(2L);
        msc2.setCompetitor(competitor2);
        msc2.setScoreA(8);

        MatchStageCompetitor msc3 = new MatchStageCompetitor();
        msc3.setId(3L);
        msc3.setCompetitor(competitor1);
        msc3.setScoreA(9);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc1, msc2, msc3);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor1, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10, result.getFirst().scoreA());
        assertEquals(9, result.get(1).scoreA());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenLargeList_thenFindsCorrectOnes() {
        // Arrange
        Competitor targetCompetitor = new Competitor();
        targetCompetitor.setId(50L);
        targetCompetitor.setFirstName("Target");
        targetCompetitor.setLastName("Competitor");

        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);

            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);
            msc.setScoreA(i);

            matchStageCompetitorList.add(msc);
        }

        // Add target competitor stages
        for (int i = 0; i < 3; i++) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId(100L + i);
            msc.setCompetitor(targetCompetitor);
            msc.setScoreA(50 + i);
            matchStageCompetitorList.add(msc);
        }

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(targetCompetitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    // =====================================================================
    // Tests for initMatchStageCompetitor - Edge Cases: All Fields Null
    // =====================================================================

    @Test
    public void testInitMatchStageCompetitor_whenAllFieldsNull_thenProcesses() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setFirearmType(null);
        msc.setDivision(null);
        msc.setPowerFactor(null);
        msc.setCompetitorCategory(null);
        msc.setScoreA(null);
        msc.setScoreB(null);
        msc.setScoreC(null);
        msc.setScoreD(null);
        msc.setPoints(null);
        msc.setMisses(null);
        msc.setPenalties(null);
        msc.setProcedurals(null);
        msc.setTime(null);
        msc.setHitFactor(null);
        msc.setStagePoints(null);
        msc.setStagePercentage(null);
        msc.setStageRanking(null);
        msc.setDateEdited(null);

        List<MatchStageCompetitor> matchStageCompetitorList = List.of(msc);

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(competitor, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().firearmType().isEmpty());
        assertTrue(result.getFirst().division().isEmpty());
        assertTrue(result.getFirst().powerFactor().isEmpty());
        assertTrue(result.getFirst().competitorCategory().isEmpty());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Null Input Handling
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenNullList_thenReturnsEmptyList() {
        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitorSet_whenEmptyList_thenReturnsEmptyList() {
        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Null Elements Handling
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenAllNullElements_thenReturnsEmptyList() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorList.add(null);
        matchCompetitorList.add(null);
        matchCompetitorList.add(null);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitorSet_whenSomeNullElements_thenFiltersNulls() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");
        competitor1.setLastName("Doe");

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        competitor2.setLastName("Smith");

        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(competitor1);

        MatchCompetitor mc2 = new MatchCompetitor();
        mc2.setId(2L);
        mc2.setCompetitor(competitor2);

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorList.add(null);
        matchCompetitorList.add(mc1);
        matchCompetitorList.add(null);
        matchCompetitorList.add(mc2);
        matchCompetitorList.add(null);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(competitor1, competitor2)));
    }

    @Test
    public void testGetCompetitorSet_whenNullCompetitorInMatchCompetitor_thenFiltersNull() {
        // Arrange
        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(null);

        List<MatchCompetitor> matchCompetitorList = List.of(mc1);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Single Element
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenSingleCompetitor_thenReturnsSingleElement() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(competitor));
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertEquals(competitor, firstResult);
        assertEquals("John", firstResult.getFirstName());
        assertEquals("Doe", firstResult.getLastName());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Multiple Elements
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenMultipleCompetitors_thenReturnsAll() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("FirstName" + i);
            competitor.setLastName("LastName" + i);

            MatchCompetitor mc = new MatchCompetitor();
            mc.setId((long) i);
            mc.setCompetitor(competitor);

            matchCompetitorList.add(mc);
        }

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        List<String> firstNames = result.stream().map(Competitor::getFirstName).toList();
        List<String> lastNames = result.stream().map(Competitor::getLastName).toList();
        for (int i = 0; i < 5; i++) {
            assertTrue(firstNames.contains("FirstName" + (i + 1)));
            assertTrue(lastNames.contains("LastName" + (i + 1)));
        }
    }

    // =====================================================================
    // Tests for getCompetitorSet - Partial Data
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenCompetitorsWithoutNames_thenReturnsAll() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("John");

        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(competitor1);

        MatchCompetitor mc2 = new MatchCompetitor();
        mc2.setId(2L);
        mc2.setCompetitor(competitor2);

        List<MatchCompetitor> matchCompetitorList = List.of(mc1, mc2);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        List<String> firstNames = result.stream().map(Competitor::getFirstName).toList();
        assertEquals(1, firstNames.stream().filter(Objects::isNull).count());
        assertTrue(firstNames.contains("John"));
    }

    @Test
    public void testGetCompetitorSet_whenCompetitorsWithNullFields_thenReturnsAll() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName(null);
        competitor.setLastName(null);
        competitor.setMiddleNames(null);
        competitor.setSapsaNumber(null);

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(competitor, result.stream().findFirst().orElse(null));
    }

    // =====================================================================
    // Tests for getCompetitorSet - Full Data
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenCompleteCompetitorData_thenReturnsComplete() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");
        competitor.setDateOfBirth(LocalDate.of(1980, 5, 15));

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(competitor));
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertEquals("John", firstResult.getFirstName());
        assertEquals("Doe", firstResult.getLastName());
        assertEquals("Michael", firstResult.getMiddleNames());
        assertEquals(12345, firstResult.getSapsaNumber());
        assertEquals("COMP001", firstResult.getCompetitorNumber());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Duplicates
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenDuplicateCompetitors_thenReturnsSingleInstance() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(competitor);

        MatchCompetitor mc2 = new MatchCompetitor();
        mc2.setId(2L);
        mc2.setCompetitor(competitor);

        MatchCompetitor mc3 = new MatchCompetitor();
        mc3.setId(3L);
        mc3.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc1, mc2, mc3);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(competitor));
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenLargeList_thenProcessesAll() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("FirstName" + i);
            competitor.setLastName("LastName" + i);

            MatchCompetitor mc = new MatchCompetitor();
            mc.setId((long) i);
            mc.setCompetitor(competitor);

            matchCompetitorList.add(mc);
        }

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenSpecialCharactersInNames_thenPreserves() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("O'Connor");
        competitor.setLastName("Van-Der-Berg");
        competitor.setMiddleNames("José-María");

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals("O'Connor", firstResult.getFirstName());
        assertEquals("Van-Der-Berg", firstResult.getLastName());
        assertEquals("José-María", firstResult.getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Unicode Characters
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenUnicodeCharactersInNames_thenPreserves() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Müller");
        competitor.setLastName("Søren");
        competitor.setMiddleNames("Ñoño");

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals("Müller", firstResult.getFirstName());
        assertEquals("Søren", firstResult.getLastName());
        assertEquals("Ñoño", firstResult.getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Long Names
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenVeryLongNames_thenPreserves() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("A".repeat(200));
        competitor.setLastName("B".repeat(200));
        competitor.setMiddleNames("C".repeat(200));

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals(200, firstResult.getFirstName().length());
        assertEquals(200, firstResult.getLastName().length());
        assertEquals(200, firstResult.getMiddleNames().length());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Empty Strings
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenEmptyStringNames_thenPreserves() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("");
        competitor.setLastName("");
        competitor.setMiddleNames("");

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Competitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals("", firstResult.getFirstName());
        assertEquals("", firstResult.getLastName());
        assertEquals("", firstResult.getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Extreme Numbers
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenExtremeNumbers_thenPreserves() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(Long.MAX_VALUE);
        competitor1.setFirstName("Max");
        competitor1.setSapsaNumber(Integer.MAX_VALUE);

        Competitor competitor2 = new Competitor();
        competitor2.setId(1L);
        competitor2.setFirstName("Min");
        competitor2.setSapsaNumber(Integer.MIN_VALUE);

        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(competitor1);

        MatchCompetitor mc2 = new MatchCompetitor();
        mc2.setId(2L);
        mc2.setCompetitor(competitor2);

        List<MatchCompetitor> matchCompetitorList = List.of(mc1, mc2);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().map(Competitor::getId).toList().contains(Long.MAX_VALUE));
        assertTrue(result.stream().map(Competitor::getSapsaNumber).toList().contains(Integer.MAX_VALUE));
        assertTrue(result.stream().map(Competitor::getSapsaNumber).toList().contains(Integer.MIN_VALUE));
    }

    // =====================================================================
    // Tests for getCompetitorSet - Edge Cases: Mixed Valid and Null
    // =====================================================================

    @Test
    public void testGetCompetitorSet_whenMixedValidAndNullCompetitors_thenFiltersCorrectly() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");

        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(competitor1);

        MatchCompetitor mc2 = new MatchCompetitor();
        mc2.setId(2L);
        mc2.setCompetitor(null);

        Competitor competitor3 = new Competitor();
        competitor3.setId(3L);
        competitor3.setFirstName("Jane");

        MatchCompetitor mc3 = new MatchCompetitor();
        mc3.setId(3L);
        mc3.setCompetitor(competitor3);

        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorList.add(mc1);
        matchCompetitorList.add(null);
        matchCompetitorList.add(mc2);
        matchCompetitorList.add(mc3);

        // Act
        Set<Competitor> result = ipscMatchService.getCompetitorSet(new HashSet<>(matchCompetitorList));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        List<String> firstNames = result.stream().map(Competitor::getFirstName).toList();
        assertTrue(firstNames.containsAll(List.of("John", "Jane")));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Null Input Handling
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenNullList_thenReturnsEmptyList() {
        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_whenEmptyList_thenReturnsEmptyList() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Null Elements Handling
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenAllNullElements_thenReturnsEmptyList() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();
        matchStageList.add(null);
        matchStageList.add(null);
        matchStageList.add(null);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_whenSomeNullElements_thenFiltersNulls() {
        // Arrange
        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(new ArrayList<>());

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(new ArrayList<>());

        List<IpscMatchStage> matchStageList = new ArrayList<>();
        matchStageList.add(null);
        matchStageList.add(stage1);
        matchStageList.add(null);
        matchStageList.add(stage2);
        matchStageList.add(null);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Empty because stages have no competitors
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Empty Competitor Lists
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenStagesWithoutCompetitors_thenReturnsEmptyList() {
        // Arrange
        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(new ArrayList<>());

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(new ArrayList<>());

        List<IpscMatchStage> matchStageList = List.of(stage1, stage2);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_whenNullCompetitorLists_thenReturnsEmptyList() {
        // Arrange
        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(null);

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(null);

        List<IpscMatchStage> matchStageList = List.of(stage1, stage2);

        // Act
        Set<MatchStageCompetitor> result =
                assertDoesNotThrow(() -> ipscMatchService.getMatchStageCompetitorSet(matchStageList));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Single Stage
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenSingleStageOneCompetitor_thenReturnsSingle() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setScoreA(10);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageCompetitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals(msc, firstResult);
        assertEquals(competitor, firstResult.getCompetitor());
    }

    @Test
    public void testGetMatchStageCompetitorList_whenSingleStageMultipleCompetitors_thenReturnsAll() {
        // Arrange
        List<MatchStageCompetitor> competitors = new ArrayList<>();
        for (int i1 = 1; i1 <= 5; i1++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i1);
            competitor.setFirstName("FirstName" + i1);

            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i1);
            msc.setCompetitor(competitor);
            msc.setScoreA(10 + i1);

            competitors.add(msc);
        }

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(competitors);

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        List<String> firstNames =
                result.stream().map(MatchStageCompetitor::getCompetitor).map(Competitor::getFirstName).toList();
        for (int i = 0; i < 5; i++) {
            assertTrue(firstNames.contains("FirstName" + (i + 1)));
        }
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Multiple Stages
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenMultipleStages_thenAggregatesAll() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        for (int stageNum = 1; stageNum <= 3; stageNum++) {
            List<MatchStageCompetitor> competitors = new ArrayList<>();
            for (int compNum = 1; compNum <= 2; compNum++) {
                Competitor competitor = new Competitor();
                competitor.setId(stageNum * 10L + compNum);
                competitor.setFirstName("Stage" + stageNum + "Comp" + compNum);

                MatchStageCompetitor msc = new MatchStageCompetitor();
                msc.setId(stageNum * 10L + compNum);
                msc.setCompetitor(competitor);
                msc.setScoreA(stageNum * compNum);

                competitors.add(msc);
            }

            IpscMatchStage stage = new IpscMatchStage();
            stage.setId((long) stageNum);
            stage.setMatchStageCompetitors(competitors);

            matchStageList.add(stage);
        }

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.size()); // 3 stages * 2 competitors each
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Mixed Scenarios
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenMixedEmptyAndPopulated_thenReturnsOnlyPopulated() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);

        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(new ArrayList<>());

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(List.of(msc));

        IpscMatchStage stage3 = new IpscMatchStage();
        stage3.setId(3L);
        stage3.setMatchStageCompetitors(new ArrayList<>());

        List<IpscMatchStage> matchStageList = List.of(stage1, stage2, stage3);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageCompetitor firstResult = result.stream().findFirst().orElse(null);
        assertEquals(msc, firstResult);
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Partial Data
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenCompetitorsWithPartialData_thenReturnsAll() {
        // Arrange
        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        // No name set

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        // No last name

        MatchStageCompetitor msc1 = new MatchStageCompetitor();
        msc1.setId(1L);
        msc1.setCompetitor(competitor1);
        msc1.setScoreA(null);

        MatchStageCompetitor msc2 = new MatchStageCompetitor();
        msc2.setId(2L);
        msc2.setCompetitor(competitor2);
        msc2.setScoreB(8);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc1, msc2));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        List<String> firstNames = result.stream().map(MatchStageCompetitor::getCompetitor).map(Competitor::getFirstName).toList();
        assertEquals(1, firstNames.stream().filter(Objects::isNull).count());
        assertTrue(firstNames.contains("Jane"));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Full Data
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenCompleteData_thenReturnsComplete() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Michael");
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("COMP001");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setFirearmType(FirearmType.HANDGUN);
        msc.setDivision(Division.OPEN);
        msc.setPowerFactor(PowerFactor.MAJOR);
        msc.setCompetitorCategory(CompetitorCategory.JUNIOR);
        msc.setScoreA(10);
        msc.setScoreB(9);
        msc.setScoreC(8);
        msc.setScoreD(7);
        msc.setPoints(34);
        msc.setMisses(0);
        msc.setPenalties(0);
        msc.setProcedurals(0);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchStageCompetitor firstResult = result.stream().findFirst().orElse(null);
        assertNotNull(firstResult);
        assertEquals("John", firstResult.getCompetitor().getFirstName());
        assertEquals("Doe", firstResult.getCompetitor().getLastName());
        assertEquals(FirearmType.HANDGUN, firstResult.getFirearmType());
        assertEquals(Division.OPEN, firstResult.getDivision());
        assertEquals(10, firstResult.getScoreA());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenLargeNumberOfStages_thenProcessesAll() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("Competitor" + i);

            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);
            msc.setScoreA(i);

            IpscMatchStage stage = new IpscMatchStage();
            stage.setId((long) i);
            stage.setMatchStageCompetitors(List.of(msc));

            matchStageList.add(stage);
        }

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(50, result.size());
    }

    @Test
    public void testGetMatchStageCompetitorList_whenManyCompetitorsPerStage_thenProcessesAll() {
        // Arrange
        List<MatchStageCompetitor> competitors = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("Competitor" + i);

            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);

            competitors.add(msc);
        }

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(competitors);

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Duplicates
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenSameCompetitorInMultipleStages_thenReturnsAll() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        List<IpscMatchStage> matchStageList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);
            msc.setScoreA(10 * i);

            IpscMatchStage stage = new IpscMatchStage();
            stage.setId((long) i);
            stage.setMatchStageCompetitors(List.of(msc));

            matchStageList.add(stage);
        }

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        // Same competitor but different MatchStageCompetitor instances
        assertTrue(result.stream().allMatch(m -> m.getCompetitor().equals(competitor)));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenSpecialCharactersInData_thenPreserves() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("O'Connor");
        competitor.setLastName("Van-Der-Berg");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);
        msc.setDivision(Division.PCC_OPTICS);
        msc.setPowerFactor(PowerFactor.MINOR);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        List<String> firstNames =
                result.stream().map(MatchStageCompetitor::getCompetitor).map(Competitor::getFirstName).toList();
        List<String> lastNames =
                result.stream().map(MatchStageCompetitor::getCompetitor).map(Competitor::getLastName).toList();
        assertTrue(firstNames.contains("O'Connor"));
        assertTrue(lastNames.contains("Van-Der-Berg"));
        assertTrue(result.stream().allMatch(m -> m.getDivision() == Division.PCC_OPTICS));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Null Competitors
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_whenNullCompetitorInMatchStageCompetitor_thenIncludes() {
        // Arrange
        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(null);
        msc.setScoreA(10);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Aggregation
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_aggregatesFromMultipleStagesTotalCorrectly() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();
        int totalExpected = 0;

        // Stage 1: 2 competitors
        List<MatchStageCompetitor> stage1Competitors = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) (10 + i));
            msc.setCompetitor(new Competitor());
            stage1Competitors.add(msc);
            totalExpected++;
        }

        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(stage1Competitors);
        matchStageList.add(stage1);

        // Stage 2: 0 competitors (empty)
        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(new ArrayList<>());
        matchStageList.add(stage2);

        // Stage 3: 5 competitors
        List<MatchStageCompetitor> stage3Competitors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) (30 + i));
            msc.setCompetitor(new Competitor());
            stage3Competitors.add(msc);
            totalExpected++;
        }

        IpscMatchStage stage3 = new IpscMatchStage();
        stage3.setId(3L);
        stage3.setMatchStageCompetitors(stage3Competitors);
        matchStageList.add(stage3);

        // Act
        Set<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorSet(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(totalExpected, result.size()); // 2 + 0 + 5 = 7
    }

    // =====================================================================
    // Tests for mapMatchResults - Valid Data Scenarios
    // =====================================================================

    @Test
    public void testMapMatchResults_whenValidData_thenReturnsIpscResponses() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();


        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        holder.setClubs(List.of(clubRequest));

        MatchRequest match = new MatchRequest();
        match.setMatchId(100);
        match.setMatchName("Match 1");
        match.setClubId(1);

        MatchRequest match2 = new MatchRequest();
        match2.setMatchId(200);
        match2.setMatchName("Match 2");
        match2.setClubId(1);
        holder.setMatches(List.of(match, match2));

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(10);
        stage1.setMatchId(100);

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(20);
        stage2.setMatchId(200);
        holder.setStages(List.of(stage1, stage2));

        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100, responses.getFirst().getMatch().getMatchId());
        assertEquals(1, responses.getFirst().getStages().size());
        assertEquals(10, responses.getFirst().getStages().getFirst().getStageId());

        assertEquals(200, responses.get(1).getMatch().getMatchId());
        assertEquals(1, responses.get(1).getStages().size());
        assertEquals(20, responses.get(1).getStages().getFirst().getStageId());
    }

    @Test
    public void testMapMatchResults_whenNullMatchList_thenReturnsEmptyList() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setMatches(null);
        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testMapMatchResults_whenNoMatchingClub_thenCreatesClubWithIdOnly() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setClubs(null);

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    public void testMapMatchResults_whenEmptyMatchList_thenReturnsEmptyList() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setMatches(new ArrayList<>());
        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testMapMatchResults_whenNullMatchName_thenProcessesWithoutName() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName(null);
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertNull(responses.getFirst().getMatch().getMatchName());
    }

    @Test
    public void testMapMatchResults_whenEmptyMatchName_thenProcessesWithEmptyName() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertEquals("", responses.getFirst().getMatch().getMatchName());
    }

    @Test
    public void testMapMatchResults_whenBlankMatchName_thenProcessesWithBlankName() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("   ");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertEquals("   ", responses.getFirst().getMatch().getMatchName());
    }

    @Test
    public void testMapMatchResults_whenNullStages_thenReturnsEmptyHolder() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(null);
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(0, responses.size());
        assertTrue(responseHolder.getIpscList().isEmpty());
    }

    @Test
    public void testMapMatchResults_whenNullMembers_thenProcessesWithoutMembers() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(null);
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertTrue(responses.getFirst().getMembers().isEmpty());
    }

    @Test
    public void testMapMatchResults_whenNullEnrolledMembers_thenReturnsEmptyHolder() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(null);
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertEquals(0, responses.size());
        assertTrue(responseHolder.getIpscList().isEmpty());
    }

    // =====================================================================
    // Tests for mapMatchResults - Partial Data Processing
    // =====================================================================

    @Test
    public void testMapMatchResults_whenPartialData_thenMapsAvailableData() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Partial Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        // Only stages, no members or scores
        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(200);
        stageRequest.setStageName("Stage 1");
        stageRequest.setMatchId(100);
        holder.setStages(List.of(stageRequest));

        holder.setClubs(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertNotNull(responses.getFirst().getMatch());
        assertEquals(1, responses.getFirst().getStages().size());
        assertTrue(responses.getFirst().getMembers().isEmpty());
        assertTrue(responses.getFirst().getScores().isEmpty());
    }

    @Test
    public void testMapMatchResults_whenPartialMembers_thenMapsOnlyMatchingMembers() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        // Members exist but not enrolled in this match
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        holder.setMembers(List.of(memberRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertTrue(responses.getFirst().getMembers().isEmpty());
    }

    // =====================================================================
    // Tests for mapMatchResults - Full Data Mapping
    // =====================================================================

    @Test
    public void testMapMatchResults_whenCompleteData_thenMapsAllData() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");
        holder.setClubs(List.of(clubRequest));

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Complete Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(200);
        stageRequest.setStageName("Stage 1");
        stageRequest.setMatchId(100);
        holder.setStages(List.of(stageRequest));

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(10);
        tagRequest.setTagName("RO");
        holder.setTags(List.of(tagRequest));

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        holder.setMembers(List.of(memberRequest));

        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(50);
        enrolledRequest.setMatchId(100);
        enrolledRequest.setCompetitorId(500);
        holder.setEnrolledMembers(List.of(enrolledRequest));

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(200);
        scoreRequest.setMemberId(50);
        scoreRequest.setFinalScore(95);
        holder.setScores(List.of(scoreRequest));

        holder.setClassifications(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getMatch());
        assertEquals(100, response.getMatch().getMatchId());
        assertEquals("Complete Match", response.getMatch().getMatchName());
        assertEquals(1, response.getStages().size());
        assertEquals(1, response.getTags().size());
        assertEquals(1, response.getMembers().size());
        assertEquals(1, response.getEnrolledMembers().size());
        assertEquals(1, response.getScores().size());
        assertNotNull(response.getClub());
    }

    // =====================================================================
    // Tests for mapMatchResults - Edge Cases
    // =====================================================================

    @Test
    public void testMapMatchResults_whenNullClubId_thenProcessesWithoutClubMatch() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(null);
        holder.setMatches(List.of(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        holder.setClubs(List.of(clubRequest));

        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
    }

    @Test
    public void testMapMatchResults_whenNullStagesButNonNullMatches_thenReturnsEmptyHolder() {        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest match = new MatchRequest();
        match.setMatchId(100);
        match.setMatchName("Match 1");
        match.setClubId(1);

        MatchRequest match2 = new MatchRequest();
        match2.setMatchId(101);
        match2.setMatchName("Match 2");
        match2.setClubId(1);
        holder.setMatches(List.of(match, match2));

        holder.setClubs(new ArrayList<>());
        holder.setStages(null);
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testMapMatchResults_whenStagesNotMatchingAnyMatch_thenIgnoresStages() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        // Stages belong to different match
        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(200);
        stageRequest.setStageName("Other Stage");
        stageRequest.setMatchId(999);
        holder.setStages(List.of(stageRequest));

        holder.setClubs(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(1, responses.size());
        assertTrue(responses.getFirst().getStages().isEmpty());
    }

    @Test
    public void testMapMatchResults_whenSpecialCharactersInMatchName_thenPreservesCharacters() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match & Co. (2025) - v2.0");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals("Test Match & Co. (2025) - v2.0", responses.getFirst().getMatch().getMatchName());
    }

    @Test
    public void testMapMatchResults_whenLargeNumberOfMatches_thenProcessesAllMatches() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        List<MatchRequest> matches = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            MatchRequest matchRequest = new MatchRequest();
            matchRequest.setMatchId(100 + i);
            matchRequest.setMatchName("Match " + i);
            matchRequest.setClubId(1);
            matches.add(matchRequest);
        }
        holder.setMatches(matches);

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = ipscMatchService.mapMatchResults(holder);

        // Assert
        assertNotNull(responseHolder);
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertEquals(10, responses.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(101 + i, responses.get(i).getMatch().getMatchId());
            assertEquals("Match " + (i + 1), responses.get(i).getMatch().getMatchName());
        }
    }

    // =====================================================================
    // Tests for generateIpscMatchRecordHolder - Output Verification Focus
    // =====================================================================

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullList_thenReturnsHolderWithEmptyMatches() {
        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.matches());
        assertTrue(result.matches().isEmpty());
        assertEquals(0, result.matches().size());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_verifyOutputStructure_thenContainsAllRequiredFields() {
        // Arrange


        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");
        club.setAbbreviation("TC");
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Complete Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 30, 0));
        match.setClub(club);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 45, 30));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify output structure
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());

        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record);

        // Verify all record fields are populated
        assertNotNull(record.name());
        assertNotNull(record.scheduledDate());
        assertNotNull(record.clubName());
        assertNotNull(record.matchFirearmType());
        assertNotNull(record.matchCategory());
        assertNotNull(record.competitors());
        assertNotNull(record.dateEdited());

        // Verify specific values
        assertEquals("Complete Match", record.name());
        assertEquals("Test Club (TC)", record.clubName());
        assertTrue(record.scheduledDate().contains("2025"));
        assertTrue(record.scheduledDate().contains("09"));
        assertTrue(record.scheduledDate().contains("06"));
        assertEquals("Handgun", record.matchFirearmType());
        assertEquals("League", record.matchCategory());
        assertTrue(record.dateEdited().contains("2025"));
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenCompetitorListIsNotNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify competitors list is never null
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());
        assertNotNull(result.matches().getFirst().competitors());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenNameIsNotNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName(null);
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify output converts null name to empty string
        assertNotNull(result);
        assertNotNull(result.matches());
        assertEquals(1, result.matches().size());
        assertNotNull(result.matches().getFirst().name());
        assertEquals("", result.matches().getFirst().name());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenScheduledDateFormatted() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Date Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 12, 25, 18, 30, 45));
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setDateEdited(null);

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify date is formatted correctly
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.scheduledDate());
        // Should be formatted as "2025-12-25 18:30"
        assertEquals("2025-12-25 18:30", record.scheduledDate());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenClubNameHandlesNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("No Club Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.RIFLE);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify club name defaults to empty string when club is null
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.clubName());
        assertEquals("", record.clubName());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenFirearmTypeEnumFormatted() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();
        FirearmType[] types = {FirearmType.HANDGUN, FirearmType.RIFLE, FirearmType.SHOTGUN, FirearmType.PCC};

        for (int i = 0; i < types.length; i++) {
            IpscMatch match = new IpscMatch();
            match.setId((long) (i + 1));
            match.setName("Match " + i);
            match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
            match.setClub(null);
            match.setMatchFirearmType(types[i]);
            match.setMatchCategory(MatchCategory.LEAGUE);
            match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
            matchList.add(match);
        }

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify enum values are formatted as strings
        assertNotNull(result);
        assertEquals(4, result.matches().size());

        assertEquals("Handgun", result.matches().get(0).matchFirearmType());
        assertEquals("Rifle", result.matches().get(1).matchFirearmType());
        assertEquals("Shotgun", result.matches().get(2).matchFirearmType());
        assertEquals("PCC", result.matches().get(3).matchFirearmType());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenMatchCategoryEnumFormatted() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();
        MatchCategory[] categories = {MatchCategory.LEAGUE, MatchCategory.CLUB_SHOOT};

        for (int i = 0; i < categories.length; i++) {
            IpscMatch match = new IpscMatch();
            match.setId((long) (i + 1));
            match.setName("Match " + i);
            match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
            match.setClub(null);
            match.setMatchFirearmType(FirearmType.HANDGUN);
            match.setMatchCategory(categories[i]);
            match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
            matchList.add(match);
        }

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify enum values are formatted as strings
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertEquals("League", result.matches().get(0).matchCategory());
        assertEquals("Club Shoot", result.matches().get(1).matchCategory());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenDateEditedFormatted() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Edited Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 7, 14, 22, 55));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify date edited is formatted correctly
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.dateEdited());
        assertEquals("2025-09-07 14:22", record.dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_whenNullDateEdited_thenFormatsAsEmpty() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("No Edit Date");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(null);

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify null date is handled
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.dateEdited());
        assertEquals("", record.dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_whenNullScheduledDate_thenFormatsAsEmpty() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("No Scheduled Date");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify null scheduled date is handled
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.scheduledDate());
        assertEquals("", record.scheduledDate());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_whenMultipleMatches_thenPreservesOrder() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            IpscMatch match = new IpscMatch();
            match.setId((long) i);
            match.setName("Match " + i);
            match.setScheduledDate(LocalDateTime.of(2025, 9, (5 + i), 10, 0, 0));
            match.setClub(null);
            match.setMatchFirearmType(FirearmType.HANDGUN);
            match.setMatchCategory(MatchCategory.LEAGUE);
            match.setDateEdited(LocalDateTime.of(2025, 9, (5 + i), 15, 0, 0));
            matchList.add(match);
        }

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify order is preserved
        assertNotNull(result);
        assertEquals(5, result.matches().size());
        for (int i = 0; i < 5; i++) {
            assertEquals("Match " + (i + 1), result.matches().get(i).name());
        }
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_whenNullInInputList_thenFiltersNulls() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match);

        matchList.add(null);

        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify nulls are filtered out
        assertNotNull(result);
        assertEquals(2, result.matches().size());
        assertEquals("Match 1", result.matches().get(0).name());
        assertEquals("Match 2", result.matches().get(1).name());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenHolderRecordIsNotNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify record holder itself is never null
        assertNotNull(result);
        assertNotNull(result.matches());
        assertFalse(result.matches().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenNameNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with a null name
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName(null);
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match);

        // Match with an empty name
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Match with normal name
        IpscMatch match3 = new IpscMatch();
        match3.setId(3L);
        match3.setName("Normal Name");
        match3.setScheduledDate(LocalDateTime.of(2025, 9, 8, 10, 0, 0));
        match3.setClub(null);
        match3.setMatchFirearmType(FirearmType.SHOTGUN);
        match3.setMatchCategory(MatchCategory.LEAGUE);
        match3.setDateEdited(LocalDateTime.of(2025, 9, 8, 15, 0, 0));
        matchList.add(match3);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify all names are not null (nulls converted to empty strings)
        assertNotNull(result);
        assertEquals(3, result.matches().size());

        assertNotNull(result.matches().get(0).name());
        assertEquals("", result.matches().get(0).name());

        assertNotNull(result.matches().get(1).name());
        assertEquals("", result.matches().get(1).name());

        assertNotNull(result.matches().get(2).name());
        assertEquals("Normal Name", result.matches().get(2).name());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenClubNameNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with null club
        IpscMatch match1 = new IpscMatch();
        match1.setId(1L);
        match1.setName("Match 1");
        match1.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match1.setClub(null);
        match1.setMatchFirearmType(FirearmType.HANDGUN);
        match1.setMatchCategory(MatchCategory.LEAGUE);
        match1.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match1);

        // Match with club
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");
        club.setAbbreviation("TC");
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(club);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify club names are never null
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertNotNull(result.matches().get(0).clubName());
        assertEquals("", result.matches().get(0).clubName());

        assertNotNull(result.matches().get(1).clubName());
        assertEquals("Test Club (TC)", result.matches().get(1).clubName());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenScheduledDateNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with null scheduled date
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(null);
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match);

        // Match with scheduled date
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 14, 30, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify scheduled dates are never null
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertNotNull(result.matches().get(0).scheduledDate());
        assertEquals("", result.matches().get(0).scheduledDate());

        assertNotNull(result.matches().get(1).scheduledDate());
        assertEquals("2025-09-07 14:30", result.matches().get(1).scheduledDate());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenDateEditedNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with null date edited
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(null);
        matchList.add(match);

        // Match with date edited
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 16, 45, 30));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify date edited is never null
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertNotNull(result.matches().get(0).dateEdited());
        assertEquals("", result.matches().get(0).dateEdited());

        assertNotNull(result.matches().get(1).dateEdited());
        assertEquals("2025-09-07 16:45", result.matches().get(1).dateEdited());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenFirearmTypeNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with a null firearm type
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match);

        // Match with a firearm type
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.PCC);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify firearm types are never null
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertNotNull(result.matches().get(0).matchFirearmType());
        assertEquals("", result.matches().get(0).matchFirearmType());

        assertNotNull(result.matches().get(1).matchFirearmType());
        assertEquals("PCC", result.matches().get(1).matchFirearmType());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenMatchCategoryNeverNull() {
        // Arrange
        List<IpscMatch> matchList = new ArrayList<>();

        // Match with null category
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Match 1");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(null);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
        matchList.add(match);

        // Match with category
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 7, 10, 0, 0));
        match2.setClub(null);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.LEAGUE);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 7, 15, 0, 0));
        matchList.add(match2);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify categories are never null
        assertNotNull(result);
        assertEquals(2, result.matches().size());

        assertNotNull(result.matches().get(0).matchCategory());
        assertEquals("", result.matches().get(0).matchCategory());

        assertNotNull(result.matches().get(1).matchCategory());
        assertEquals("League", result.matches().get(1).matchCategory());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_outputVerification_thenCompetitorsNeverNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setClub(null);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert - Verify competitors list is never null, always an empty list if no competitors
        assertNotNull(result);
        IpscMatchRecord record = result.matches().getFirst();
        assertNotNull(record.competitors());
        assertTrue(record.competitors().isEmpty());
        assertEquals(0, record.competitors().size());
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getStages().size());
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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        ipscResponse.setScores(null);
        ipscResponse.setMembers(new ArrayList<>());

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Existing Club");
        existingClub.setAbbreviation("ABC");

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Match with Existing Club");

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    @Disabled
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
        when(matchEntityService.findMatchByNameAndScheduledDate(eq("Match with Existing Club"), isNull()))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertTrue(existingMatch.getDateUpdated().isBefore(scoreResponse.getLastModified()));
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Match with Existing Club", result.get().getMatch().getName());
    }

    @Disabled
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
        when(matchEntityService.findMatchByNameAndScheduledDate(eq("Match with Existing Club"), isNull()))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getStages().size());
    }

    @Disabled
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

        when(matchEntityService.findMatchByNameAndScheduledDate(eq("Existing Match"), isNull()))
                .thenReturn(Optional.of(existingMatch));

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    // =====================================================================
    // Tests for initStages
    // =====================================================================

    @Test
    public void initStages_whenMatchOrStagesAreNull_thenReturnsEmptyList() {
        assertTrue(ipscMatchService.initStages(null, List.of(new StageResponse())).isEmpty());
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        assertTrue(ipscMatchService.initStages(matchDto, null).isEmpty());
    }

    @Test
    public void initStages_whenStageResponsesContainNullEntries_thenMapsOnlyValidStages() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        StageResponse stageResponse = new StageResponse();
        stageResponse.setMatchId(100);
        stageResponse.setStageId(21);
        stageResponse.setStageName("Stage 21");
        stageResponse.setMaxPoints(120);

        List<StageResponse> stageResponses = new ArrayList<>();
        stageResponses.add(stageResponse);
        stageResponses.add(null);

        List<MatchStageDto> stages = ipscMatchService.initStages(matchDto, stageResponses);

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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        assertTrue(ipscMatchService.initCompetitors(matchResults, null).isEmpty());
        assertTrue(ipscMatchService.initCompetitors(matchResultsWithoutMatch, new IpscResponse()).isEmpty());
    }

    @Test
    public void initCompetitors_whenScoresContainDifferentMatchesOrNonPositiveValues_thenReturnsOnlyScoredCompetitorsForCurrentMatch() {
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);

        ScoreResponse s1 = new ScoreResponse();
        s1.setMatchId(100);
        s1.setStageId(21);
        s1.setMemberId(1);
        s1.setFinalScore(null);
        ScoreResponse s2 = new ScoreResponse();
        s2.setMatchId(100);
        s2.setStageId(21);
        s2.setMemberId(2);
        s2.setFinalScore(0);
        ScoreResponse s3 = new ScoreResponse();
        s3.setMatchId(100);
        s3.setStageId(21);
        s3.setMemberId(3);
        s3.setFinalScore(95);
        ScoreResponse s4 = new ScoreResponse();
        s4.setMatchId(999);
        s4.setStageId(21);
        s4.setMemberId(4);
        s4.setFinalScore(80);
        ScoreResponse s5 = new ScoreResponse();
        s5.setMatchId(100);
        s5.setStageId(21);
        s5.setMemberId(5);
        s5.setFinalScore(70);
        ipscResponse.setScores(List.of(s1, s2, s3, s4, s5));

        MemberResponse m1 = new MemberResponse();
        m1.setMemberId(1);
        m1.setFirstName("Ignored");
        m1.setLastName("Null");
        m1.setIsRegisteredForMatch(true);
        MemberResponse m2 = new MemberResponse();
        m2.setMemberId(2);
        m2.setFirstName("Ignored");
        m2.setLastName("Zero");
        m2.setIsRegisteredForMatch(true);
        MemberResponse m3 = new MemberResponse();
        m3.setMemberId(3);
        m3.setFirstName("Jane");
        m3.setLastName("Doe");
        m3.setIsRegisteredForMatch(true);
        MemberResponse m4 = new MemberResponse();
        m4.setMemberId(4);
        m4.setFirstName("Skip");
        m4.setLastName("Me");
        m4.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(m1, m2, m3, m4));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());

        List<CompetitorDto> competitors = ipscMatchService.initCompetitors(
                new MatchResultsDto(matchDto),
                ipscResponse
        );

        assertEquals(1, competitors.size());
        assertEquals(3, competitors.getFirst().getIndex());
        assertEquals("Jane", competitors.getFirst().getFirstName());
        assertEquals("Doe", competitors.getFirst().getLastName());
    }

    @Test
    public void initCompetitors_whenScoresOrMembersAreNull_thenReturnsEmptyList() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscResponse nullScoresResponse = new IpscResponse();
        MatchResponse matchResponse1 = new MatchResponse();
        matchResponse1.setMatchId(100);
        matchResponse1.setMatchName("Match 100");
        nullScoresResponse.setMatch(matchResponse1);
        nullScoresResponse.setScores(null);
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(1);
        memberResponse.setFirstName("Jane");
        memberResponse.setLastName("Doe");
        memberResponse.setIsRegisteredForMatch(true);
        nullScoresResponse.setMembers(List.of(memberResponse));

        IpscResponse nullMembersResponse = new IpscResponse();
        MatchResponse matchResponse2 = new MatchResponse();
        matchResponse2.setMatchId(100);
        matchResponse2.setMatchName("Match 100");
        nullMembersResponse.setMatch(matchResponse2);
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(21);
        scoreResponse.setMemberId(1);
        scoreResponse.setFinalScore(80);
        nullMembersResponse.setScores(List.of(scoreResponse));
        nullMembersResponse.setMembers(null);

        assertTrue(ipscMatchService.initCompetitors(matchResults, nullScoresResponse).isEmpty());
        assertTrue(ipscMatchService.initCompetitors(matchResults, nullMembersResponse).isEmpty());
    }

    @Test
    public void initCompetitors_whenScoresAndMembersAreEmpty_thenReturnsEmptyList() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        List<CompetitorDto> competitors = ipscMatchService.initCompetitors(matchResults, ipscResponse);

        assertNotNull(competitors);
        assertTrue(competitors.isEmpty());
    }

    @Test
    public void initCompetitors_whenMemberHasMultipleValidScores_thenReturnsCompetitorOnce() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ScoreResponse s1 = new ScoreResponse();
        s1.setMatchId(100);
        s1.setStageId(21);
        s1.setMemberId(9);
        s1.setFinalScore(120);
        ScoreResponse s2 = new ScoreResponse();
        s2.setMatchId(100);
        s2.setStageId(22);
        s2.setMemberId(9);
        s2.setFinalScore(130);
        ipscResponse.setScores(List.of(s1, s2));
        MemberResponse m1 = new MemberResponse();
        m1.setMemberId(9);
        m1.setFirstName("Sam");
        m1.setLastName("Shooter");
        m1.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(m1));

        List<CompetitorDto> competitors = ipscMatchService.initCompetitors(matchResults, ipscResponse);

        assertEquals(1, competitors.size());
        assertEquals(9, competitors.getFirst().getIndex());
        assertEquals("Sam", competitors.getFirst().getFirstName());
        assertEquals("Shooter", competitors.getFirst().getLastName());
    }

    @Test
    public void initCompetitors_whenMatchResponseIsNullWithPopulatedScoresAndMembers_thenThrowsNullPointerException() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(null);
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(21);
        scoreResponse.setMemberId(1);
        scoreResponse.setFinalScore(90);
        ipscResponse.setScores(List.of(scoreResponse));
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(1);
        memberResponse.setFirstName("Jane");
        memberResponse.setLastName("Doe");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));

        assertThrows(NullPointerException.class,
                () -> ipscMatchService.initCompetitors(matchResults, ipscResponse));
    }

    // =====================================================================
    // Tests for initScores
    // =====================================================================

    @Test
    public void initScores_whenResponseIsNull_thenLeavesCollectionsEmpty() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        assertDoesNotThrow(() -> ipscMatchService.initScores(matchResults, null));

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenScoresAndMembersAreEmpty_thenKeepsCollectionsEmpty() {
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        matchResults.setStages(new ArrayList<>());

        ipscMatchService.initScores(matchResults, ipscResponse);

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenScoreFieldsArePartial_thenBuildsDtosFromAvailableValues() {
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);

        ScoreResponse s1 = new ScoreResponse();
        s1.setMatchId(100);
        s1.setStageId(10);
        s1.setMemberId(1);
        s1.setFinalScore(null);
        ScoreResponse s2 = new ScoreResponse();
        s2.setMatchId(100);
        s2.setStageId(10);
        s2.setMemberId(2);
        s2.setFinalScore(0);
        ScoreResponse s3 = new ScoreResponse();
        s3.setMatchId(100);
        s3.setStageId(10);
        s3.setMemberId(3);
        s3.setFinalScore(150);
        ScoreResponse s4 = new ScoreResponse();
        s4.setMatchId(999);
        s4.setStageId(10);
        s4.setMemberId(4);
        s4.setFinalScore(200);
        ipscResponse.setScores(List.of(s1, s2, s3, s4));

        MemberResponse m1 = new MemberResponse();
        m1.setMemberId(1);
        m1.setFirstName("Ignored");
        m1.setLastName("Null");
        m1.setIsRegisteredForMatch(true);
        MemberResponse m2 = new MemberResponse();
        m2.setMemberId(2);
        m2.setFirstName("Ignored");
        m2.setLastName("Zero");
        m2.setIsRegisteredForMatch(true);
        MemberResponse m3 = new MemberResponse();
        m3.setMemberId(3);
        m3.setFirstName("Jane");
        m3.setLastName("Doe");
        m3.setIsRegisteredForMatch(true);
        MemberResponse m4 = new MemberResponse();
        m4.setMemberId(4);
        m4.setFirstName("Skip");
        m4.setLastName("Me");
        m4.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(m1, m2, m3, m4));

        EnrolledResponse e1 = new EnrolledResponse();
        e1.setMemberId(1);
        e1.setCompetitorId(1);
        e1.setMatchId(100);
        EnrolledResponse e2 = new EnrolledResponse();
        e2.setMemberId(2);
        e2.setCompetitorId(2);
        e2.setMatchId(100);
        EnrolledResponse e3 = new EnrolledResponse();
        e3.setMemberId(3);
        e3.setCompetitorId(3);
        e3.setMatchId(100);
        EnrolledResponse e4 = new EnrolledResponse();
        e4.setMemberId(4);
        e4.setCompetitorId(4);
        e4.setMatchId(999);
        ipscResponse.setEnrolledMembers(List.of(e1, e2, e3, e4));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(10);
        stageDto.setStageNumber(10);
        stageDto.setId(10L);
        stageDto.setMaxPoints(200);
        matchResults.setStages(List.of(stageDto));

        ipscMatchService.initScores(matchResults, ipscResponse);

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
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(5);
        scoreResponse.setFinalScore(100);

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(5);
        memberResponse.setFirstName("Test");
        memberResponse.setLastName("User");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(5);
        enrolledResponse.setCompetitorId(5);
        enrolledResponse.setMatchId(100);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(10);
        stageDto.setStageNumber(10);
        stageDto.setId(10L);
        stageDto.setMaxPoints(150);
        matchResults.setStages(List.of(stageDto));

        assertDoesNotThrow(() -> ipscMatchService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getCompetitors().size());
        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getTime().compareTo(BigDecimal.ZERO));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getHitFactor().compareTo(BigDecimal.ZERO));
        assertEquals(0, matchResults.getMatchStageCompetitors().getFirst().getDeductionPercentage().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void initScores_whenFieldsAreComplete_thenBuildsMatchAndStageScores() {
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
        memberResponse.setIcsAlias("15000");
        memberResponse.setIsRegisteredForMatch(true);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(9);
        enrolledResponse.setCompetitorId(9);
        enrolledResponse.setMatchId(100);
        enrolledResponse.setRefNo("BBB");

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(memberResponse));
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(21);
        stageDto.setStageNumber(21);
        stageDto.setId(21L);
        stageDto.setMaxPoints(100);
        matchResults.setStages(List.of(stageDto));

        ipscMatchService.initScores(matchResults, ipscResponse);

        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals("Alice", matchResults.getCompetitors().getFirst().getFirstName());
        assertEquals("Jones", matchResults.getCompetitors().getFirst().getLastName());
        assertEquals("1990-01-15", matchResults.getCompetitors().getFirst().getDateOfBirth().toString());
        assertEquals("15000", matchResults.getCompetitors().getFirst().getCompetitorNumber());
        assertEquals(15000, matchResults.getCompetitors().getFirst().getSapsaNumber());
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
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(6);
        memberResponse.setFirstName("   ");
        memberResponse.setLastName("   ");
        memberResponse.setIsRegisteredForMatch(true);

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(6);
        scoreResponse.setFinalScore(150);
        ipscResponse.setScores(List.of(scoreResponse));
        ipscResponse.setMembers(List.of(memberResponse));
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(6);
        enrolledResponse.setCompetitorId(6);
        enrolledResponse.setMatchId(100);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(10);
        stageDto.setStageNumber(10);
        stageDto.setId(10L);
        matchResults.setStages(List.of(stageDto));

        assertDoesNotThrow(() -> ipscMatchService.initScores(matchResults, ipscResponse));

        assertEquals(1, matchResults.getCompetitors().size());
        assertEquals("", matchResults.getCompetitors().getFirst().getFirstName());
        assertEquals("", matchResults.getCompetitors().getFirst().getLastName());
    }

    @Test
    public void initScores_whenMemberHasMultipleStageScores_thenAggregatesMatchPointsAndUsesLatestEditDate() {
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
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(List.of(stageOneScore, stageTwoScore));
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(9);
        memberResponse.setFirstName("Sam");
        memberResponse.setLastName("Shooter");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(9);
        enrolledResponse.setCompetitorId(9);
        enrolledResponse.setMatchId(100);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto1 = new MatchStageDto();
        stageDto1.setMatch(matchResults.getMatch());
        stageDto1.setIndex(10);
        stageDto1.setStageNumber(10);
        stageDto1.setId(10L);
        MatchStageDto stageDto2 = new MatchStageDto();
        stageDto2.setMatch(matchResults.getMatch());
        stageDto2.setIndex(11);
        stageDto2.setStageNumber(11);
        stageDto2.setId(11L);
        matchResults.setStages(List.of(
                stageDto1,
                stageDto2
        ));

        ipscMatchService.initScores(matchResults, ipscResponse);

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
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(999);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(9);
        scoreResponse.setFinalScore(100);
        ipscResponse.setScores(List.of(scoreResponse));
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(9);
        memberResponse.setFirstName("Sam");
        memberResponse.setLastName("Shooter");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(9);
        enrolledResponse.setCompetitorId(9);
        enrolledResponse.setMatchId(999);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(10);
        stageDto.setStageNumber(10);
        stageDto.setId(10L);
        matchResults.setStages(List.of(stageDto));

        ipscMatchService.initScores(matchResults, ipscResponse);

        assertTrue(matchResults.getCompetitors().isEmpty());
        assertTrue(matchResults.getMatchCompetitors().isEmpty());
        assertTrue(matchResults.getMatchStageCompetitors().isEmpty());
    }

    @Test
    public void initScores_whenNumericScoreFieldsAreInvalid_thenDefaultsNumericOutputsToZero() {
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(10);
        scoreResponse.setMemberId(5);
        scoreResponse.setFinalScore(50);
        scoreResponse.setTime("bad-time");
        scoreResponse.setHitFactor("bad-hf");
        scoreResponse.setDeductionPercentage("bad-deduction");

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setScores(List.of(scoreResponse));
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(5);
        memberResponse.setFirstName("Casey");
        memberResponse.setLastName("Edge");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(5);
        enrolledResponse.setCompetitorId(5);
        enrolledResponse.setMatchId(100);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(1L);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchResults.getMatch());
        stageDto.setIndex(10);
        stageDto.setStageNumber(10);
        stageDto.setId(10L);
        stageDto.setMaxPoints(100);
        matchResults.setStages(List.of(stageDto));

        ipscMatchService.initScores(matchResults, ipscResponse);

        assertFalse(matchResults.getMatchStageCompetitors().isEmpty());
        MatchStageCompetitorDto competitorDto = matchResults.getMatchStageCompetitors().getFirst();
        assertEquals(0, competitorDto.getTime().compareTo(BigDecimal.ZERO));
        assertEquals(0, competitorDto.getHitFactor().compareTo(BigDecimal.ZERO));
        assertEquals(0, competitorDto.getDeductionPercentage().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void initMatchResults_whenScoresMembersAndEnrollmentProvided_thenBuildsCompetitorAndScoreCollections() {
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(100);
        scoreResponse.setStageId(21);
        scoreResponse.setMemberId(9);
        scoreResponse.setFinalScore(95);

        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Match 100");
        ipscResponse.setMatch(matchResponse);
        StageResponse stageResponse = new StageResponse();
        stageResponse.setMatchId(100);
        stageResponse.setStageId(21);
        stageResponse.setStageName("Stage 21");
        ipscResponse.setStages(List.of(stageResponse));
        ipscResponse.setScores(List.of(scoreResponse));
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(9);
        memberResponse.setFirstName("Bob");
        memberResponse.setLastName("Smith");
        memberResponse.setIsRegisteredForMatch(true);
        ipscResponse.setMembers(List.of(memberResponse));
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setMemberId(9);
        enrolledResponse.setCompetitorId(9);
        enrolledResponse.setMatchId(100);
        ipscResponse.setEnrolledMembers(List.of(enrolledResponse));

        Optional<MatchResultsDto> result = ipscMatchService.initMatchResults(ipscResponse);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCompetitors().size());
        assertEquals(1, result.get().getMatchCompetitors().size());
        assertFalse(result.get().getMatchStageCompetitors().isEmpty());
    }

    // =====================================================================
    // Tests for initMatchResults - Null Input Handling (from ipscMatchServiceTest)
    // =====================================================================

    @Test
    public void testInitMatchResults_whenIpscResponseIsNull_thenReturnsEmptyOptional() {
        // Act
        var result = ipscMatchService.initMatchResults(null);

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
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(null);
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("Test Match");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitMatchResults_whenMembersAreNull_thenProcessesWithoutMembers() {
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
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName(null);
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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
        clubResponse.setClubName(null);
        ipscResponse.setClub(clubResponse);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(100);
        matchResponse.setMatchName("   ");
        ipscResponse.setMatch(matchResponse);

        ipscResponse.setStages(new ArrayList<>());
        ipscResponse.setScores(null);
        ipscResponse.setMembers(null);

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("Simple Match", result.get().getMatch().getName());
        assertTrue(result.get().getStages().isEmpty());
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

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(200);
        stageResponse.setStageName("Stage 1");
        stageResponse.setMatchId(100);
        ipscResponse.setStages(List.of(stageResponse));

        ipscResponse.setScores(new ArrayList<>());
        ipscResponse.setMembers(new ArrayList<>());

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

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

        // Act
        var result = ipscMatchService.initMatchResults(ipscResponse);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        MatchResultsDto matchResults = result.get();
        assertEquals("Complete Match", matchResults.getMatch().getName());
        assertEquals(1, matchResults.getStages().size());
    }
}
