package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    // ─── initCompetitorRecord ───────────────────────────────────────────────────

    @Test
    public void testInitCompetitorRecord_whenAnyArgIsNull_thenEmpty() {
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        CompetitorResultRecord result = buildCompetitorResultRecord();

        assertTrue(transformationService.initCompetitorRecord(null, matchCompetitor, result).isEmpty());
        assertTrue(transformationService.initCompetitorRecord(competitor, null, result).isEmpty());
        assertTrue(transformationService.initCompetitorRecord(competitor, matchCompetitor, null).isEmpty());
    }

    @Test
    public void testInitCompetitorRecord_whenValidInputsWithMatchClub_thenReturnsRecordWithClubFromMatchClub() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        ClubIdentifier clubIdentifier = ClubIdentifier.HPSC;
        matchCompetitor.setMatchClub(clubIdentifier);
        CompetitorResultRecord resultRecord = buildCompetitorResultRecord();

        // Act
        Optional<CompetitorRecord> result =
                transformationService.initCompetitorRecord(competitor, matchCompetitor, resultRecord);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().firstName());
        assertEquals("Doe", result.get().lastName());
        assertEquals(clubIdentifier.getName(), result.get().clubName());
    }

    @Test
    public void testInitCompetitorRecord_whenMatchClubIsNullButMatchHasClub_thenReturnsRecordWithClubFromMatch() {
        // Arrange
        Competitor competitor = buildCompetitor("Jane", "Smith");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        matchCompetitor.setMatchClub(null);
        Club club = new Club();
        club.setName("Test Club");
        IpscMatch match = new IpscMatch();
        match.setClub(club);
        match.setScheduledDate(LocalDateTime.now());
        matchCompetitor.setMatch(match);
        CompetitorResultRecord resultRecord = buildCompetitorResultRecord();

        // Act
        Optional<CompetitorRecord> result =
                transformationService.initCompetitorRecord(competitor, matchCompetitor, resultRecord);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Club", result.get().clubName());
    }

    @Test
    public void testInitCompetitorRecord_whenNoClubAvailable_thenReturnsRecordWithEmptyClubName() {
        // Arrange
        Competitor competitor = buildCompetitor("Alice", "Brown");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        matchCompetitor.setMatchClub(null);
        CompetitorResultRecord resultRecord = buildCompetitorResultRecord();

        // Act
        Optional<CompetitorRecord> result =
                transformationService.initCompetitorRecord(competitor, matchCompetitor, resultRecord);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().clubName());
    }

    // ─── initCompetitorResult ───────────────────────────────────────────────────

    @Test
    public void testInitCompetitorResult_whenCompetitorIsNull_thenEmpty() {
        MatchCompetitor matchCompetitor = buildMatchCompetitor(new Competitor());
        MatchCompetitorOverallResultsRecord overall = new MatchCompetitorOverallResultsRecord("100.0000", "95.00", "");
        List<MatchCompetitorStageResultRecord> stages = List.of(buildStageResultRecord());

        assertTrue(transformationService.initCompetitorResult(null, matchCompetitor, overall, stages).isEmpty());
    }

    @Test
    public void testInitCompetitorResult_whenOverallResultIsNull_thenEmpty() {
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        List<MatchCompetitorStageResultRecord> stages = List.of(buildStageResultRecord());

        assertTrue(transformationService.initCompetitorResult(competitor, matchCompetitor, null, stages).isEmpty());
    }

    @Test
    public void testInitCompetitorResult_whenStageResultsAreNull_thenEmpty() {
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        MatchCompetitorOverallResultsRecord overall = new MatchCompetitorOverallResultsRecord("100.0000", "95.00", "");

        assertTrue(transformationService.initCompetitorResult(competitor, matchCompetitor, overall, null).isEmpty());
    }

    @Test
    public void testInitCompetitorResult_whenValidInputs_thenReturnsRecordWithFirearmDivisionAndPowerFactor() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor matchCompetitor = buildMatchCompetitor(competitor);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.PRODUCTION);
        matchCompetitor.setPowerFactor(PowerFactor.MINOR);
        MatchCompetitorOverallResultsRecord overall = new MatchCompetitorOverallResultsRecord("100.0000", "95.00", "");
        List<MatchCompetitorStageResultRecord> stages = List.of(buildStageResultRecord());

        // Act
        Optional<CompetitorResultRecord> result =
                transformationService.initCompetitorResult(competitor, matchCompetitor, overall, stages);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.HANDGUN.toString(), result.get().firearmType());
        assertEquals(Division.PRODUCTION.toString(), result.get().division());
        assertEquals(PowerFactor.MINOR.toString(), result.get().powerFactor());
    }

    // ─── initMatchCompetitorOverallResult ───────────────────────────────────────

    @Test
    public void testInitMatchCompetitorOverallResult_whenCompetitorIsNull_thenEmpty() {
        assertTrue(transformationService.initMatchCompetitorOverallResult(null, new ArrayList<>()).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorOverallResult_whenMatchCompetitorListIsNull_thenEmpty() {
        assertTrue(transformationService.initMatchCompetitorOverallResult(new Competitor(), null).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorOverallResult_whenNoMatchingCompetitorInList_thenEmpty() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        Competitor other = buildCompetitor("Jane", "Smith");
        MatchCompetitor mc = buildMatchCompetitor(other);

        // Act / Assert
        assertTrue(transformationService.initMatchCompetitorOverallResult(competitor, List.of(mc)).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorOverallResult_whenMatchingCompetitorFound_thenReturnsFormattedPoints() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        MatchCompetitor mc = buildMatchCompetitor(competitor);
        mc.setMatchPoints(new BigDecimal("245.6700"));
        mc.setMatchRanking(new BigDecimal("98.25"));

        // Act
        Optional<MatchCompetitorOverallResultsRecord> result =
                transformationService.initMatchCompetitorOverallResult(competitor, List.of(mc));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("245.6700", result.get().matchPoints());
        assertEquals("98.25", result.get().matchRanking());
    }

    @Test
    public void testInitMatchCompetitorOverallResult_whenNullPointsAndRanking_thenReturnsEmptyFormattedValues() {
        // Arrange
        Competitor competitor = buildCompetitor("Alice", "Brown");
        MatchCompetitor mc = buildMatchCompetitor(competitor);
        mc.setMatchPoints(null);
        mc.setMatchRanking(null);

        // Act
        Optional<MatchCompetitorOverallResultsRecord> result =
                transformationService.initMatchCompetitorOverallResult(competitor, List.of(mc));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().matchPoints());
        assertEquals("", result.get().matchRanking());
    }

    // ─── initMatchCompetitorStageResults ────────────────────────────────────────

    @Test
    public void testInitMatchCompetitorStageResults_whenCompetitorIsNull_thenEmptyList() {
        assertTrue(transformationService.initMatchCompetitorStageResults(null, new ArrayList<>()).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorStageResults_whenListIsNull_thenEmptyList() {
        assertTrue(transformationService.initMatchCompetitorStageResults(new Competitor(), null).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorStageResults_whenNoMatchingCompetitorInList_thenEmptyList() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        Competitor other = buildCompetitor("Jane", "Smith");
        IpscMatchStage stage = buildMatchStage("Stage 1", 1);
        MatchStageCompetitor msc = buildMatchStageCompetitor(other, stage);

        // Act / Assert
        assertTrue(transformationService.initMatchCompetitorStageResults(competitor, List.of(msc)).isEmpty());
    }

    @Test
    public void testInitMatchCompetitorStageResults_whenValidEntry_thenReturnsMappedStageRecord() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        IpscMatchStage stage = buildMatchStage("El Presidente", 1);
        MatchStageCompetitor msc = buildMatchStageCompetitor(competitor, stage);
        msc.setTime(new BigDecimal("38.25"));
        msc.setHitFactor(new BigDecimal("4.5813"));
        msc.setStagePoints(new BigDecimal("175.0000"));
        msc.setStagePercentage(new BigDecimal("95.50"));
        msc.setStageRanking(BigDecimal.ONE);

        // Act
        List<MatchCompetitorStageResultRecord> results =
                transformationService.initMatchCompetitorStageResults(competitor, List.of(msc));

        // Assert
        assertEquals(1, results.size());
        assertEquals("El Presidente", results.getFirst().stageName());
        assertEquals("38.25", results.getFirst().time());
        assertEquals("4.5813", results.getFirst().hitFactor());
        assertEquals("175.0000", results.getFirst().stagePoints());
    }

    @Test
    public void testInitMatchCompetitorStageResults_whenMultipleStagesForCompetitor_thenReturnsAllStages() {
        // Arrange
        Competitor competitor = buildCompetitor("John", "Doe");
        IpscMatchStage stage1 = buildMatchStage("Stage 1", 1);
        IpscMatchStage stage2 = buildMatchStage("Stage 2", 2);
        MatchStageCompetitor msc1 = buildMatchStageCompetitor(competitor, stage1);
        MatchStageCompetitor msc2 = buildMatchStageCompetitor(competitor, stage2);

        // Act
        List<MatchCompetitorStageResultRecord> results =
                transformationService.initMatchCompetitorStageResults(competitor, List.of(msc1, msc2));

        // Assert
        assertEquals(2, results.size());
    }

    // ─── deDuplicateCompetitorDtoList ───────────────────────────────────────────

    @Test
    public void testDeDuplicateCompetitorDtoList_whenNull_thenReturnsNull() {
        assertNull(transformationService.deDuplicateCompetitorDtoList(null));
    }

    @Test
    public void testDeDuplicateCompetitorDtoList_whenEmptyMap_thenReturnsEmptyMap() {
        assertTrue(transformationService.deDuplicateCompetitorDtoList(new LinkedHashMap<>()).isEmpty());
    }

    @Test
    public void testDeDuplicateCompetitorDtoList_whenNoDuplicates_thenReturnsAllEntries() {
        // Arrange
        CompetitorDto c1 = new CompetitorDto();
        c1.setSapsaNumber(1001);
        c1.setId(1L);
        CompetitorDto c2 = new CompetitorDto();
        c2.setSapsaNumber(1002);
        c2.setId(2L);
        Map<Integer, CompetitorDto> input = new LinkedHashMap<>();
        input.put(1, c1);
        input.put(2, c2);

        // Act
        Map<Integer, CompetitorDto> result = transformationService.deDuplicateCompetitorDtoList(input);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testDeDuplicateCompetitorDtoList_whenDuplicateSapsaNumbers_thenDeduplicatesAndMergesIndexes() {
        // Arrange
        CompetitorDto c1 = new CompetitorDto();
        c1.setSapsaNumber(1001);
        c1.getIndexes().add(9);

        CompetitorDto c2 = new CompetitorDto();
        c2.setSapsaNumber(1001);
        c2.getIndexes().add(10);

        Map<Integer, CompetitorDto> input = new LinkedHashMap<>();
        input.put(9, c1);
        input.put(10, c2);

        // Act
        Map<Integer, CompetitorDto> result = transformationService.deDuplicateCompetitorDtoList(input);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(9).getIndexes().contains(9));
        assertTrue(result.get(9).getIndexes().contains(10));
    }

    @Test
    public void testDeDuplicateCompetitorDtoList_whenDuplicateIds_thenDeduplicatesAndMergesIndexes() {
        // Arrange
        CompetitorDto c1 = new CompetitorDto();
        c1.setId(5L);
        c1.getIndexes().add(1);

        CompetitorDto c2 = new CompetitorDto();
        c2.setId(5L);
        c2.getIndexes().add(2);

        Map<Integer, CompetitorDto> input = new LinkedHashMap<>();
        input.put(1, c1);
        input.put(2, c2);

        // Act
        Map<Integer, CompetitorDto> result = transformationService.deDuplicateCompetitorDtoList(input);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(1).getIndexes().contains(1));
        assertTrue(result.get(1).getIndexes().contains(2));
    }

    @Test
    public void testDeDuplicateCompetitorDtoList_whenNullSapsaAndNullId_thenKeepsBothEntries() {
        // Arrange
        CompetitorDto c1 = new CompetitorDto();
        c1.setSapsaNumber(null);
        c1.setId(null);

        CompetitorDto c2 = new CompetitorDto();
        c2.setSapsaNumber(null);
        c2.setId(null);

        Map<Integer, CompetitorDto> input = new LinkedHashMap<>();
        input.put(1, c1);
        input.put(2, c2);

        // Act
        Map<Integer, CompetitorDto> result = transformationService.deDuplicateCompetitorDtoList(input);

        // Assert
        assertEquals(2, result.size());
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

    private Competitor buildCompetitor(String firstName, String lastName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return competitor;
    }

    private MatchCompetitor buildMatchCompetitor(Competitor competitor) {
        MatchCompetitor mc = new MatchCompetitor();
        mc.setCompetitor(competitor);
        mc.setCompetitorCategory(CompetitorCategory.NONE);
        return mc;
    }

    private IpscMatchStage buildMatchStage(String name, int number) {
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName(name);
        stage.setStageNumber(number);
        return stage;
    }

    private MatchStageCompetitor buildMatchStageCompetitor(Competitor competitor, IpscMatchStage stage) {
        MatchStageCompetitor msc = new MatchStageCompetitor();
        msc.setCompetitor(competitor);
        msc.setMatchStage(stage);
        msc.setCompetitorCategory(CompetitorCategory.NONE);
        return msc;
    }

    private CompetitorResultRecord buildCompetitorResultRecord() {
        MatchCompetitorOverallResultsRecord overall = new MatchCompetitorOverallResultsRecord("100.0000", "95.00", "");
        return new CompetitorResultRecord("Handgun", "Production", "Minor", overall, new ArrayList<>());
    }

    private MatchCompetitorStageResultRecord buildStageResultRecord() {
        return new MatchCompetitorStageResultRecord("Stage 1", 10, 5, 2, 1, 50, 0, 0, 0,
                "15.50", "3.22", "50.0000", "85.00", "1.00", "");
    }
}

