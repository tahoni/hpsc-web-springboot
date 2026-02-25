package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscMatchEntityServiceImplTest {

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchService;

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
}
