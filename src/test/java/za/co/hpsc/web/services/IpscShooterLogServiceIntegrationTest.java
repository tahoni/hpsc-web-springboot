package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscShooterLogService} - exercised through the interface type, with a
 * real Spring-wired {@code IpscShooterLogServiceImpl} bean backed by the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscShooterLogServiceIntegrationTest {

    @Autowired
    private IpscShooterLogService ipscShooterLogService;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private ShooterLogRepository shooterLogRepository;

    @Autowired
    private ShooterLogOverallRepository shooterLogOverallRepository;

    // createShooterLog()
    @Test
    void testCreateShooterLog_whenRequestIsValid_thenPersistsTheLogWithItsMatches() {
        // Arrange
        IpscMatch first = createMatch("Match A");
        IpscMatch second = createMatch("Match B");
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(first.getId(), second.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.createShooterLog(request);

        // Assert
        assertNotNull(response.getShooterLogId());
        assertEquals(List.of(first.getId(), second.getId()), response.getMatchIds());
        assertTrue(shooterLogRepository.existsByMatchesId(first.getId()));
    }

    @Test
    void testCreateShooterLog_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(999_999L));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogService.createShooterLog(request));
    }

    @Test
    void testCreateShooterLog_whenAMatchIsOutsideTheDates_thenThrowsValidationException() {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 10, 1));
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(match.getId()));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLog(request));
    }

    @Test
    void testCreateShooterLog_whenNoMatches_thenCreatesAnEmptyLog() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null);

        // Act
        ShooterLogResponse response = ipscShooterLogService.createShooterLog(request);

        // Assert
        assertNotNull(response.getShooterLogId());
        assertTrue(response.getMatchIds().isEmpty());
    }

    // deleteShooterLog()
    @Test
    void testDeleteShooterLog_whenLogHasOverallResults_thenThrowsValidationExceptionAndKeepsTheLog() {
        // Arrange
        ShooterLog shooterLog = createShooterLog();
        createShooterLogOverall(shooterLog, createCompetitor("HPSC-SL-001"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.deleteShooterLog(shooterLog.getId()));
        assertTrue(shooterLogRepository.existsById(shooterLog.getId()));
    }

    @Test
    void testDeleteShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogService.deleteShooterLog(999_999L));
    }

    @Test
    void testDeleteShooterLog_whenLogHasMatchesButNoOverallResults_thenDeletesItAndKeepsTheMatches() {
        // Arrange
        IpscMatch match = createMatch("Match A");
        ShooterLog shooterLog = createShooterLog(match);

        // Act
        ipscShooterLogService.deleteShooterLog(shooterLog.getId());

        // Assert
        assertFalse(shooterLogRepository.existsById(shooterLog.getId()));
        assertTrue(ipscMatchRepository.existsById(match.getId()));
        assertFalse(shooterLogRepository.existsByMatchesId(match.getId()));
    }

    // getAllShooterLogs()
    @Test
    void testGetAllShooterLogs_whenLogsExist_thenOrdersThemByStartDate() {
        // Arrange
        ShooterLog later = createShooterLog(LocalDate.of(2090, 6, 1));
        ShooterLog earlier = createShooterLog(LocalDate.of(2090, 1, 1));

        // Act
        List<Long> ids = ipscShooterLogService.getAllShooterLogs().stream()
                .map(ShooterLogResponse::getShooterLogId)
                .toList();

        // Assert
        assertTrue(ids.indexOf(earlier.getId()) < ids.indexOf(later.getId()));
    }

    // getShooterLog()
    @Test
    void testGetShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogService.getShooterLog(999_999L));
    }

    @Test
    void testGetShooterLog_whenLogExists_thenReturnsItsMatchIds() {
        // Arrange
        IpscMatch match = createMatch("Match A");
        ShooterLog shooterLog = createShooterLog(match);

        // Act
        ShooterLogResponse response = ipscShooterLogService.getShooterLog(shooterLog.getId());

        // Assert
        assertEquals(List.of(match.getId()), response.getMatchIds());
    }

    // patchShooterLog()
    @Test
    void testPatchShooterLog_whenNewDatesLeaveAMatchOutside_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setStartDate(LocalDate.of(2026, 9, 20));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscShooterLogService.patchShooterLog(shooterLog.getId(), request));
    }

    @Test
    void testPatchShooterLog_whenOnlyMatchIdsProvided_thenReplacesTheMatchesAndKeepsTheDates() {
        // Arrange
        IpscMatch original = createMatch("Match A");
        IpscMatch replacement = createMatch("Match B");
        ShooterLog shooterLog = createShooterLog(original);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setMatchIds(List.of(replacement.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(shooterLog.getId(), request);

        // Assert
        assertEquals(List.of(replacement.getId()), response.getMatchIds());
        assertEquals(LocalDate.of(2026, 9, 1), response.getStartDate());
        assertFalse(shooterLogRepository.existsByMatchesId(original.getId()));
    }

    // updateShooterLog()
    @Test
    void testUpdateShooterLog_whenRequestIsValid_thenReplacesDatesAndMatches() {
        // Arrange
        IpscMatch original = createMatch("Match A");
        IpscMatch replacement = createMatch("Match B", LocalDate.of(2026, 10, 15));
        ShooterLog shooterLog = createShooterLog(original);
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                List.of(replacement.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.updateShooterLog(shooterLog.getId(), request);

        // Assert
        assertEquals(LocalDate.of(2026, 10, 1), response.getStartDate());
        assertEquals(LocalDate.of(2026, 10, 31), response.getEndDate());
        assertEquals(List.of(replacement.getId()), response.getMatchIds());
    }

    // createShooterLogs()
    @Test
    void testCreateShooterLogs_whenRowsAreValidAndInvalid_thenCreatesTheValidOnesAndReportsTheRest()
            throws FatalException {
        // Arrange
        IpscMatch match = createMatch("Match A");
        IpscMatch lateMatch = createMatch("Match B", LocalDate.of(2026, 11, 14));
        String csvData = String.format("""
                StartDate,EndDate,MatchIds
                2026-09-01,2026-09-30,%d
                2026-09-01,2026-09-30,999999
                2026-09-01,2026-09-30,%d
                2026-10-01,2026-10-31,
                """, match.getId(), lateMatch.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.createShooterLogs(csvData);

        // Assert
        List<ShooterLogBulkResponse> results = holder.getShooterLogResults();
        assertEquals(4, results.size());
        assertTrue(results.get(0).isSuccess());
        assertEquals(List.of(match.getId()), results.get(0).getShooterLog().getMatchIds());
        assertFalse(results.get(1).isSuccess());
        assertEquals("No IPSC match found with ID 999999", results.get(1).getMessage());
        assertEquals("999999", results.get(1).getShooterLogRow().getMatchIds());
        assertFalse(results.get(2).isSuccess());
        assertTrue(results.get(2).getMessage().contains("outside the shooter log's dates"));
        assertTrue(results.get(3).isSuccess());
        assertEquals(2, shooterLogRepository.findAllWithMatches().size());
    }

    @Test
    void testCreateShooterLogs_whenCsvIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLogs("   "));
    }

    @Test
    void testPatchShooterLog_whenMatchesAreAdded_thenKeepsTheExistingOnesAndAddsTheNewOnce() {
        // Arrange
        IpscMatch existing = createMatch("Match A");
        IpscMatch added = createMatch("Match B", LocalDate.of(2026, 9, 20));
        ShooterLog shooterLog = createShooterLog(existing);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(added.getId(), existing.getId(), added.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(shooterLog.getId(), request);

        // Assert
        assertEquals(List.of(existing.getId(), added.getId()), response.getMatchIds());
    }

    @Test
    void testPatchShooterLog_whenMatchesAreRemoved_thenDropsThemAndIgnoresOnesTheLogDoesNotCover() {
        // Arrange
        IpscMatch kept = createMatch("Match A");
        IpscMatch removed = createMatch("Match B");
        IpscMatch notCovered = createMatch("Match C");
        ShooterLog shooterLog = createShooterLog(kept, removed);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setRemoveMatchIds(List.of(removed.getId(), notCovered.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(shooterLog.getId(), request);

        // Assert
        assertEquals(List.of(kept.getId()), response.getMatchIds());
    }

    @Test
    void testPatchShooterLog_whenMatchIdsAreReplacedAndAddedAndRemoved_thenAppliesThemInThatOrder() {
        // Arrange
        IpscMatch original = createMatch("Match A");
        IpscMatch replacement = createMatch("Match B");
        IpscMatch added = createMatch("Match C");
        ShooterLog shooterLog = createShooterLog(original);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setMatchIds(List.of(replacement.getId()));
        request.setAddMatchIds(List.of(added.getId()));
        request.setRemoveMatchIds(List.of(replacement.getId()));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(shooterLog.getId(), request);

        // Assert
        assertEquals(List.of(added.getId()), response.getMatchIds());
    }

    @Test
    void testPatchShooterLog_whenAMatchIsBothAddedAndRemoved_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(7L));
        request.setRemoveMatchIds(List.of(7L));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscShooterLogService.patchShooterLog(shooterLog.getId(), request));
    }

    @Test
    void testPatchShooterLog_whenAnAddedMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(999_999L));

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscShooterLogService.patchShooterLog(shooterLog.getId(), request));
    }

    @Test
    void testPatchShooterLog_whenAnAddedMatchIsOutsideTheLogsDates_thenThrowsValidationException() {
        // Arrange
        IpscMatch late = createMatch("Match B", LocalDate.of(2026, 11, 14));
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(late.getId()));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscShooterLogService.patchShooterLog(shooterLog.getId(), request));
    }

    // updateShooterLogs()
    @Test
    void testUpdateShooterLogs_whenRowsAreValidAndInvalid_thenReplacesTheValidOnesAndReportsTheRest()
            throws FatalException {
        // Arrange
        IpscMatch original = createMatch("Match A");
        IpscMatch replacement = createMatch("Match B", LocalDate.of(2026, 10, 12));
        ShooterLog first = createShooterLog(original);
        ShooterLog second = createShooterLog(original);
        String csvData = String.format("""
                ShooterLogId,StartDate,EndDate,MatchIds
                %d,2026-10-01,2026-10-31,%d
                999999,2026-10-01,2026-10-31,
                ,2026-10-01,2026-10-31,
                %d,2026-10-01,2026-10-31,999999
                %d,,2026-10-31,
                """, first.getId(), replacement.getId(), second.getId(), second.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.updateShooterLogs(csvData);

        // Assert
        List<ShooterLogBulkResponse> results = holder.getShooterLogResults();
        assertEquals(5, results.size());
        assertTrue(results.get(0).isSuccess());
        assertEquals(List.of(replacement.getId()), results.get(0).getShooterLog().getMatchIds());
        assertEquals(LocalDate.of(2026, 10, 1), results.get(0).getShooterLog().getStartDate());
        assertFalse(results.get(1).isSuccess());
        assertEquals("No shooter log found with ID 999999", results.get(1).getMessage());
        assertEquals("999999", results.get(1).getShooterLogRow().getShooterLogId());
        assertFalse(results.get(2).isSuccess());
        assertEquals("Shooter log ID is required.", results.get(2).getMessage());
        assertEquals("", results.get(2).getShooterLogRow().getShooterLogId());
        assertFalse(results.get(3).isSuccess());
        assertEquals("No IPSC match found with ID 999999", results.get(3).getMessage());
        assertFalse(results.get(4).isSuccess());
        assertEquals("Shooter log start date is required.", results.get(4).getMessage());
        assertEquals(String.valueOf(second.getId()), results.get(4).getShooterLogRow().getShooterLogId());
        // The failed rows left the second log as it was
        assertEquals(List.of(original.getId()), ipscShooterLogService.getShooterLog(second.getId()).getMatchIds());
    }

    @Test
    void testUpdateShooterLogs_whenMatchIdsAreEmpty_thenClearsTheLogsMatches() throws FatalException {
        // Arrange
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        String csvData = String.format("ShooterLogId,StartDate,EndDate,MatchIds%n%d,2026-09-01,2026-09-30,%n",
                shooterLog.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.updateShooterLogs(csvData);

        // Assert
        assertTrue(holder.getShooterLogResults().getFirst().isSuccess());
        assertTrue(ipscShooterLogService.getShooterLog(shooterLog.getId()).getMatchIds().isEmpty());
    }

    @Test
    void testUpdateShooterLogs_whenCsvIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.updateShooterLogs("   "));
    }

    // patchShooterLogs()
    @Test
    void testPatchShooterLogs_whenColumnsAreEmpty_thenLeavesThoseFieldsUnchanged() throws FatalException {
        // Arrange
        IpscMatch match = createMatch("Match A");
        ShooterLog shooterLog = createShooterLog(match);
        String csvData = String.format("""
                ShooterLogId,StartDate,EndDate,MatchIds
                %d,,2026-10-15,
                """, shooterLog.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.patchShooterLogs(csvData);

        // Assert
        ShooterLogBulkResponse result = holder.getShooterLogResults().getFirst();
        assertTrue(result.isSuccess());
        assertEquals(LocalDate.of(2026, 9, 1), result.getShooterLog().getStartDate());
        assertEquals(LocalDate.of(2026, 10, 15), result.getShooterLog().getEndDate());
        assertEquals(List.of(match.getId()), result.getShooterLog().getMatchIds());
    }

    @Test
    void testPatchShooterLogs_whenMatchIdsAreGiven_thenReplacesTheLogsMatches() throws FatalException {
        // Arrange
        IpscMatch replacement = createMatch("Match B");
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        String csvData = String.format("ShooterLogId,MatchIds%n%d,%d%n", shooterLog.getId(), replacement.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.patchShooterLogs(csvData);

        // Assert
        assertEquals(List.of(replacement.getId()), holder.getShooterLogResults().getFirst().getShooterLog().getMatchIds());
    }

    @Test
    void testPatchShooterLogs_whenRowsAreInvalid_thenReportsEachOne() throws FatalException {
        // Arrange
        IpscMatch lateMatch = createMatch("Match A", LocalDate.of(2026, 11, 14));
        ShooterLog shooterLog = createShooterLog(createMatch("Match B"));
        // A separate log, as the failed row before it leaves the first one's dates changed in this test's session
        ShooterLog otherLog = createShooterLog(createMatch("Match C"));
        String csvData = String.format("""
                ShooterLogId,StartDate,EndDate,MatchIds
                999999,2026-09-02,,
                ,2026-09-02,,
                %d,2026-10-31,2026-10-01,
                %d,,,%d
                """, shooterLog.getId(), otherLog.getId(), lateMatch.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.patchShooterLogs(csvData);

        // Assert
        List<ShooterLogBulkResponse> results = holder.getShooterLogResults();
        assertEquals(4, results.size());
        assertTrue(results.stream().noneMatch(ShooterLogBulkResponse::isSuccess));
        assertEquals("No shooter log found with ID 999999", results.get(0).getMessage());
        assertEquals("Shooter log ID is required.", results.get(1).getMessage());
        assertEquals("Shooter log end date cannot be before its start date.", results.get(2).getMessage());
        assertTrue(results.get(3).getMessage().contains("outside the shooter log's dates"));
    }

    @Test
    void testPatchShooterLogs_whenAddAndRemoveMatchIdsAreGiven_thenAddsAndRemovesThem() throws FatalException {
        // Arrange
        IpscMatch kept = createMatch("Match A");
        IpscMatch removed = createMatch("Match B");
        IpscMatch added = createMatch("Match C", LocalDate.of(2026, 9, 20));
        ShooterLog shooterLog = createShooterLog(kept, removed);
        String csvData = String.format("ShooterLogId,AddMatchIds,RemoveMatchIds%n%d,%d,%d%n",
                shooterLog.getId(), added.getId(), removed.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.patchShooterLogs(csvData);

        // Assert
        ShooterLogBulkResponse result = holder.getShooterLogResults().getFirst();
        assertTrue(result.isSuccess());
        assertEquals(List.of(kept.getId(), added.getId()), result.getShooterLog().getMatchIds());
    }

    @Test
    void testPatchShooterLogs_whenAMatchIsBothAddedAndRemoved_thenReportsTheRowWithItsValues() throws FatalException {
        // Arrange
        ShooterLog shooterLog = createShooterLog(createMatch("Match A"));
        String csvData = String.format("ShooterLogId,AddMatchIds,RemoveMatchIds%n%d,7;8,8;9%n", shooterLog.getId());

        // Act
        ShooterLogBulkResponseHolder holder = ipscShooterLogService.patchShooterLogs(csvData);

        // Assert
        ShooterLogBulkResponse result = holder.getShooterLogResults().getFirst();
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("both added and removed"));
        assertEquals("7;8", result.getShooterLogRow().getAddMatchIds());
        assertEquals("8;9", result.getShooterLogRow().getRemoveMatchIds());
    }

    @Test
    void testPatchShooterLogs_whenCsvIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.patchShooterLogs("   "));
    }

    // Helpers
    private Competitor createCompetitor(String clubNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setClubNumber(clubNumber);
        return competitorRepository.save(competitor);
    }

    private IpscMatch createMatch(String name) {
        return createMatch(name, LocalDate.of(2026, 9, 12));
    }

    private IpscMatch createMatch(String name, LocalDate date) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(date.atStartOfDay());
        return ipscMatchRepository.save(match);
    }

    private ShooterLog createShooterLog(IpscMatch... matches) {
        return createShooterLog(LocalDate.of(2026, 9, 1), matches);
    }

    private ShooterLog createShooterLog(LocalDate startDate, IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setStartDate(startDate);
        shooterLog.setEndDate(startDate.plusDays(29));
        shooterLog.setMatches(new java.util.ArrayList<>(List.of(matches)));
        return shooterLogRepository.saveAndFlush(shooterLog);
    }

    private void createShooterLogOverall(ShooterLog shooterLog, Competitor competitor) {
        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setShooterLog(shooterLog);
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.NONE);
        overall.setDivision(Division.OPEN);
        shooterLogOverallRepository.saveAndFlush(overall);
    }
}
