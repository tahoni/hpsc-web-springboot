package za.co.hpsc.web.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.IpscShooterLogOverallService;
import za.co.hpsc.web.services.IpscShooterResultService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartupRecalculationRunnerTest {

    @Mock
    private ShooterResultRepository shooterResultRepository;

    @Mock
    private ShooterLogOverallRepository shooterLogOverallRepository;

    @Mock
    private IpscShooterResultService ipscShooterResultService;

    @Mock
    private IpscShooterLogOverallService ipscShooterLogOverallService;

    @InjectMocks
    private StartupRecalculationRunner runner;

    @Test
    void testRecalculate_whenNothingIsUncalculated_thenCalculatesNothing() {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of());
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of());

        // Act
        runner.recalculate();

        // Assert
        verifyNoInteractions(ipscShooterResultService, ipscShooterLogOverallService);
    }

    @Test
    void testRecalculate_whenMatchesAndLogsAreUncalculated_thenCalculatesEachMatchBeforeTheLogs()
            throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of(1L, 2L));
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L));

        // Act
        runner.recalculate();

        // Assert
        var order = inOrder(ipscShooterResultService, ipscShooterLogOverallService);
        order.verify(ipscShooterResultService).calculateMatchShooterResult(1L);
        order.verify(ipscShooterResultService).calculateMatchShooterResult(2L);
        order.verify(ipscShooterLogOverallService).calculateShooterLogOverall(7L);
    }

    @Test
    void testRecalculate_whenAMatchFails_thenStillCalculatesTheOthersAndTheLogs() throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of(1L, 2L));
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L));
        doThrow(new NonFatalException("No IPSC match found with ID 1"))
                .when(ipscShooterResultService).calculateMatchShooterResult(1L);

        // Act & Assert
        assertDoesNotThrow(() -> runner.recalculate());
        verify(ipscShooterResultService).calculateMatchShooterResult(2L);
        verify(ipscShooterLogOverallService).calculateShooterLogOverall(7L);
    }

    @Test
    void testRecalculate_whenALogFailsUnexpectedly_thenStillCalculatesTheOtherLogsWithoutThrowing()
            throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of());
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L, 8L));
        doThrow(new IllegalStateException("boom")).when(ipscShooterLogOverallService).calculateShooterLogOverall(7L);

        // Act & Assert
        assertDoesNotThrow(() -> runner.recalculate());
        verify(ipscShooterLogOverallService).calculateShooterLogOverall(8L);
        verify(ipscShooterResultService, never()).calculateMatchShooterResult(anyLong());
    }

    @Test
    void testRecalculate_whenTheQueryFails_thenDoesNotThrow() {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated())
                .thenThrow(new IllegalStateException("database unavailable"));

        // Act & Assert
        assertDoesNotThrow(() -> runner.recalculate());
        verifyNoInteractions(ipscShooterResultService, ipscShooterLogOverallService);
    }

    @Test
    void testRun_whenMatchesAreUncalculated_thenCalculatesThemOnAnotherThread() throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of(1L));
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of());

        // Act
        runner.run(null);

        // Assert
        verify(ipscShooterResultService, timeout(5000)).calculateMatchShooterResult(1L);
    }
}
