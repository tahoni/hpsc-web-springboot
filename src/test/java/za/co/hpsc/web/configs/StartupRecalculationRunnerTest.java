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
    void testRun_whenNothingIsUncalculated_thenCalculatesNothing() {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of());
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of());

        // Act
        runner.run(null);

        // Assert
        verifyNoInteractions(ipscShooterResultService, ipscShooterLogOverallService);
    }

    @Test
    void testRun_whenMatchesAndLogsAreUncalculated_thenCalculatesEachMatchBeforeTheLogs() throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of(1L, 2L));
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L));

        // Act
        runner.run(null);

        // Assert
        var order = inOrder(ipscShooterResultService, ipscShooterLogOverallService);
        order.verify(ipscShooterResultService).calculateMatchShooterResult(1L);
        order.verify(ipscShooterResultService).calculateMatchShooterResult(2L);
        order.verify(ipscShooterLogOverallService).calculateShooterLogOverall(7L);
    }

    @Test
    void testRun_whenAMatchFails_thenStillCalculatesTheOthersAndTheLogs() throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of(1L, 2L));
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L));
        doThrow(new NonFatalException("No IPSC match found with ID 1"))
                .when(ipscShooterResultService).calculateMatchShooterResult(1L);

        // Act & Assert
        assertDoesNotThrow(() -> runner.run(null));
        verify(ipscShooterResultService).calculateMatchShooterResult(2L);
        verify(ipscShooterLogOverallService).calculateShooterLogOverall(7L);
    }

    @Test
    void testRun_whenALogFailsUnexpectedly_thenStillCalculatesTheOtherLogsWithoutThrowing() throws NonFatalException {
        // Arrange
        when(shooterResultRepository.findMatchIdsWithoutDateCalculated()).thenReturn(List.of());
        when(shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated()).thenReturn(List.of(7L, 8L));
        doThrow(new IllegalStateException("boom")).when(ipscShooterLogOverallService).calculateShooterLogOverall(7L);

        // Act & Assert
        assertDoesNotThrow(() -> runner.run(null));
        verify(ipscShooterLogOverallService).calculateShooterLogOverall(8L);
        verify(ipscShooterResultService, never()).calculateMatchShooterResult(anyLong());
    }
}
