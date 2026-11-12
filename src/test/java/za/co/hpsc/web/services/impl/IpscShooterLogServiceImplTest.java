package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.repositories.IpscMatchRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link IpscShooterLogServiceImpl}'s impl-only protected helper methods - not declared on
 * {@link za.co.hpsc.web.services.IpscShooterLogService}. The interface's contract is covered by
 * {@link za.co.hpsc.web.services.IpscShooterLogServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscShooterLogServiceImplTest {

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    private IpscShooterLogServiceImpl ipscShooterLogServiceImpl;

    @BeforeEach
    void setUp() {
        ipscShooterLogServiceImpl = new IpscShooterLogServiceImpl(null, null, ipscMatchRepository, null);
    }

    // applyFields()
    @Test
    void testApplyFields_whenRequestHasDatesAndMatches_thenCopiesThemOntoTheLog() {
        // Arrange
        ShooterLog shooterLog = new ShooterLog();
        IpscMatch match = match(10L);
        when(ipscMatchRepository.findAllById(List.of(10L))).thenReturn(List.of(match));
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L));

        // Act
        ipscShooterLogServiceImpl.applyFields(shooterLog, request);

        // Assert
        assertEquals(LocalDate.of(2026, 9, 1), shooterLog.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), shooterLog.getEndDate());
        assertEquals(List.of(match), shooterLog.getMatches());
    }

    // adjustMatches()
    @Test
    void testAdjustMatches_whenTheLogHasNoMatchesLoaded_thenAddsToAnEmptyList() {
        // Arrange
        IpscMatch added = match(10L);
        when(ipscMatchRepository.findAllById(List.of(10L))).thenReturn(List.of(added));

        // Act
        List<IpscMatch> result = ipscShooterLogServiceImpl.adjustMatches(null, List.of(10L), null);

        // Assert
        assertEquals(List.of(added), result);
    }

    // resolveMatches()
    @Test
    void testResolveMatches_whenIdsAreNull_thenReturnsAnEmptyModifiableList() {
        // Act
        List<IpscMatch> result = ipscShooterLogServiceImpl.resolveMatches(null);

        // Assert
        assertTrue(result.isEmpty());
        assertDoesNotThrow(() -> result.add(match(1L)));
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    void testResolveMatches_whenIdsRepeat_thenReturnsEachMatchOnceInTheOrderGiven() {
        // Arrange
        IpscMatch first = match(10L);
        IpscMatch second = match(11L);
        when(ipscMatchRepository.findAllById(List.of(11L, 10L))).thenReturn(List.of(first, second));

        // Act
        List<IpscMatch> result = ipscShooterLogServiceImpl.resolveMatches(List.of(11L, 10L, 11L));

        // Assert
        assertEquals(List.of(second, first), result);
    }

    @Test
    void testResolveMatches_whenAMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findAllById(List.of(10L))).thenReturn(List.of());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterLogServiceImpl.resolveMatches(List.of(10L)));
        assertEquals("No IPSC match found with ID 10", exception.getMessage());
    }

    // validateMatchesWithinDates()
    @Test
    void testValidateMatchesWithinDates_whenLogHasNoMatches_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        // Act & Assert
        assertDoesNotThrow(() -> ipscShooterLogServiceImpl.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenLogHasNoDates_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(null, null, match(10L, LocalDate.of(2026, 9, 12)));

        // Act & Assert
        assertDoesNotThrow(() -> ipscShooterLogServiceImpl.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenMatchesAreOnTheBoundaryDates_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                match(10L, LocalDate.of(2026, 9, 1)), match(11L, LocalDate.of(2026, 9, 30)));

        // Act & Assert
        assertDoesNotThrow(() -> ipscShooterLogServiceImpl.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenAMatchIsAfterTheEndDate_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                match(10L, LocalDate.of(2026, 9, 12)), match(11L, LocalDate.of(2026, 10, 1)));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogServiceImpl.validateMatchesWithinDates(shooterLog));
        assertTrue(exception.getMessage().startsWith("Match with ID 11 is scheduled for 2026-10-01"));
    }

    // Helpers
    private ShooterLog shooterLog(LocalDate startDate, LocalDate endDate, IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setStartDate(startDate);
        shooterLog.setEndDate(endDate);
        shooterLog.setMatches(List.of(matches));
        return shooterLog;
    }

    private IpscMatch match(Long id, LocalDate date) {
        IpscMatch match = match(id);
        match.setScheduledDate(date.atStartOfDay());
        return match;
    }

    private IpscMatch match(Long id) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        return match;
    }
}
