package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecord;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransformationServiceTest {

    @Mock
    private ClubEntityService clubEntityService;
    @Mock
    private MatchEntityService matchEntityService;
    @Mock
    private MatchStageEntityService matchStageEntityService;
    @Mock
    private CompetitorEntityService competitorEntityService;
    @Mock
    private MatchCompetitorEntityService matchCompetitorEntityService;
    @Mock
    private MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    @InjectMocks
    private TransformationServiceImpl transformationService;

    @Test
    public void testMapMatchResults_whenNullRequest_thenThrowsValidationException() {
        // Act / Assert
        assertThrows(ValidationException.class, () -> transformationService.mapMatchResults(null));
    }

    @Test
    public void testMapMatchResults_whenMatchesNull_thenReturnsEmptyHolder() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setMatches(null);

        // Act
        IpscResponseHolder result = transformationService.mapMatchResults(holder);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getIpscList());
        assertTrue(result.getIpscList().isEmpty());
    }

    @Test
    public void testMapMatchResults_whenValidInput_thenMapsMatchMembersAndClub() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setMatches(List.of(buildMatchRequest(1)));
        holder.setStages(List.of(buildStageRequest(1, 1), buildStageRequest(2, 1)));
        holder.setScores(List.of(buildScoreRequest(1, 1, 9, 50), buildScoreRequest(2, 1, 10, 60)));
        holder.setMembers(List.of(buildMemberRequest(9), buildMemberRequest(10)));
        holder.setEnrolledMembers(List.of(buildEnrolledRequest(9, 1)));
        holder.setTags(List.of(new TagRequest(null, 1, "Tag")));
        holder.setClubs(List.of(buildClubRequest(101)));

        // Act
        IpscResponseHolder result = transformationService.mapMatchResults(holder);

        // Assert
        assertEquals(1, result.getIpscList().size());
        IpscResponse response = result.getIpscList().getFirst();
        assertEquals(1, response.getMatch().getMatchId());
        assertEquals(1, response.getStages().size());
        assertEquals(1, response.getScores().size());
        assertEquals(2, response.getMembers().size());
        assertNotNull(response.getClub());
        assertEquals(101, response.getClub().getClubId());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenNullInput_thenReturnsEmptyHolder() {
        // Act
        var result = transformationService.generateIpscMatchRecordHolder(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.matches().isEmpty());
    }

    @Test
    public void testGenerateIpscMatchRecordHolder_whenValidEntities_thenReturnsRecords() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C100");
        competitor.setDateOfBirth(LocalDate.of(1991, 2, 2));

        Club club = new Club();
        club.setName("Holster Club");
        club.setAbbreviation("HPSC");

        IpscMatch match = new IpscMatch();
        match.setName("Main Match");
        match.setScheduledDate(LocalDateTime.of(2026, 3, 31, 10, 0));
        match.setClub(club);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.PRODUCTION);
        matchCompetitor.setPowerFactor(PowerFactor.MINOR);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.NONE);
        matchCompetitor.setMatchPoints(BigDecimal.valueOf(120.12));
        matchCompetitor.setMatchRanking(BigDecimal.valueOf(95.33));

        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");

        MatchStageCompetitor stageCompetitor = new MatchStageCompetitor();
        stageCompetitor.setCompetitor(competitor);
        stageCompetitor.setMatchStage(stage);
        stageCompetitor.setFirearmType(FirearmType.HANDGUN);
        stageCompetitor.setDivision(Division.PRODUCTION);
        stageCompetitor.setPowerFactor(PowerFactor.MINOR);
        stageCompetitor.setCompetitorCategory(CompetitorCategory.NONE);
        stageCompetitor.setStagePoints(BigDecimal.valueOf(55.1234));
        stageCompetitor.setStagePercentage(BigDecimal.valueOf(88.12));
        stageCompetitor.setStageRanking(BigDecimal.valueOf(2));
        stage.setMatchStageCompetitors(List.of(stageCompetitor));

        match.setMatchCompetitors(List.of(matchCompetitor));
        match.setMatchStages(List.of(stage));

        // Act
        var result = transformationService.generateIpscMatchRecordHolder(List.of(
                new MatchHolder(match, club,
                        List.of(stage), List.of(competitor), List.of(matchCompetitor), List.of(stageCompetitor))));

        // Assert
        assertEquals(1, result.matches().size());
        assertEquals("Main Match", result.matches().getFirst().name());
        assertEquals(1, result.matches().getFirst().competitors().size());
    }

    @Test
    public void testInitMatchResults_whenNullResponse_thenEmpty() {
        // Act / Assert
        assertTrue(transformationService.initMatchResults(null).isEmpty());
    }

    @Test
    public void testInitMatchResults_whenMissingMatch_thenEmpty() {
        // Arrange
        IpscResponse response = new IpscResponse();

        // Act / Assert
        assertTrue(transformationService.initMatchResults(response).isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenMatchNull_thenEmpty() {
        // Act / Assert
        Optional<IpscResponse> result = transformationService.createBasicMatch(new IpscRequestHolder(), null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenMatchIdNull_thenEmpty() {
        // Arrange
        MatchRequest request = buildMatchRequest(1);
        request.setMatchId(null);

        // Act / Assert
        Optional<IpscResponse> result = transformationService.createBasicMatch(new IpscRequestHolder(), request);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenRequiredCollectionsMissing_thenEmpty() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setStages(null);
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act / Assert
        Optional<IpscResponse> result = transformationService.createBasicMatch(holder, buildMatchRequest(1));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateBasicMatch_whenValid_thenFiltersByMatchId() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setTags(List.of(new TagRequest(null, 1, "Tag")));
        holder.setStages(List.of(buildStageRequest(1, 1), buildStageRequest(2, 1)));
        holder.setEnrolledMembers(List.of(buildEnrolledRequest(9, 1), buildEnrolledRequest(10, 2)));
        holder.setScores(List.of(buildScoreRequest(1, 1, 9, 10), buildScoreRequest(2, 1, 10, 20)));

        // Act
        Optional<IpscResponse> result = transformationService.createBasicMatch(holder, buildMatchRequest(1));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStages().size());
        assertEquals(1, result.get().getEnrolledMembers().size());
        assertEquals(1, result.get().getScores().size());
    }

    @Test
    public void testAddMembersToMatch_whenResponseNull_thenNoThrow() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setScores(new ArrayList<>());
        holder.setMembers(new ArrayList<>());

        // Act / Assert
        assertDoesNotThrow(() -> transformationService.addMembersToMatch(null, holder));
    }

    @Test
    public void testAddMembersToMatch_whenValid_thenSetsMappedMembers() {
        // Arrange
        IpscResponse response = new IpscResponse();
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setScores(List.of(buildScoreRequest(1, 1, 9, 20)));
        holder.setMembers(List.of(buildMemberRequest(9), buildMemberRequest(10)));

        // Act
        transformationService.addMembersToMatch(response, holder);

        // Assert
        assertEquals(1, response.getMembers().size());
        assertEquals(9, response.getMembers().getFirst().getMemberId());
    }

    @Test
    public void testAddClubToMatch_whenNullArgs_thenNoThrow() {
        // Act / Assert
        assertDoesNotThrow(() -> transformationService.addClubToMatch(null, null));
    }

    @Test
    public void testAddClubToMatch_whenClubFound_thenSetsFullClub() {
        // Arrange
        IpscResponse response = new IpscResponse();
        response.setMatch(new MatchResponse(1, "M1", LocalDateTime.now(), 500, 1, 1));
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setClubs(List.of(buildClubRequest(500)));

        // Act
        transformationService.addClubToMatch(response, holder);

        // Assert
        assertNotNull(response.getClub());
        assertEquals("Holster Club", response.getClub().getClubName());
    }

    @Test
    public void testAddClubToMatch_whenClubNotFound_thenSetsDefaultClubWithIdOnly() {
        // Arrange
        IpscResponse response = new IpscResponse();
        response.setMatch(new MatchResponse(1, "M1", LocalDateTime.now(), 501, 1, 1));
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setClubs(List.of(buildClubRequest(500)));

        // Act
        transformationService.addClubToMatch(response, holder);

        // Assert
        assertNotNull(response.getClub());
        assertEquals(501, response.getClub().getClubId());
    }

    @Test
    public void testInitIpscMatchResponse_whenNullInputs_thenEmpty() {
        // Act / Assert
        assertTrue(transformationService.initIpscMatchRecord(null, new Club(), new ArrayList<>()).isEmpty());
        assertTrue(transformationService.initIpscMatchRecord(new IpscMatch(), new Club(), null).isEmpty());
    }

    @Test
    public void testInitIpscMatchResponse_whenValid_thenReturnsRecord() {
        // Arrange
        Club club = new Club();
        club.setName("Holster Club");
        IpscMatch match = new IpscMatch();
        match.setName("Main Match");
        match.setScheduledDate(LocalDateTime.of(2026, 3, 31, 10, 0));

        // Act
        Optional<IpscMatchRecord> result = transformationService.initIpscMatchRecord(match, club, new ArrayList<>());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Main Match", result.get().name());
        assertEquals(club.toString(), result.get().clubName());
    }

    @Test
    public void testGetCompetitorSet_whenNull_thenEmptySet() {
        // Act / Assert
        assertTrue(transformationService.getCompetitorSet(null).isEmpty());
    }

    @Test
    public void testGetCompetitorSet_whenValid_thenUniqueCompetitorsOnly() {
        // Arrange
        Competitor competitor = new Competitor();
        List<Competitor> input = new ArrayList<>();
        input.add(competitor);
        input.add(competitor);
        input.add(null);

        // Act
        Set<Competitor> result = transformationService.getCompetitorSet(input);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.contains(null));
    }

    @Test
    public void testGetMatchCompetitorSet_whenNullMatch_thenEmptySet() {
        // Act / Assert
        assertTrue(transformationService.getMatchCompetitorSet(null).isEmpty());
    }

    @Test
    public void testGetMatchCompetitorSet_whenValid_thenFiltersNulls() {
        // Arrange
        MatchCompetitor mc = new MatchCompetitor();
        List<MatchCompetitor> input = new ArrayList<>();
        input.add(mc);
        input.add(null);

        // Act
        Set<MatchCompetitor> result = transformationService.getMatchCompetitorSet(input);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testGetMatchStageCompetitorSet_whenNull_thenEmptySet() {
        // Act / Assert
        assertTrue(transformationService.getMatchStageCompetitorSet(null).isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorSet_whenValid_thenFiltersNullsAndReturnsUniqueSet() {
        // Arrange
        MatchStageCompetitor c1 = new MatchStageCompetitor();
        List<MatchStageCompetitor> input = new ArrayList<>();
        input.add(c1);
        input.add(c1);
        input.add(null);

        // Act
        Set<MatchStageCompetitor> result = transformationService.getMatchStageCompetitorSet(input);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.contains(null));
    }

    @Test
    public void testInitClub_whenNull_thenEmpty() {
        // Act / Assert
        assertTrue(transformationService.initClub(null).isEmpty());
    }

    @Test
    public void testInitClub_whenClubFound_thenBuildsDtoFromEntityAndResponse() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse(1, "HPSC", "Holster Club");
        Club club = new Club();
        club.setId(99L);
        club.setName("Persisted Club");
        club.setAbbreviation("PC");
        when(clubEntityService.findClubByNameOrAbbreviation("Holster Club", "HPSC"))
                .thenReturn(Optional.of(club));

        // Act
        Optional<ClubDto> result = transformationService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(99L, result.get().getId());
        assertEquals("Holster Club", result.get().getName());
    }

    @Test
    public void testInitClub_whenClubNotFound_thenBuildsDtoFromResponse() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse(1, "HPSC", "Holster Club");
        when(clubEntityService.findClubByNameOrAbbreviation("Holster Club", "HPSC"))
                .thenReturn(Optional.empty());

        // Act
        Optional<ClubDto> result = transformationService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Holster Club", result.get().getName());
    }

    @Test
    public void testInitMatch_whenResponseOrMatchNull_thenEmpty() {
        // Act / Assert
        assertTrue(transformationService.initMatch(null, null).isEmpty());
        assertTrue(transformationService.initMatch(new IpscResponse(), null).isEmpty());
    }

    @Test
    public void testInitMatch_whenExistingMatchAndNewerScores_thenPresent() {
        // Arrange
        IpscResponse response = buildBaseIpscResponse(1);
        response.setScores(List.of(new ScoreResponse(1, 1, 9, 0, 0, 0, 0,
                0, 0, 0, "", false, "", 0, "", "", 0,
                false, LocalDateTime.of(2026, 3, 31, 12, 0))));

        IpscMatch existing = new IpscMatch();
        existing.setName("Existing");
        existing.setDateEdited(LocalDateTime.of(2026, 3, 31, 10, 0));
        when(matchEntityService.findMatchByNameAndScheduledDate(anyString(), any()))
                .thenReturn(Optional.of(existing));

        // Act
        Optional<MatchDto> result = transformationService.initMatch(response, new ClubDto());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIndex());
    }

    @Test
    public void testInitMatch_whenNoExistingMatch_thenPresent() {
        // Arrange
        IpscResponse response = buildBaseIpscResponse(1);
        when(matchEntityService.findMatchByNameAndScheduledDate(anyString(), any()))
                .thenReturn(Optional.empty());

        // Act
        Optional<MatchDto> result = transformationService.initMatch(response, null);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    public void testInitStages_whenNullArgs_thenEmptyList() {
        // Act / Assert
        assertTrue(transformationService.initStages(null, new ArrayList<>()).isEmpty());
        assertTrue(transformationService.initStages(new MatchDto(), null).isEmpty());
    }

    @Test
    public void testInitStages_whenValid_thenMapsStages() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);
        matchDto.setIndex(1);
        StageResponse stageResponse = new StageResponse(1, 1, "Stage 1", "Desc", 1,
                1, 10, 0, 0, 0, 0, 20, 100, 0, 0);

        when(matchStageEntityService.findMatchStage(1L, 1)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = transformationService.initStages(matchDto, List.of(stageResponse));

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getStageNumber());
    }

    @Test
    public void testInitCompetitors_whenInvalidInputs_thenEmpty() {
        // Arrange
        MatchResultsDto emptyResults = new MatchResultsDto();

        // Act / Assert
        assertTrue(transformationService.initCompetitors(emptyResults, null).isEmpty());
    }

    @Test
    public void testInitCompetitors_whenValidScores_thenReturnsOnlyScoringCompetitors() {
        // Arrange
        MatchResultsDto results = new MatchResultsDto();
        MatchDto match = new MatchDto();
        match.setIndex(1);
        results.setMatch(match);

        IpscResponse response = buildBaseIpscResponse(1);
        response.setScores(List.of(
                new ScoreResponse(1, 1, 9, 0, 0, 0, 0, 0,
                        0, 0, "", false, "", 0, "", "", 0, false, LocalDateTime.now()),
                new ScoreResponse(1, 1, 10, 0, 0, 0, 0, 0,
                        0, 0, "", false, "", 0, "", "", 50, false, LocalDateTime.now())
        ));
        response.setMembers(List.of(
                new MemberResponse(9, "Doe", "John", "", false,
                        LocalDateTime.of(1990, 1, 1, 0, 0), "111", "BBB", true, null, null, null),
                new MemberResponse(10, "Poe", "Jane", "", false,
                        LocalDateTime.of(1991, 1, 1, 0, 0), "222", "BBB", true, null, null, null)
        ));

        when(competitorEntityService.findCompetitor(anyString(), anyString(), anyString(), any()))
                .thenReturn(Optional.empty());

        // Act
        List<CompetitorDto> result = transformationService.initCompetitors(results, response);

        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.getFirst().getIndexes().getFirst());
    }

    @Test
    public void testInitScores_whenInvalidInputs_thenNoChanges() {
        // Arrange
        MatchResultsDto results = new MatchResultsDto();
        results.setMatch(null);
        results.setMatchCompetitors(new ArrayList<>());
        results.setMatchStageCompetitors(new ArrayList<>());

        IpscResponse response = new IpscResponse();
        response.setScores(new ArrayList<>());
        response.setMembers(new ArrayList<>());

        // Act / Assert
        assertDoesNotThrow(() -> transformationService.initEnrolledCompetitors(results, response));
        assertTrue(results.getMatchCompetitors().isEmpty());
    }

    // Helper methods

    private MatchRequest buildMatchRequest(int matchId) {
        MatchRequest request = new MatchRequest();
        request.setMatchId(matchId);
        request.setMatchName("Match " + matchId);
        request.setClubId(100 + matchId);
        request.setMatchDate(LocalDateTime.of(2026, 3, 31, 10, 0));
        request.setFirearmId(1);
        return request;
    }

    private StageRequest buildStageRequest(int matchId, int stageId) {
        StageRequest request = new StageRequest();
        request.setMatchId(matchId);
        request.setStageId(stageId);
        request.setStageName("Stage " + stageId);
        request.setTargetPaper(10);
        request.setMaxPoints(100);
        request.setMinRounds(20);
        return request;
    }

    private ScoreRequest buildScoreRequest(int matchId, int stageId, int memberId, int finalScore) {
        ScoreRequest request = new ScoreRequest();
        request.setMatchId(matchId);
        request.setStageId(stageId);
        request.setMemberId(memberId);
        request.setFinalScore(finalScore);
        request.setScoreA(5);
        request.setHitFactor("2.25");
        request.setTime("12.11");
        request.setDeduction(false);
        request.setLastModified(LocalDateTime.of(2026, 3, 31, 11, 0));
        return request;
    }

    private MemberRequest buildMemberRequest(int memberId) {
        MemberRequest request = new MemberRequest();
        request.setMemberId(memberId);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDateTime.of(1990, 1, 1, 0, 0));
        request.setFemale(false);
        request.setIcsAlias("1234");
        request.setRefNo("BBB");
        request.setIsRegisteredForMatch(true);
        return request;
    }

    private EnrolledRequest buildEnrolledRequest(int memberId, int matchId) {
        EnrolledRequest request = new EnrolledRequest();
        request.setMemberId(memberId);
        request.setCompetitorId(memberId);
        request.setMatchId(matchId);
        request.setDivisionId(1);
        request.setRefNo("BBB");
        request.setCompetitorCategoryId(2);
        request.setMajorPowerFactor(true);
        request.setScoreClassificationId(true);
        request.setIsDisqualified(false);
        return request;
    }

    private ClubRequest buildClubRequest(int clubId) {
        ClubRequest request = new ClubRequest();
        request.setClubId(clubId);
        request.setClubCode("HPSC");
        request.setClubName("Holster Club");
        return request;
    }

    private IpscResponse buildBaseIpscResponse(int matchId) {
        IpscResponse response = new IpscResponse();
        response.setMatch(new MatchResponse(matchId, "Match " + matchId,
                LocalDateTime.of(2026, 3, 31, 10, 0), 200, 3, 1));
        return response;
    }
}

