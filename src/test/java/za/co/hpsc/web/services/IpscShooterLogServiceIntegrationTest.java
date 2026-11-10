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
