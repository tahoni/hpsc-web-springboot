package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceImplTest {
    @InjectMocks
    private IpscMatchServiceImpl ipscMatchService;

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
        assertEquals(1, responses.getFirst().getStages().size());
        assertEquals(10, responses.getFirst().getStages().getFirst().getStageId());

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
}
