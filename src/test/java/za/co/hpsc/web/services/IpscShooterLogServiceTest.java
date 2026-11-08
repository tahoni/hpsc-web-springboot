package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.services.impl.IpscShooterLogServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscShooterLogService} contract, exercised through the interface type with every
 * repository dependency mocked.
 */
@ExtendWith(MockitoExtension.class)
class IpscShooterLogServiceTest {

    @Mock
    private ShooterLogRepository shooterLogRepository;

    @Mock
    private ShooterLogOverallRepository shooterLogOverallRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private TransactionService transactionService;

    private IpscShooterLogService ipscShooterLogService;

    @BeforeEach
    void setUp() {
        ipscShooterLogService = new IpscShooterLogServiceImpl(shooterLogRepository, shooterLogOverallRepository,
                ipscMatchRepository, transactionService);
    }

    // createShooterLog()
    @Test
    void testCreateShooterLog_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLog(null));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenEndDateIsBeforeStartDate_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 30), LocalDate.of(2026, 9, 1), null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLog(request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L, 11L));
        when(ipscMatchRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(match(10L)));

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterLogService.createShooterLog(request));
        assertEquals("No IPSC match found with ID 11", exception.getMessage());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenRequestIsValid_thenSavesTheLogWithItsMatchesAndReturnsIt() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(11L, 10L));
        when(ipscMatchRepository.findAllById(List.of(11L, 10L))).thenReturn(List.of(match(10L), match(11L)));
        when(transactionService.saveShooterLog(any(ShooterLog.class))).thenAnswer(invocation -> {
            ShooterLog saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        // Act
        ShooterLogResponse response = ipscShooterLogService.createShooterLog(request);

        // Assert
        ArgumentCaptor<ShooterLog> captor = ArgumentCaptor.forClass(ShooterLog.class);
        verify(transactionService).saveShooterLog(captor.capture());
        assertEquals(LocalDate.of(2026, 9, 1), captor.getValue().getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), captor.getValue().getEndDate());
        assertEquals(5L, response.getShooterLogId());
        assertEquals(List.of(11L, 10L), response.getMatchIds());
    }

    @Test
    void testCreateShooterLog_whenAMatchIsBeforeTheStartDate_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L));
        when(ipscMatchRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(match(10L, LocalDate.of(2026, 8, 31))));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogService.createShooterLog(request));
        assertEquals("Match with ID 10 is scheduled for 2026-08-31, outside the shooter log's dates 2026-09-01 to "
                + "2026-09-30.", exception.getMessage());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenAMatchIsAfterTheEndDate_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L));
        when(ipscMatchRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(match(10L, LocalDate.of(2026, 10, 1))));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLog(request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenAMatchHasNoScheduledDate_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L));
        when(ipscMatchRepository.findAllById(List.of(10L))).thenReturn(List.of(match(10L, null)));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.createShooterLog(request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCreateShooterLog_whenMatchesAreOnTheStartAndEndDates_thenSavesTheLog() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L, 11L));
        when(ipscMatchRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(
                match(10L, LocalDate.of(2026, 9, 1)), match(11L, LocalDate.of(2026, 9, 30))));
        when(transactionService.saveShooterLog(any(ShooterLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShooterLogResponse response = ipscShooterLogService.createShooterLog(request);

        // Assert
        assertEquals(List.of(10L, 11L), response.getMatchIds());
    }

    // deleteShooterLog()
    @Test
    void testDeleteShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogService.deleteShooterLog(99L));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testDeleteShooterLog_whenLogHasOverallResults_thenThrowsValidationException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog(1L)));
        when(shooterLogOverallRepository.existsByShooterLogId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.deleteShooterLog(1L));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testDeleteShooterLog_whenLogHasNoDependents_thenDeletesIt() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(shooterLogOverallRepository.existsByShooterLogId(1L)).thenReturn(false);

        // Act
        assertDoesNotThrow(() -> ipscShooterLogService.deleteShooterLog(1L));

        // Assert
        verify(transactionService).deleteShooterLog(shooterLog);
    }

    @Test
    void testDeleteShooterLog_whenReferenceAddedBeforeFlush_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(shooterLogOverallRepository.existsByShooterLogId(1L)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("FK violation")).when(transactionService)
                .deleteShooterLog(shooterLog);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogService.deleteShooterLog(1L));
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    // getAllShooterLogs()
    @Test
    void testGetAllShooterLogs_whenLogsExist_thenMapsEachToAResponse() {
        // Arrange
        when(shooterLogRepository.findAllWithMatches()).thenReturn(List.of(shooterLog(1L), shooterLog(2L)));

        // Act
        List<ShooterLogResponse> result = ipscShooterLogService.getAllShooterLogs();

        // Assert
        assertEquals(List.of(1L, 2L), result.stream().map(ShooterLogResponse::getShooterLogId).toList());
    }

    @Test
    void testGetAllShooterLogs_whenNoLogs_thenReturnsEmptyList() {
        // Arrange
        when(shooterLogRepository.findAllWithMatches()).thenReturn(List.of());

        // Act & Assert
        assertTrue(ipscShooterLogService.getAllShooterLogs().isEmpty());
    }

    // getShooterLog()
    @Test
    void testGetShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterLogService.getShooterLog(99L));
        assertEquals("No shooter log found with ID 99", exception.getMessage());
    }

    @Test
    void testGetShooterLog_whenLogExists_thenReturnsItsDatesAndMatchIds() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));

        // Act
        ShooterLogResponse response = ipscShooterLogService.getShooterLog(1L);

        // Assert
        assertEquals(1L, response.getShooterLogId());
        assertEquals(LocalDate.of(2026, 9, 1), response.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), response.getEndDate());
        assertEquals(List.of(10L), response.getMatchIds());
    }

    // patchShooterLog()
    @Test
    void testPatchShooterLog_whenEmptyRequest_thenLeavesTheLogUnchanged() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(1L, new ShooterLogPatchRequest());

        // Assert
        assertEquals(LocalDate.of(2026, 9, 1), response.getStartDate());
        assertEquals(List.of(10L), response.getMatchIds());
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    void testPatchShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscShooterLogService.patchShooterLog(99L, new ShooterLogPatchRequest()));
    }

    @Test
    void testPatchShooterLog_whenEndDateWouldBeBeforeStartDate_thenThrowsValidationException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog(1L)));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setEndDate(LocalDate.of(2026, 8, 1));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.patchShooterLog(1L, request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testPatchShooterLog_whenLogHasNoStartDate_thenAppliesTheEndDateWithoutComparing() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        shooterLog.setStartDate(null);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setEndDate(LocalDate.of(2026, 8, 1));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(1L, request);

        // Assert
        assertEquals(LocalDate.of(2026, 8, 1), response.getEndDate());
    }

    @Test
    void testPatchShooterLog_whenLogHasNoEndDate_thenAppliesTheStartDateWithoutComparing() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        shooterLog.setEndDate(null);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setStartDate(LocalDate.of(2026, 10, 1));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(1L, request);

        // Assert
        assertEquals(LocalDate.of(2026, 10, 1), response.getStartDate());
    }

    @Test
    void testPatchShooterLog_whenMatchIdsAreEmpty_thenClearsTheLogsMatches() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setMatchIds(List.of());

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(1L, request);

        // Assert
        assertTrue(response.getMatchIds().isEmpty());
    }

    @Test
    void testPatchShooterLog_whenDatesAndMatchIdsProvided_thenAppliesThem() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(ipscMatchRepository.findAllById(List.of(12L))).thenReturn(List.of(match(12L, LocalDate.of(2026, 10, 15))));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setStartDate(LocalDate.of(2026, 10, 1));
        request.setEndDate(LocalDate.of(2026, 10, 31));
        request.setMatchIds(List.of(12L));

        // Act
        ShooterLogResponse response = ipscShooterLogService.patchShooterLog(1L, request);

        // Assert
        assertEquals(LocalDate.of(2026, 10, 1), response.getStartDate());
        assertEquals(LocalDate.of(2026, 10, 31), response.getEndDate());
        assertEquals(List.of(12L), response.getMatchIds());
    }

    @Test
    void testPatchShooterLog_whenNewDatesLeaveAnExistingMatchOutside_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setStartDate(LocalDate.of(2026, 10, 1));
        request.setEndDate(LocalDate.of(2026, 10, 31));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.patchShooterLog(1L, request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testPatchShooterLog_whenANewMatchIsOutsideTheLogsDates_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(ipscMatchRepository.findAllById(List.of(12L)))
                .thenReturn(List.of(match(12L, LocalDate.of(2026, 10, 15))));
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setMatchIds(List.of(12L));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.patchShooterLog(1L, request));
        verifyNoInteractions(transactionService);
    }

    // updateShooterLog()
    @Test
    void testUpdateShooterLog_whenLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null);
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogService.updateShooterLog(99L, request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testUpdateShooterLog_whenRequestIsInvalid_thenThrowsValidationExceptionBeforeLookingUpTheLog() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(null, LocalDate.of(2026, 9, 30), null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.updateShooterLog(1L, request));
        verifyNoInteractions(shooterLogRepository, transactionService);
    }

    @Test
    void testUpdateShooterLog_whenAMatchIsOutsideTheNewDates_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(ipscMatchRepository.findAllById(List.of(10L))).thenReturn(List.of(match(10L)));
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                List.of(10L));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogService.updateShooterLog(1L, request));
        verifyNoInteractions(transactionService);
    }

    @Test
    void testUpdateShooterLog_whenRequestIsValid_thenReplacesDatesAndMatches() {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(ipscMatchRepository.findAllById(List.of(11L))).thenReturn(List.of(match(11L, LocalDate.of(2026, 10, 15))));
        when(transactionService.saveShooterLog(shooterLog)).thenReturn(shooterLog);
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                List.of(11L));

        // Act
        ShooterLogResponse response = ipscShooterLogService.updateShooterLog(1L, request);

        // Assert
        assertEquals(LocalDate.of(2026, 10, 1), response.getStartDate());
        assertEquals(List.of(11L), response.getMatchIds());
    }

    // Helpers
    private ShooterLog shooterLog(Long id, IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setId(id);
        shooterLog.setStartDate(LocalDate.of(2026, 9, 1));
        shooterLog.setEndDate(LocalDate.of(2026, 9, 30));
        shooterLog.setMatches(new ArrayList<>(List.of(matches)));
        return shooterLog;
    }

    private IpscMatch match(Long id) {
        return match(id, LocalDate.of(2026, 9, 12));
    }

    private IpscMatch match(Long id, LocalDate date) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        match.setName("Match " + id);
        match.setScheduledDate(date == null ? null : date.atStartOfDay());
        return match;
    }
}
