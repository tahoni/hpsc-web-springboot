package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceTest {

    @InjectMocks
    private IpscMatchServiceImpl matchService;

    @Test
    void testMapMatchResults_withValidData_thenReturnsIpscResponses() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");
        holder.setClubs(List.of(clubRequest));

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(200);
        stageRequest.setStageName("Test Stage");
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
        IpscResponseHolder responseHolder = matchService.mapMatchResults(holder);

        // Assert
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getMatch());
        assertEquals(100, response.getMatch().getMatchId());
        assertEquals("Test Match", response.getMatch().getMatchName());

        assertNotNull(response.getStages());
        assertEquals(1, response.getStages().size());
        assertEquals(200, response.getStages().getFirst().getStageId());

        assertNotNull(response.getTags());
        assertEquals(1, response.getTags().size());
        assertEquals(10, response.getTags().getFirst().getTagId());

        assertNotNull(response.getEnrolledMembers());
        assertEquals(1, response.getEnrolledMembers().size());
        assertEquals(50, response.getEnrolledMembers().getFirst().getMemberId());

        assertNotNull(response.getScores());
        assertEquals(1, response.getScores().size());
        assertEquals(95, response.getScores().getFirst().getFinalScore());

        assertNotNull(response.getMembers());
        assertEquals(1, response.getMembers().size());
        assertEquals("John", response.getMembers().getFirst().getFirstName());
    }

    @Test
    void testMapMatchResults_withMultipleMatches_thenReturnsMultipleResponses() {
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
        IpscResponseHolder responseHolder = matchService.mapMatchResults(holder);

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
    void testMapMatchResults_withEmptyRequestHolderLists_thenReturnsEmptyList() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setClubs(new ArrayList<>());
        holder.setMatches(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        IpscResponseHolder responseHolder = matchService.mapMatchResults(holder);

        // Assert
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testMapMatchResults_withMatchButNoMembers_thenReturnsResponseWithEmptyMembers() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
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
        IpscResponseHolder responseHolder = matchService.mapMatchResults(holder);

        // Assert
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getMembers());
        assertTrue(response.getMembers().isEmpty());
    }

    @Test
    void testMapMatchResults_withEmptyRequestHolder_thenReturnsEmptyList() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        // Act
        IpscResponseHolder responseHolder = matchService.mapMatchResults(holder);

        // Assert
        List<IpscResponse> responses = responseHolder.getIpscList();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testMapMatchResults_withNullRequestHolder_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchService.mapMatchResults(null));
    }
}
