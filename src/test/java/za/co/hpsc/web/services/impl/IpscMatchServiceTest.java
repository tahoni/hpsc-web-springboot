package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        stageRequest.setStageName("Stage 1");
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
        assertEquals(100, result.getMatch().getMatchId());
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
        assertEquals(100, result.getMatch().getMatchId());
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
        assertEquals(100, result.getMatch().getMatchId());
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
    public void testAddClubToMatch_withMultipleClubsAndSpecificId_thenFindsCorrectOne() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(3);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest1 = new ClubRequest();
        clubRequest1.setClubId(1);
        clubRequest1.setClubCode("AAA");
        clubRequest1.setClubName("Club A");

        ClubRequest clubRequest2 = new ClubRequest();
        clubRequest2.setClubId(2);
        clubRequest2.setClubCode("BBB");
        clubRequest2.setClubName("Club B");

        ClubRequest clubRequest3 = new ClubRequest();
        clubRequest3.setClubId(3);
        clubRequest3.setClubCode("CCC");
        clubRequest3.setClubName("Club C");

        ClubRequest clubRequest4 = new ClubRequest();
        clubRequest4.setClubId(4);
        clubRequest4.setClubCode("DDD");
        clubRequest4.setClubName("Club D");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest1, clubRequest2, clubRequest3, clubRequest4));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(3, ipscResponse.getClub().getClubId());
        assertEquals("CCC", ipscResponse.getClub().getClubCode());
        assertEquals("Club C", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withResponseAlreadyHavingClub_thenOverwritesExistingClub() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(2);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        // Pre-set a club on the response
        ClubResponse oldClub = new ClubResponse();
        oldClub.setClubId(1);
        oldClub.setClubCode("OLD");
        oldClub.setClubName("Old Club");
        ipscResponse.setClub(oldClub);

        ClubRequest newClubRequest = new ClubRequest();
        newClubRequest.setClubId(2);
        newClubRequest.setClubCode("NEW");
        newClubRequest.setClubName("New Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(newClubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert - should have the new club
        assertNotNull(ipscResponse.getClub());
        assertEquals(2, ipscResponse.getClub().getClubId());
        assertEquals("NEW", ipscResponse.getClub().getClubCode());
        assertEquals("New Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withSpecialCharactersInClubName_thenPreserves() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("SPC");
        clubRequest.setClubName("Club & Co. (Cape Town) - Est. 2020");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("SPC", ipscResponse.getClub().getClubCode());
        assertEquals("Club & Co. (Cape Town) - Est. 2020", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withUnicodeCharactersInClubName_thenPreserves() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("UNI");
        clubRequest.setClubName("Café ☆ Club 日本");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("UNI", ipscResponse.getClub().getClubCode());
        assertEquals("Café ☆ Club 日本", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withEmptyClubCode_thenSetsEmptyString() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("");
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("", ipscResponse.getClub().getClubCode());
        assertEquals("Test Club", ipscResponse.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_withWhitespaceOnlyClubCode_thenSetsAsIs() {
        // Arrange
        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(new MatchResponse(matchRequest));

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("   ");
        clubRequest.setClubName("Test Club");

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(List.of(clubRequest));

        // Act
        ipscMatchService.addClubToMatch(ipscResponse, ipscRequestHolder);

        // Assert
        assertNotNull(ipscResponse.getClub());
        assertEquals(1, ipscResponse.getClub().getClubId());
        assertEquals("   ", ipscResponse.getClub().getClubCode());
        assertEquals("Test Club", ipscResponse.getClub().getClubName());
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
    // Tests for mapMatchResults - Valid Data Scenarios
    // =====================================================================

    @Test
    public void testMapMatchResults_withValidData_thenReturnsIpscResponses() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();


        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        holder.setClubs(List.of(clubRequest));

        MatchRequest match1 = new MatchRequest();
        match1.setMatchId(100);
        match1.setMatchName("Match 1");
        match1.setClubId(1);

        MatchRequest match2 = new MatchRequest();
        match2.setMatchId(200);
        match2.setMatchName("Match 2");
        match2.setClubId(1);
        holder.setMatches(List.of(match1, match2));

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
        assertEquals(1, responses.get(0).getStages().size());
        assertEquals(10, responses.get(0).getStages().getFirst().getStageId());

        assertEquals(200, responses.get(1).getMatch().getMatchId());
        assertEquals(1, responses.get(1).getStages().size());
        assertEquals(20, responses.get(1).getStages().getFirst().getStageId());
    }

    @Test
    public void testMapMatchResults_withNullMatchList_thenReturnsEmptyList() {
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
    public void testMapMatchResults_withNoMatchingClub_thenCreatesClubWithIdOnly() {
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
    public void testMapMatchResults_withEmptyMatchList_thenReturnsEmptyList() {
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
    public void testMapMatchResults_withNullMatchName_thenProcessesWithoutName() {
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
    public void testMapMatchResults_withEmptyMatchName_thenProcessesWithEmptyName() {
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
    public void testMapMatchResults_withBlankMatchName_thenProcessesWithBlankName() {
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
    public void testMapMatchResults_withNullStages_thenReturnsEmptyHolder() {
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
    public void testMapMatchResults_withNullMembers_thenProcessesWithoutMembers() {
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
    public void testMapMatchResults_withNullEnrolledMembers_thenReturnsEmptyHolder() {
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
    public void testMapMatchResults_withPartialData_thenMapsAvailableData() {
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
    public void testMapMatchResults_withPartialMembers_thenMapsOnlyMatchingMembers() {
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
    public void testMapMatchResults_withCompleteData_thenMapsAllData() {
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
    public void testMapMatchResults_withNullClubId_thenProcessesWithoutClubMatch() {
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
    public void testMapMatchResults_withNullStagesButNonNullMatches_thenReturnsEmptyHolder() {        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest match1 = new MatchRequest();
        match1.setMatchId(100);
        match1.setMatchName("Match 1");
        match1.setClubId(1);

        MatchRequest match2 = new MatchRequest();
        match2.setMatchId(101);
        match2.setMatchName("Match 2");
        match2.setClubId(1);
        holder.setMatches(List.of(match1, match2));

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
    public void testMapMatchResults_withStagesNotMatchingAnyMatch_thenIgnoresStages() {
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
    public void testMapMatchResults_withSpecialCharactersInMatchName_thenPreservesCharacters() {
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
    public void testMapMatchResults_withLargeNumberOfMatches_thenProcessesAllMatches() {
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
    // Tests for generateIpscMatchRecordHolder - Empty and Null Lists
    // =====================================================================

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
//        match.setClubName(ClubIdentifier.HPSC);
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
        assertEquals("Handgun", result.matches().get(0).matchFirearmType());
        assertEquals("Rifle", result.matches().get(1).matchFirearmType());
        assertEquals("League", result.matches().getFirst().matchCategory());
        assertEquals("Club Shoot", result.matches().get(1).matchCategory());
        assertEquals("2025-09-05 10:00", result.matches().getFirst().scheduledDate());
        assertEquals("2025-09-04 15:00", result.matches().get(1).scheduledDate());
        assertEquals("2025-09-06 10:00", result.matches().get(0).dateEdited());
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
        IpscMatchRecordHolder result = assertDoesNotThrow(() ->
                ipscMatchService.generateIpscMatchRecordHolder(matchList));

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
}
