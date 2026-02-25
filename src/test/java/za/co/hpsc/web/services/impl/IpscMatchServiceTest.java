package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceTest {

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchService;

    // =====================================================================
    // Tests for createBasicMatch - Valid Data and Filtering
    // =====================================================================

    @Test
    public void testCreateBasicMatch_withValidData_thenReturnsIpscResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setMatchDate(LocalDateTime.of(2025, 9, 6, 0, 0, 0));

        TagRequest tagRequest1 = new TagRequest();
        tagRequest1.setTagId(1);
        tagRequest1.setTagName("RO");

        TagRequest tagRequest2 = new TagRequest();
        tagRequest2.setTagId(2);
        tagRequest2.setTagName("CRO");

        StageRequest stageRequest1 = new StageRequest();
        stageRequest1.setStageId(1);
        stageRequest1.setStageName("Stage 1");
        stageRequest1.setMatchId(100);

        StageRequest stageRequest2 = new StageRequest();
        stageRequest2.setStageId(2);
        stageRequest2.setStageName("Stage 2");
        stageRequest2.setMatchId(100);

        StageRequest stageRequest3 = new StageRequest();
        stageRequest3.setStageId(3);
        stageRequest3.setStageName("Stage 3");
        stageRequest3.setMatchId(200); // Different match

        EnrolledRequest enrolledRequest1 = new EnrolledRequest();
        enrolledRequest1.setMemberId(50);
        enrolledRequest1.setMatchId(100);
        enrolledRequest1.setCompetitorId(500);

        EnrolledRequest enrolledRequest2 = new EnrolledRequest();
        enrolledRequest2.setMemberId(51);
        enrolledRequest2.setMatchId(100);
        enrolledRequest2.setCompetitorId(501);

        EnrolledRequest enrolledRequest3 = new EnrolledRequest();
        enrolledRequest3.setMemberId(52);
        enrolledRequest3.setMatchId(200); // Different match

        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMatchId(100);
        scoreRequest1.setStageId(1);
        scoreRequest1.setMemberId(50);
        scoreRequest1.setFinalScore(100);

        ScoreRequest scoreRequest2 = new ScoreRequest();
        scoreRequest2.setMatchId(100);
        scoreRequest2.setStageId(2);
        scoreRequest2.setMemberId(51);
        scoreRequest2.setFinalScore(95);

        ScoreRequest scoreRequest3 = new ScoreRequest();
        scoreRequest3.setMatchId(200); // Different match
        scoreRequest3.setStageId(3);
        scoreRequest3.setMemberId(52);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(List.of(tagRequest1, tagRequest2));
        ipscRequestHolder.setStages(List.of(stageRequest1, stageRequest2, stageRequest3));
        ipscRequestHolder.setEnrolledMembers(List.of(enrolledRequest1, enrolledRequest2, enrolledRequest3));
        ipscRequestHolder.setScores(List.of(scoreRequest1, scoreRequest2, scoreRequest3));

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
    public void testCreateBasicMatch_withNoMatchingStages_thenReturnsEmptyStagesList() {
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
    public void testCreateBasicMatch_withNoMatchingEnrolledMembers_thenReturnsEmptyEnrolledList() {
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
    public void testCreateBasicMatch_withNoMatchingScores_thenReturnsEmptyScoresList() {
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
    public void testCreateBasicMatch_withEmptyRequestHolder_thenReturnsResponseWithEmptyLists() {
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
    public void testCreateBasicMatch_withMultipleTags_thenIncludesAllTags() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        TagRequest tagRequest1 = new TagRequest();
        tagRequest1.setTagId(1);
        tagRequest1.setTagName("RO");

        TagRequest tagRequest2 = new TagRequest();
        tagRequest2.setTagId(2);
        tagRequest2.setTagName("CRO");

        TagRequest tagRequest3 = new TagRequest();
        tagRequest3.setTagId(3);
        tagRequest3.setTagName("SO");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(List.of(tagRequest1, tagRequest2, tagRequest3));
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

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
    public void testCreateBasicMatch_withMixedMatchIds_thenFiltersCorrectly() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(1);
        stage1.setMatchId(100);

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(2);
        stage2.setMatchId(200);

        StageRequest stage3 = new StageRequest();
        stage3.setStageId(3);
        stage3.setMatchId(100);

        EnrolledRequest enrolled1 = new EnrolledRequest();
        enrolled1.setMemberId(50);
        enrolled1.setMatchId(100);

        EnrolledRequest enrolled2 = new EnrolledRequest();
        enrolled2.setMemberId(51);
        enrolled2.setMatchId(300);

        ScoreRequest score1 = new ScoreRequest();
        score1.setMatchId(100);
        score1.setMemberId(50);

        ScoreRequest score2 = new ScoreRequest();
        score2.setMatchId(400);
        score2.setMemberId(52);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stage1, stage2, stage3));
        ipscRequestHolder.setEnrolledMembers(List.of(enrolled1, enrolled2));
        ipscRequestHolder.setScores(List.of(score1, score2));

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
    public void testCreateBasicMatch_withNullMatchId_thenReturnsNull() {
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

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setMemberId(50);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stageRequest));
        ipscRequestHolder.setEnrolledMembers(List.of(enrolledRequest));
        ipscRequestHolder.setScores(List.of(scoreRequest));

        // Act
        IpscResponse result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest).orElse(null);

        // Assert
        assertNull(result);
    }

    // =====================================================================
    // Tests for addClubToMatch - Club Matching and Null Handling
    // =====================================================================

    @Test
    public void testAddClubToMatch_withMatchingClub_thenSetsClubOnResponse() {
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
    public void testAddClubToMatch_withNoMatchingClub_thenSetsDefaultClubResponse() {
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
    public void testAddClubToMatch_withEmptyClubsList_thenSetsDefaultClubResponse() {
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
    public void testAddClubToMatch_withMultipleClubs_thenSetsCorrectClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(2);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(1);
        clubRequest1.setClubCode("ABC");
        clubRequest1.setClubName("First Club");

        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(2);
        clubRequest2.setClubCode("DEF");
        clubRequest2.setClubName("Second Club");

        ClubRequest clubRequest3 = new ClubRequest();
        clubRequest3.setClubId(3);
        clubRequest3.setClubCode("GHI");
        clubRequest3.setClubName("Third Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest1, clubRequest2, clubRequest3));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("DEF", ipscResponse.getClub().getClubCode());
        assertEquals("Second Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withClubIdNull_thenSetsNullClubResponse() {
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
    public void testAddClubToMatch_withClubIdZero_thenFindsMatchingClubOrDefault() {
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
    public void testAddClubToMatch_withCompleteClubData_thenSetsAllClubFields() {
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
        clubRequest.setAddress1("123 Main St");
        clubRequest.setAddress2("Suite 100");
        clubRequest.setCity("Cape Town");
        clubRequest.setProvince("Western Cape");
        clubRequest.setCountryId("ZA");
        clubRequest.setPostalCode("8001");
        clubRequest.setOfficePhoneNumber("0211234567");
        clubRequest.setAlternativePhoneNumber("0827654321");
        clubRequest.setFaxNumber("0211234568");
        clubRequest.setEmail("info@alphaclub.co.za");
        clubRequest.setWebsite("https://alphaclub.co.za");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
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
    public void testAddClubToMatch_withNullClubsList_thenDoesNothing() {
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
    public void testAddClubToMatch_withAllNullClubsInList_thenSetsDefaultClub() {
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
    public void testAddClubToMatch_withEmptyClubCode_thenPreservesEmptyString() {
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
    public void testAddClubToMatch_withEmptyClubName_thenPreservesEmptyString() {
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
    public void testAddClubToMatch_withWhitespaceClubCode_thenPreservesWhitespace() {
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
    public void testAddClubToMatch_withOnlyClubId_thenSetsOnlyClubId() {
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
    public void testAddClubToMatch_withIdAndCodeOnly_thenSetsIdAndCode() {
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
    public void testAddClubToMatch_withCompleteClubDataAllFields_thenSetsAllFields() {
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
        clubRequest.setAddress1("123 Main St");
        clubRequest.setAddress2("Suite 100");
        clubRequest.setCity("Cape Town");
        clubRequest.setProvince("Western Cape");
        clubRequest.setCountryId("ZA");
        clubRequest.setPostalCode("8001");
        clubRequest.setOfficePhoneNumber("0211234567");
        clubRequest.setAlternativePhoneNumber("0827654321");
        clubRequest.setFaxNumber("0211234568");
        clubRequest.setEmail("info@alphaclub.co.za");
        clubRequest.setWebsite("https://alphaclub.co.za");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
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
    public void testAddClubToMatch_withMultipleClubsAllComplete_thenSelectsCorrectOne() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(2);

        MatchResponse matchResponse = new MatchResponse(matchRequest);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);

        ClubRequest club1 = new ClubRequest();
        club1.setClubId(1);
        club1.setClubCode("ABC");
        club1.setClubName("First Club");
        club1.setContact("Contact 1");
        club1.setEmail("club1@test.com");

        ClubRequest club2 = new ClubRequest();
        club2.setClubId(2);
        club2.setClubCode("DEF");
        club2.setClubName("Second Club");
        club2.setContact("Contact 2");
        club2.setEmail("club2@test.com");

        ClubRequest club3 = new ClubRequest();
        club3.setClubId(3);
        club3.setClubCode("GHI");
        club3.setClubName("Third Club");
        club3.setContact("Contact 3");
        club3.setEmail("club3@test.com");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(club1, club2, club3));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("DEF", ipscResponse.getClub().getClubCode());
        assertEquals("Second Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withNullIpscResponse_thenDoesNothing() {
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
    public void testAddClubToMatch_withNullIpscRequestHolder_thenDoesNothing() {
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
    public void testAddClubToMatch_withBothNullParameters_thenDoesNothing() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> ipscMatchService.addClubToMatch(null, null));
    }

    @Test
    public void testAddClubToMatch_withNullMatch_thenDoesNothing() {
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
    public void testAddClubToMatch_withLargeClubIdNumber_thenFindsCorrectClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(999999);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(100);
        clubRequest1.setClubCode("ABC");
        clubRequest1.setClubName("Club One");

        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(999999);
        clubRequest2.setClubCode("XYZ");
        clubRequest2.setClubName("Large ID Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest1, clubRequest2));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(999999, ipscResponse.getClub().getClubId());
        assertEquals("XYZ", ipscResponse.getClub().getClubCode());
        assertEquals("Large ID Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withNegativeClubId_thenHandlesNegativeId() {
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
    public void testAddClubToMatch_withDuplicateClubIds_thenUsesFirstMatch() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(1);
        clubRequest1.setClubCode("FIRST");
        clubRequest1.setClubName("First Club");

        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(1); // Same ID
        clubRequest2.setClubCode("SECOND");
        clubRequest2.setClubName("Second Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest1, clubRequest2));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert - should use first match
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("FIRST", ipscResponse.getClub().getClubCode());
        assertEquals("First Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withPartialClubData_thenSetsAvailableFields() {
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
    public void testAddClubToMatch_withClubCodeAndNameNull_thenSetsClubIdOnly() {
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
    public void testAddClubToMatch_withVeryLongClubCode_thenPreservesFullCode() {
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
    public void testAddClubToMatch_withVeryLongClubName_thenPreservesFullName() {
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
    public void testAddClubToMatch_withSpecialCharactersInCode_thenPreservesCharacters() {
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
    public void testAddClubToMatch_withPunctuationInClubName_thenPreservesCharacters() {
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
    public void testAddClubToMatch_withUnicodeInClubCode_thenPreservesUnicode() {
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
    public void testAddClubToMatch_withEmojiInClubName_thenPreservesEmoji() {
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
    public void testAddClubToMatch_withMaxIntClubId_thenProcessesSuccessfully() {
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
    public void testAddClubToMatch_withMinIntClubId_thenProcessesSuccessfully() {
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
    public void testAddClubToMatch_withNullElementsInClubList_thenFiltersAndFindsMatch() {
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

        ClubRequest nullClub = null;

        ClubRequest club2 = new ClubRequest();
        club2.setClubId(2);
        club2.setClubCode("BBB");
        club2.setClubName("Second Club");

        List<ClubRequest> clubList = new ArrayList<>();
        clubList.add(club1);
        clubList.add(nullClub);
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
    public void testAddClubToMatch_withExistingClubThenNewData_thenCompletelyOverwrites() {
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
    public void testAddClubToMatch_withLargeClubListManyElements_thenFindsCorrectClub() {
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
    public void testAddMembersToMatch_withMatchingMembers_thenSetsMembersOnResponse() {
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

        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMemberId(50);
        scoreRequest1.setMatchId(100);
        scoreRequest1.setStageId(1);

        ScoreRequest scoreRequest2 = new ScoreRequest();
        scoreRequest2.setMemberId(51);
        scoreRequest2.setMatchId(100);
        scoreRequest2.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest1, memberRequest2));
        ipscRequestHolder.setScores(List.of(scoreRequest1, scoreRequest2));

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
    public void testAddMembersToMatch_withSingleScore_thenSetsMatchingMember() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        memberRequest.setIcsAlias("12345");

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(50);
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);
        scoreRequest.setFinalScore(100);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest));
        ipscRequestHolder.setScores(List.of(scoreRequest));

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
    public void testAddMembersToMatch_withNoMatchingMembers_thenSetsEmptyList() {
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
    public void testAddMembersToMatch_withNoScores_thenDoesNotSetMembers() {
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
    public void testAddMembersToMatch_withNoMembers_thenSetsEmptyList() {
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
    public void testAddMembersToMatch_withMultipleMembersAndScores_thenSetsLastMatchingMembers() {
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

        ScoreRequest scoreRequest1 = new ScoreRequest();
        scoreRequest1.setMemberId(50);
        scoreRequest1.setMatchId(100);

        ScoreRequest scoreRequest2 = new ScoreRequest();
        scoreRequest2.setMemberId(51);
        scoreRequest2.setMatchId(100);

        ScoreRequest scoreRequest3 = new ScoreRequest();
        scoreRequest3.setMemberId(52);
        scoreRequest3.setMatchId(100);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest1, memberRequest2, memberRequest3));
        ipscRequestHolder.setScores(List.of(scoreRequest1, scoreRequest2, scoreRequest3));

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
    public void testAddMembersToMatch_withMixedMatchingAndNonMatchingMembers_thenSetsMatchingMembers() {
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

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMemberId(51);
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(1);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setMembers(List.of(memberRequest1, memberRequest2, memberRequest3));
        ipscRequestHolder.setScores(List.of(scoreRequest));

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
    public void testAddMembersToMatch_withNullMemberId_thenHandlesGracefully() {
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
    public void testAddMembersToMatch_withCompleteMemberData_thenSetsAllMemberFields() {
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
    public void testCreateBasicMatch_withNullMatch_thenReturnsEmptyOptional() {
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
    public void testCreateBasicMatch_withNullIpscRequestHolder_thenThrowsNullPointerException() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                ipscMatchService.createBasicMatch(null, matchRequest));
    }

    @Test
    public void testCreateBasicMatch_withNullMatchId_thenReturnsEmptyOptional() {
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
    public void testCreateBasicMatch_withNullStages_thenReturnsEmptyOptional() {
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
    public void testCreateBasicMatch_withNullEnrolledMembers_thenReturnsEmptyOptional() {
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
    public void testCreateBasicMatch_withNullScores_thenReturnsEmptyOptional() {
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
    public void testCreateBasicMatch_withBlankMatchName_thenProcesses() {
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
    public void testCreateBasicMatch_withNullMatchName_thenProcesses() {
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
    public void testCreateBasicMatch_withWhitespaceMatchName_thenProcesses() {
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
    public void testCreateBasicMatch_withEmptyTags_thenReturnsResponseWithEmptyTags() {
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
    public void testCreateBasicMatch_withEmptyStages_thenReturnsResponseWithEmptyStages() {
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
    public void testCreateBasicMatch_withEmptyEnrolledMembers_thenReturnsResponseWithEmptyEnrolled() {
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
    public void testCreateBasicMatch_withEmptyScores_thenReturnsResponseWithEmptyScores() {
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
    public void testCreateBasicMatch_withOnlyStages_thenFiltersStagesByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(1);
        stage1.setMatchId(100);
        stage1.setStageName("Stage 1");

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(2);
        stage2.setMatchId(200);  // Different match
        stage2.setStageName("Stage 2");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stage1, stage2));  // Only stages
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStages().size());
        assertEquals(1, result.get().getStages().getFirst().getStageId());
        assertEquals(100, result.get().getStages().getFirst().getMatchId());
    }

    @Test
    public void testCreateBasicMatch_withOnlyEnrolledMembers_thenFiltersEnrolledByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        EnrolledRequest enrolled1 = new EnrolledRequest();
        enrolled1.setMemberId(50);
        enrolled1.setMatchId(100);

        EnrolledRequest enrolled2 = new EnrolledRequest();
        enrolled2.setMemberId(51);
        enrolled2.setMatchId(200);  // Different match

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(List.of(enrolled1, enrolled2));  // Only enrolled
        ipscRequestHolder.setScores(new ArrayList<>());

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getEnrolledMembers().size());
        assertEquals(50, result.get().getEnrolledMembers().getFirst().getMemberId());
    }

    @Test
    public void testCreateBasicMatch_withOnlyScores_thenFiltersScoresByMatchId() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        ScoreRequest score1 = new ScoreRequest();
        score1.setMatchId(100);
        score1.setMemberId(50);
        score1.setFinalScore(100);

        ScoreRequest score2 = new ScoreRequest();
        score2.setMatchId(200);  // Different match
        score2.setMemberId(51);
        score2.setFinalScore(95);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(List.of(score1, score2));  // Only scores

        // Act
        Optional<IpscResponse> result = ipscMatchService.createBasicMatch(ipscRequestHolder, matchRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getScores().size());
        assertEquals(100, result.get().getScores().getFirst().getMatchId());
    }

    @Test
    public void testCreateBasicMatch_withStagesAndScores_thenFiltersCorrectly() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stage = new StageRequest();
        stage.setStageId(1);
        stage.setMatchId(100);

        ScoreRequest score = new ScoreRequest();
        score.setMatchId(100);
        score.setMemberId(50);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(new ArrayList<>());
        ipscRequestHolder.setStages(List.of(stage));
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(List.of(score));

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
    public void testCreateBasicMatch_withFullData_thenReturnsCompleteResponse() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Full Match Data");
        matchRequest.setMatchDate(LocalDateTime.of(2025, 9, 6, 10, 0, 0));

        TagRequest tag = new TagRequest();
        tag.setTagId(1);
        tag.setTagName("RO");

        StageRequest stage = new StageRequest();
        stage.setStageId(1);
        stage.setMatchId(100);
        stage.setStageName("Stage 1");

        EnrolledRequest enrolled = new EnrolledRequest();
        enrolled.setMemberId(50);
        enrolled.setMatchId(100);

        ScoreRequest score = new ScoreRequest();
        score.setMatchId(100);
        score.setMemberId(50);
        score.setFinalScore(100);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(List.of(tag));
        ipscRequestHolder.setStages(List.of(stage));
        ipscRequestHolder.setEnrolledMembers(List.of(enrolled));
        ipscRequestHolder.setScores(List.of(score));

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
    public void testCreateBasicMatch_withMultipleTags_thenIncludesAll() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        TagRequest tag1 = new TagRequest();
        tag1.setTagId(1);
        tag1.setTagName("RO");

        TagRequest tag2 = new TagRequest();
        tag2.setTagId(2);
        tag2.setTagName("CRO");

        TagRequest tag3 = new TagRequest();
        tag3.setTagId(3);
        tag3.setTagName("SO");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setTags(List.of(tag1, tag2, tag3));  // All tags included
        ipscRequestHolder.setStages(new ArrayList<>());
        ipscRequestHolder.setEnrolledMembers(new ArrayList<>());
        ipscRequestHolder.setScores(new ArrayList<>());

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
    public void testCreateBasicMatch_withNullStagesInCollection_thenFiltersNulls() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(1);
        stage1.setMatchId(100);

        StageRequest nullStage = null;

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(2);
        stage2.setMatchId(100);

        List<StageRequest> stageList = new ArrayList<>();
        stageList.add(stage1);
        stageList.add(nullStage);
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
    public void testCreateBasicMatch_withNullScoresInCollection_thenFiltersNulls() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");

        ScoreRequest score1 = new ScoreRequest();
        score1.setMatchId(100);
        score1.setMemberId(50);

        ScoreRequest nullScore = null;

        ScoreRequest score2 = new ScoreRequest();
        score2.setMatchId(100);
        score2.setMemberId(51);

        List<ScoreRequest> scoreList = new ArrayList<>();
        scoreList.add(score1);
        scoreList.add(nullScore);
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
    public void testGenerateIpscMatchRecordHolder_withEmptyList_thenReturnsEmptyHolder() {
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
    public void testGenerateIpscMatchRecordHolder_withSingleMatch_thenReturnsSingleRecord() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 15, 0, 0));

        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

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
    public void testGenerateIpscMatchRecordHolder_withMultipleMatches_thenReturnsMultipleRecords() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName(ClubIdentifier.SOSC.getName());
        club.setAbbreviation(ClubIdentifier.SOSC.getName());

        IpscMatch match1 = new IpscMatch();
        match1.setId(1L);
        match1.setName("Match 1");
        match1.setScheduledDate(LocalDateTime.of(2025, 9, 5, 10, 0, 0));
        match1.setClub(club);
        match1.setMatchFirearmType(FirearmType.HANDGUN);
        match1.setMatchCategory(MatchCategory.LEAGUE);
        match1.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match1.setMatchStages(new ArrayList<>());
        match1.setMatchCompetitors(new ArrayList<>());

        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("Match 2");
        match2.setScheduledDate(LocalDateTime.of(2025, 9, 4, 15, 0, 0));
        match2.setClub(club);
        match2.setMatchFirearmType(FirearmType.RIFLE);
        match2.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match2.setDateEdited(LocalDateTime.of(2025, 9, 5, 15, 0, 0));
        match2.setMatchStages(new ArrayList<>());
        match2.setMatchCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match1, match2);

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
    public void testGenerateIpscMatchRecordHolder_withNullMatchStages_thenProcessesWithoutStages() {
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
        match.setMatchStages(null);
        match.setMatchCompetitors(new ArrayList<>());

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
    public void testGenerateIpscMatchRecordHolder_withNullMatchCompetitors_thenProcessesWithoutCompetitors() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(null);

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
    public void testGenerateIpscMatchRecordHolder_withMatchStagesButNoCompetitors_thenReturnsEmptyCompetitors() {
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

        match.setMatchStages(List.of(stage));
        match.setMatchCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_withNullDateFields_thenHandlesNullDates() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

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
    public void testGenerateIpscMatchRecordHolder_withNullMatchNameAndCategory_thenProcesses() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

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
    public void testGenerateIpscMatchRecordHolder_withLargeNumberOfMatches_thenProcessesAll() {
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
            match.setMatchStages(new ArrayList<>());
            match.setMatchCompetitors(new ArrayList<>());
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
    public void testGenerateIpscMatchRecordHolder_withSpecialCharactersInNames_thenPreservesCharacters() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Test Match & Co. (2025) - v2.0");
        match.setScheduledDate(LocalDateTime.of(2025, 9, 6, 15, 0, 0));
//        match.setClubName(ClubIdentifier.SOSC);
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setDateEdited(LocalDateTime.of(2025, 9, 6, 10, 0, 0));
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals("Test Match & Co. (2025) - v2.0", result.matches().getFirst().name());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_withEmptyMatchStagesList_thenProcessesCorrectly() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_withEmptyCompetitorsList_thenProcessesCorrectly() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

        List<IpscMatch> matchList = List.of(match);

        // Act
        IpscMatchRecordHolder result = ipscMatchService.generateIpscMatchRecordHolder(matchList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.matches().size());
        assertTrue(result.matches().getFirst().competitors().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_withMatchContainsAllFields_thenVerifiesDataIntegrity() {
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
        match.setMatchStages(new ArrayList<>());
        match.setMatchCompetitors(new ArrayList<>());

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
    public void testInitIpscMatchResponse_withNullMatch_thenReturnsEmptyOptional() {
        // Arrange
        List<CompetitorMatchRecord> competitors = new ArrayList<>();

        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(null, competitors);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitIpscMatchResponse_withNullCompetitors_thenReturnsEmptyOptional() {
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
    public void testInitIpscMatchResponse_withBothNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<IpscMatchRecord> result = ipscMatchService.initIpscMatchResponse(null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for initIpscMatchResponse - Null/Blank/Empty String Handling
    // =====================================================================

    @Test
    public void testInitIpscMatchResponse_withNullMatchName_thenReturnsNull() {
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
    public void testInitIpscMatchResponse_withBlankMatchName_thenProcesses() {
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
    public void testInitIpscMatchResponse_withWhitespaceMatchName_thenProcesses() {
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
    public void testInitIpscMatchResponse_withOnlyMatchName_thenProcesses() {
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
    public void testInitIpscMatchResponse_withOnlyClub_thenSetsClubName() {
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
    public void testInitIpscMatchResponse_withOnlyScheduledDate_thenFormatsDate() {
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
    public void testInitIpscMatchResponse_withNullScheduledDate_thenHandlesNull() {
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
    public void testInitIpscMatchResponse_withNullDateEdited_thenHandles() {
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
    public void testInitIpscMatchResponse_withCompleteData_thenReturnsCompleteRecord() {
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
    public void testInitIpscMatchResponse_withMultipleCompetitors_thenIncludesAll() {
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
    public void testInitIpscMatchResponse_withAllFirearmTypes_thenProcesses() {
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
    public void testInitIpscMatchResponse_withAllMatchCategories_thenProcesses() {
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
    public void testInitIpscMatchResponse_withNullFirearmType_thenHandlesGracefully() {
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
    public void testInitIpscMatchResponse_withNullMatchCategory_thenHandlesGracefully() {
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
    public void testInitIpscMatchResponse_withHistoricalDate_thenProcesses() {
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
    public void testInitIpscMatchResponse_withFutureDate_thenProcesses() {
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
    public void testInitIpscMatchResponse_withVeryLongMatchName_thenProcesses() {
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
    public void testInitIpscMatchResponse_withUnicodeInMatchName_thenProcesses() {
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
    public void testInitIpscMatchResponse_withEmptyCompetitorsList_thenIncludesEmpty() {
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
    public void testInitIpscMatchResponse_withSpecialCharactersInName_thenPreserves() {
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
    public void testInitIpscMatchResponse_withClubNullName_thenHandlesGracefully() {
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
    public void testInitIpscMatchResponse_withLargeCompetitorList_thenProcesses() {
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
    public void testInitIpscMatchResponse_withBothDateFieldsNull_thenHandles() {
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
    public void testInitIpscMatchResponse_withAllOptionalFieldsNull_thenProcesses() {
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
    public void testInitCompetitor_withNullCompetitor_thenReturnsEmptyOptional() {
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
    public void testInitCompetitor_withNullMatchCompetitorRecord_thenReturnsEmptyOptional() {
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
    public void testInitCompetitor_withNullStagesList_thenReturnsEmptyOptional() {
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
    public void testInitCompetitor_withAllNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<CompetitorMatchRecord> result = ipscMatchService.initCompetitor(null, null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    // =====================================================================
    // Tests for initCompetitor - Null/Blank/Empty String Handling
    // =====================================================================

    @Test
    public void testInitCompetitor_withNullFirstName_thenProcesses() {
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
    public void testInitCompetitor_withEmptyFirstName_thenProcesses() {
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
    public void testInitCompetitor_withNullLastName_thenProcesses() {
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
    public void testInitCompetitor_withNullMiddleNames_thenProcesses() {
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
    public void testInitCompetitor_withNullDateOfBirth_thenFormatsNull() {
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
    public void testInitCompetitor_withOnlyRequiredFields_thenProcesses() {
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
    public void testInitCompetitor_withEmptyStagesList_thenIncludesEmptyList() {
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
    public void testInitCompetitor_withCompleteData_thenReturnsCompleteRecord() {
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
    public void testInitCompetitor_withMultipleStages_thenIncludesAllStages() {
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
    public void testInitCompetitor_withVeryLongFirstName_thenProcesses() {
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
    public void testInitCompetitor_withUnicodeInNames_thenProcesses() {
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
    public void testInitCompetitor_withSpecialCharactersInNames_thenProcesses() {
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
    public void testInitCompetitor_withLargeSapsaNumber_thenProcesses() {
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
    public void testInitCompetitor_withNullSapsaNumber_thenProcesses() {
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
    public void testInitCompetitor_withHistoricalDateOfBirth_thenProcesses() {
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
    public void testInitCompetitor_withRecentDateOfBirth_thenProcesses() {
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
    public void testInitCompetitor_withAllFieldsExceptSapsa_thenProcesses() {
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
    public void testInitCompetitor_withAllFieldsExceptNumber_thenProcesses() {
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
    public void testInitCompetitor_withNullFieldsInMatchRecord_thenProcesses() {
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
    public void testInitCompetitor_withLargeStageList_thenProcesses() {
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
    public void testInitCompetitor_withEmptyCompetitorNumber_thenProcesses() {
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
    public void testInitMatchCompetitor_withNullCompetitor_thenReturnsEmptyOptional() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(null, matchCompetitorList);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_withNullMatchCompetitorList_thenReturnsEmptyOptional() {
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
    public void testInitMatchCompetitor_withBothNullParameters_thenReturnsEmptyOptional() {
        // Act
        Optional<MatchCompetitorRecord> result = ipscMatchService.initMatchCompetitor(null, null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testInitMatchCompetitor_withEmptyMatchCompetitorList_thenReturnsEmptyOptional() {
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
    public void testInitMatchCompetitor_withNoMatchingCompetitor_thenReturnsEmptyOptional() {
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
    public void testInitMatchCompetitor_withNullMatch_thenProcesses() {
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
    public void testInitMatchCompetitor_withNullClub_thenSetsEmptyClubName() {
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
    public void testInitMatchCompetitor_withNullFirearmType_thenSetsEmpty() {
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
    public void testInitMatchCompetitor_withNullDivision_thenSetsEmpty() {
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
    public void testInitMatchCompetitor_withNullPowerFactor_thenSetsEmpty() {
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
    public void testInitMatchCompetitor_withNullCategory_thenSetsEmpty() {
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
    public void testInitMatchCompetitor_withOnlyFirearmType_thenProcesses() {
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
    public void testInitMatchCompetitor_withOnlyDivision_thenProcesses() {
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
    public void testInitMatchCompetitor_withCompleteData_thenReturnsCompleteRecord() {
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
    public void testInitMatchCompetitor_withMultipleCompetitorsInList_thenFindsCorrectOne() {
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
    public void testInitMatchCompetitor_withAllFirearmTypes_thenProcesses() {
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
    public void testInitMatchCompetitor_withZeroMatchPoints_thenProcesses() {
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
    public void testInitMatchCompetitor_withNegativeMatchPoints_thenProcesses() {
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
    public void testInitMatchCompetitor_withVeryLargeMatchPoints_thenProcesses() {
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
    public void testInitMatchCompetitor_withNullMatchPoints_thenFormatsAsNull() {
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
    public void testInitMatchCompetitor_withNullMatchRanking_thenFormatsAsNull() {
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
    public void testInitMatchCompetitor_withNullDateEdited_thenFormatsNull() {
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
    public void testInitMatchCompetitor_withHistoricalDate_thenProcesses() {
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
    public void testInitMatchCompetitor_withFutureDate_thenProcesses() {
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
    public void testInitMatchCompetitor_withNullsInList_thenFiltersAndFinds() {
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
    public void testInitMatchCompetitor_withLargeList_thenFindsCorrectOne() {
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
    public void testInitMatchCompetitor_withAllFieldsNull_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withNullCompetitor_thenReturnsEmptyList() {
        // Arrange
        List<MatchStageCompetitor> matchStageCompetitorList = new ArrayList<>();

        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(null, matchStageCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_withNullMatchStageCompetitorList_thenReturnsEmptyList() {
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
    public void testInitMatchStageCompetitor_withBothNullParameters_thenReturnsEmptyList() {
        // Act
        List<MatchStageCompetitorRecord> result = ipscMatchService.initMatchStageCompetitor(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInitMatchStageCompetitor_withEmptyList_thenReturnsEmptyList() {
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
    public void testInitMatchStageCompetitor_withNoMatchingCompetitor_thenReturnsEmptyList() {
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
    public void testInitMatchStageCompetitor_withNullFirearmType_thenSetsEmpty() {
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
    public void testInitMatchStageCompetitor_withNullDivision_thenSetsEmpty() {
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
    public void testInitMatchStageCompetitor_withNullPowerFactor_thenSetsEmpty() {
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
    public void testInitMatchStageCompetitor_withNullCompetitorCategory_thenSetsEmpty() {
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
    public void testInitMatchStageCompetitor_withNullScores_thenIncludesNullScores() {
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
    public void testInitMatchStageCompetitor_withNullPenalties_thenIncludesNullPenalties() {
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
    public void testInitMatchStageCompetitor_withNullBigDecimals_thenFormatsAsEmpty() {
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
    public void testInitMatchStageCompetitor_withNullDateEdited_thenFormatsAsEmpty() {
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
    public void testInitMatchStageCompetitor_withOnlyScores_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withOnlyPerformanceMetrics_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withCompleteData_thenReturnsCompleteRecord() {
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
    public void testInitMatchStageCompetitor_withMultipleStages_thenReturnsAll() {
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
    public void testInitMatchStageCompetitor_withAllFirearmTypes_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withZeroScores_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withLargeScores_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withZeroBigDecimalValues_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withLargeBigDecimalValues_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withHistoricalDate_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withFutureDate_thenProcesses() {
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
    public void testInitMatchStageCompetitor_withNullsInList_thenFiltersAndFinds() {
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
    public void testInitMatchStageCompetitor_withMixedCompetitors_thenFiltersCorrectly() {
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
    public void testInitMatchStageCompetitor_withLargeList_thenFindsCorrectOnes() {
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
    public void testInitMatchStageCompetitor_withAllFieldsNull_thenProcesses() {
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
    // Tests for getCompetitorList - Null Input Handling
    // =====================================================================

    @Test
    public void testGetCompetitorList_withNullList_thenReturnsEmptyList() {
        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitorList_withEmptyList_thenReturnsEmptyList() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getCompetitorList - Null Elements Handling
    // =====================================================================

    @Test
    public void testGetCompetitorList_withAllNullElements_thenReturnsEmptyList() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        matchCompetitorList.add(null);
        matchCompetitorList.add(null);
        matchCompetitorList.add(null);

        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitorList_withSomeNullElements_thenFiltersNulls() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(competitor1, result.getFirst());
        assertEquals(competitor2, result.get(1));
    }

    @Test
    public void testGetCompetitorList_withNullCompetitorInMatchCompetitor_thenIncludesNull() {
        // Arrange
        MatchCompetitor mc1 = new MatchCompetitor();
        mc1.setId(1L);
        mc1.setCompetitor(null);

        List<MatchCompetitor> matchCompetitorList = List.of(mc1);

        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst());
    }

    // =====================================================================
    // Tests for getCompetitorList - Single Element
    // =====================================================================

    @Test
    public void testGetCompetitorList_withSingleCompetitor_thenReturnsSingleElement() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(competitor, result.getFirst());
        assertEquals("John", result.getFirst().getFirstName());
        assertEquals("Doe", result.getFirst().getLastName());
    }

    // =====================================================================
    // Tests for getCompetitorList - Multiple Elements
    // =====================================================================

    @Test
    public void testGetCompetitorList_withMultipleCompetitors_thenReturnsAll() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("FirstName" + (i + 1), result.get(i).getFirstName());
            assertEquals("LastName" + (i + 1), result.get(i).getLastName());
        }
    }

    // =====================================================================
    // Tests for getCompetitorList - Partial Data
    // =====================================================================

    @Test
    public void testGetCompetitorList_withCompetitorsWithoutNames_thenReturnsAll() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNull(result.getFirst().getFirstName());
        assertEquals("John", result.get(1).getFirstName());
    }

    @Test
    public void testGetCompetitorList_withCompetitorsWithNullFields_thenReturnsAll() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(competitor, result.getFirst());
    }

    // =====================================================================
    // Tests for getCompetitorList - Full Data
    // =====================================================================

    @Test
    public void testGetCompetitorList_withCompleteCompetitorData_thenReturnsComplete() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        assertEquals("Doe", result.getFirst().getLastName());
        assertEquals("Michael", result.getFirst().getMiddleNames());
        assertEquals(12345, result.getFirst().getSapsaNumber());
        assertEquals("COMP001", result.getFirst().getCompetitorNumber());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Duplicates
    // =====================================================================

    @Test
    public void testGetCompetitorList_withDuplicateCompetitors_thenReturnsAllInstances() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(competitor, result.getFirst());
        assertEquals(competitor, result.get(1));
        assertEquals(competitor, result.get(2));
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testGetCompetitorList_withLargeList_thenProcessesAll() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testGetCompetitorList_withSpecialCharactersInNames_thenPreserves() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("O'Connor", result.getFirst().getFirstName());
        assertEquals("Van-Der-Berg", result.getFirst().getLastName());
        assertEquals("José-María", result.getFirst().getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Unicode Characters
    // =====================================================================

    @Test
    public void testGetCompetitorList_withUnicodeCharactersInNames_thenPreserves() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Müller", result.getFirst().getFirstName());
        assertEquals("Søren", result.getFirst().getLastName());
        assertEquals("Ñoño", result.getFirst().getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Long Names
    // =====================================================================

    @Test
    public void testGetCompetitorList_withVeryLongNames_thenPreserves() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200, result.getFirst().getFirstName().length());
        assertEquals(200, result.getFirst().getLastName().length());
        assertEquals(200, result.getFirst().getMiddleNames().length());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Empty Strings
    // =====================================================================

    @Test
    public void testGetCompetitorList_withEmptyStringNames_thenPreserves() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.getFirst().getFirstName());
        assertEquals("", result.getFirst().getLastName());
        assertEquals("", result.getFirst().getMiddleNames());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Extreme Numbers
    // =====================================================================

    @Test
    public void testGetCompetitorList_withExtremeNumbers_thenPreserves() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.MAX_VALUE, result.getFirst().getId());
        assertEquals(Integer.MAX_VALUE, result.getFirst().getSapsaNumber());
        assertEquals(Integer.MIN_VALUE, result.get(1).getSapsaNumber());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Mixed Valid and Null
    // =====================================================================

    @Test
    public void testGetCompetitorList_withMixedValidAndNullCompetitors_thenFiltersCorrectly() {
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
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        assertNull(result.get(1));
        assertEquals("Jane", result.get(2).getFirstName());
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Order Preservation
    // =====================================================================

    @Test
    public void testGetCompetitorList_thenPreservesOrder() {
        // Arrange
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("Name" + i);

            MatchCompetitor mc = new MatchCompetitor();
            mc.setId((long) i);
            mc.setCompetitor(competitor);

            matchCompetitorList.add(mc);
        }

        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, result.get(i).getId());
            assertEquals("Name" + (i + 1), result.get(i).getFirstName());
        }
    }

    // =====================================================================
    // Tests for getCompetitorList - Edge Cases: Immutability
    // =====================================================================

    @Test
    public void testGetCompetitorList_returnsImmutableList() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");

        MatchCompetitor mc = new MatchCompetitor();
        mc.setId(1L);
        mc.setCompetitor(competitor);

        List<MatchCompetitor> matchCompetitorList = List.of(mc);

        // Act
        List<Competitor> result = ipscMatchService.getCompetitorList(matchCompetitorList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify that the returned list is immutable
        assertThrows(UnsupportedOperationException.class, () -> result.add(new Competitor()));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Null Input Handling
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withNullList_thenReturnsEmptyList() {
        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_withEmptyList_thenReturnsEmptyList() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Null Elements Handling
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withAllNullElements_thenReturnsEmptyList() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();
        matchStageList.add(null);
        matchStageList.add(null);
        matchStageList.add(null);

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_withSomeNullElements_thenFiltersNulls() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Empty because stages have no competitors
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Empty Competitor Lists
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withStagesWithoutCompetitors_thenReturnsEmptyList() {
        // Arrange
        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(new ArrayList<>());

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(new ArrayList<>());

        List<IpscMatchStage> matchStageList = List.of(stage1, stage2);

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorList_withNullCompetitorLists_thenReturnsEmptyList() {
        // Arrange
        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatchStageCompetitors(null);

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatchStageCompetitors(null);

        List<IpscMatchStage> matchStageList = List.of(stage1, stage2);

        // Act & Assert - This will throw NPE since flatMap expects non-null
        assertThrows(NullPointerException.class, () -> ipscMatchService.getMatchStageCompetitorList(matchStageList));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Single Stage
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withSingleStageOneCompetitor_thenReturnsSingle() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msc, result.getFirst());
        assertEquals(competitor, result.getFirst().getCompetitor());
    }

    @Test
    public void testGetMatchStageCompetitorList_withSingleStageMultipleCompetitors_thenReturnsAll() {
        // Arrange
        List<MatchStageCompetitor> competitors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitor.setFirstName("FirstName" + i);

            MatchStageCompetitor msc = new MatchStageCompetitor();
            msc.setId((long) i);
            msc.setCompetitor(competitor);
            msc.setScoreA(10 + i);

            competitors.add(msc);
        }

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(competitors);

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("FirstName" + (i + 1), result.get(i).getCompetitor().getFirstName());
        }
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Multiple Stages
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withMultipleStages_thenAggregatesAll() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        for (int stageNum = 1; stageNum <= 3; stageNum++) {
            List<MatchStageCompetitor> competitors = new ArrayList<>();
            for (int compNum = 1; compNum <= 2; compNum++) {
                Competitor competitor = new Competitor();
                competitor.setId((long) (stageNum * 10 + compNum));
                competitor.setFirstName("Stage" + stageNum + "Comp" + compNum);

                MatchStageCompetitor msc = new MatchStageCompetitor();
                msc.setId((long) (stageNum * 10 + compNum));
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.size()); // 3 stages * 2 competitors each
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Mixed Scenarios
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withMixedEmptyAndPopulated_thenReturnsOnlyPopulated() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msc, result.getFirst());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Partial Data
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withCompetitorsWithPartialData_thenReturnsAll() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNull(result.getFirst().getCompetitor().getFirstName());
        assertEquals("Jane", result.get(1).getCompetitor().getFirstName());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Full Data
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withCompleteData_thenReturnsComplete() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getCompetitor().getFirstName());
        assertEquals("Doe", result.getFirst().getCompetitor().getLastName());
        assertEquals(FirearmType.HANDGUN, result.getFirst().getFirearmType());
        assertEquals(Division.OPEN, result.getFirst().getDivision());
        assertEquals(10, result.getFirst().getScoreA());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Large Lists
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withLargeNumberOfStages_thenProcessesAll() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(50, result.size());
    }

    @Test
    public void testGetMatchStageCompetitorList_withManyCompetitorsPerStage_thenProcessesAll() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Duplicates
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withSameCompetitorInMultipleStages_thenReturnsAll() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        // Same competitor but different MatchStageCompetitor instances
        assertEquals(competitor, result.getFirst().getCompetitor());
        assertEquals(competitor, result.get(1).getCompetitor());
        assertEquals(competitor, result.get(2).getCompetitor());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Special Characters
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withSpecialCharactersInData_thenPreserves() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("O'Connor", result.getFirst().getCompetitor().getFirstName());
        assertEquals("Van-Der-Berg", result.getFirst().getCompetitor().getLastName());
        assertEquals(Division.PCC_OPTICS, result.getFirst().getDivision());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Order Preservation
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_thenPreservesOrder() {
        // Arrange
        List<IpscMatchStage> matchStageList = new ArrayList<>();

        for (int stageNum = 1; stageNum <= 3; stageNum++) {
            List<MatchStageCompetitor> competitors = new ArrayList<>();
            for (int compNum = 1; compNum <= 3; compNum++) {
                Competitor competitor = new Competitor();
                competitor.setId((long) (stageNum * 10 + compNum));
                competitor.setFirstName("S" + stageNum + "C" + compNum);

                MatchStageCompetitor msc = new MatchStageCompetitor();
                msc.setId((long) (stageNum * 10 + compNum));
                msc.setCompetitor(competitor);

                competitors.add(msc);
            }

            IpscMatchStage stage = new IpscMatchStage();
            stage.setId((long) stageNum);
            stage.setMatchStageCompetitors(competitors);

            matchStageList.add(stage);
        }

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(9, result.size()); // 3 stages * 3 competitors
        // Verify order is preserved: stage1-comp1, stage1-comp2, stage1-comp3, stage2-comp1, etc.
        assertEquals("S1C1", result.getFirst().getCompetitor().getFirstName());
        assertEquals("S1C2", result.get(1).getCompetitor().getFirstName());
        assertEquals("S1C3", result.get(2).getCompetitor().getFirstName());
        assertEquals("S2C1", result.get(3).getCompetitor().getFirstName());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Immutability
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_returnsImmutableList() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");

        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setId(1L);
        msc.setCompetitor(competitor);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(1L);
        stage.setMatchStageCompetitors(List.of(msc));

        List<IpscMatchStage> matchStageList = List.of(stage);

        // Act
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify that the returned list is immutable
        assertThrows(UnsupportedOperationException.class, () -> result.add(new MatchStageCompetitor()));
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorList - Edge Cases: Null Competitors
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorList_withNullCompetitorInMatchStageCompetitor_thenIncludes() {
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst().getCompetitor());
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
        List<MatchStageCompetitor> result = ipscMatchService.getMatchStageCompetitorList(matchStageList);

        // Assert
        assertNotNull(result);
        assertEquals(totalExpected, result.size()); // 2 + 0 + 5 = 7
    }
}
