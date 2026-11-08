package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponse;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.impl.IpscShooterLogOverallServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscShooterLogOverallServiceTest {

    @Mock
    private ShooterLogRepository shooterLogRepository;

    @Mock
    private ShooterLogOverallRepository shooterLogOverallRepository;

    @Mock
    private ShooterResultRepository shooterResultRepository;

    @Mock
    private IpscShooterResultService ipscShooterResultService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private IpscShooterLogOverallServiceImpl ipscShooterLogOverallService;

    // calculateShooterLogOverall()
    @Test
    void testCalculateShooterLogOverall_whenCompetitorHasFewerMatchesThanCount_thenAveragesAllOfThem()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L, 1), match(11L, 2));
        Competitor competitor = competitor(5L);
        stubLog(shooterLog, List.of(
                shooterResult(100L, shooterLog.getMatches().get(0), competitor, "100", Division.OPEN),
                shooterResult(101L, shooterLog.getMatches().get(1), competitor, "80", Division.OPEN)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        ShooterLogOverall saved = savedOverallResults().getFirst();
        assertSame(shooterLog, saved.getShooterLog());
        assertSame(competitor, saved.getCompetitor());
        assertEquals(new BigDecimal("90.00"), saved.getPercentageInLog());
        assertEquals(1, saved.getRankInLog());
        assertNotNull(saved.getDateCalculated());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorsAreNotVerified_thenExcludesThemAndDeletesTheirOveralls()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor verified = competitor(5L);
        Competitor unverified = competitor(6L);
        unverified.setIsVerified(false);
        Competitor unrecorded = competitor(7L);
        unrecorded.setIsVerified(null);
        ShooterLogOverall staleOverall = overall(50L, shooterLog, unverified, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(
                shooterResult(100L, match, verified, "100", Division.OPEN),
                shooterResult(101L, match, unverified, "90", Division.OPEN),
                shooterResult(102L, match, unrecorded, "80", Division.OPEN)), List.of(staleOverall));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        ArgumentCaptor<List<ShooterLogOverall>> savedCaptor = listCaptor();
        ArgumentCaptor<List<ShooterLogOverall>> deletedCaptor = listCaptor();
        verify(transactionService).replaceShooterLogOverallResults(savedCaptor.capture(), deletedCaptor.capture());
        assertEquals(1, savedCaptor.getValue().size());
        assertSame(verified, savedCaptor.getValue().getFirst().getCompetitor());
        assertEquals(1, savedCaptor.getValue().getFirst().getRankInLog());
        assertEquals(List.of(staleOverall), deletedCaptor.getValue());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorHasMoreMatchesThanCount_thenAveragesOnlyTheBestOnes()
            throws NonFatalException {
        // Arrange
        IpscMatch first = match(10L, 1);
        IpscMatch second = match(11L, 2);
        IpscMatch third = match(12L, 3);
        IpscMatch fourth = match(13L, 4);
        IpscMatch fifth = match(14L, 5);
        ShooterLog shooterLog = shooterLog(1L, first, second, third, fourth, fifth);
        Competitor competitor = competitor(5L);
        stubLog(shooterLog, List.of(
                shooterResult(100L, first, competitor, "100", Division.OPEN),
                shooterResult(101L, second, competitor, "90", Division.OPEN),
                shooterResult(102L, third, competitor, "10", Division.OPEN),
                shooterResult(103L, fourth, competitor, "80", Division.OPEN),
                shooterResult(104L, fifth, competitor, "70", Division.OPEN)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        // The best four are 100, 90, 80 and 70; the 10 is dropped
        assertEquals(new BigDecimal("85.00"), savedOverallResults().getFirst().getPercentageInLog());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorsAreInTheSameGroup_thenRanksThemByPercentageWithTiesShared()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor first = competitor(5L);
        Competitor second = competitor(6L);
        Competitor third = competitor(7L);
        Competitor fourth = competitor(8L);
        stubLog(shooterLog, List.of(
                shooterResult(100L, match, first, "90", Division.OPEN),
                shooterResult(101L, match, second, "90", Division.OPEN),
                shooterResult(102L, match, third, "80", Division.OPEN),
                shooterResult(103L, match, fourth, "100", Division.OPEN)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        List<ShooterLogOverall> saved = savedOverallResults();
        assertEquals(1, overallOf(saved, fourth).getRankInLog());
        assertEquals(2, overallOf(saved, first).getRankInLog());
        assertEquals(2, overallOf(saved, second).getRankInLog());
        assertEquals(4, overallOf(saved, third).getRankInLog());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorsAreInDifferentDivisions_thenRanksEachDivisionSeparately()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor open = competitor(5L);
        Competitor production = competitor(6L);
        stubLog(shooterLog, List.of(
                shooterResult(100L, match, open, "60", Division.OPEN),
                shooterResult(101L, match, production, "100", Division.PRODUCTION)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        List<ShooterLogOverall> saved = savedOverallResults();
        assertEquals(1, overallOf(saved, open).getRankInLog());
        assertEquals(1, overallOf(saved, production).getRankInLog());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorChangedDivision_thenUsesTheMostRecentMatchsDivision()
            throws NonFatalException {
        // Arrange
        IpscMatch earlier = match(10L, 1);
        IpscMatch later = match(11L, 2);
        ShooterLog shooterLog = shooterLog(1L, earlier, later);
        Competitor competitor = competitor(5L);
        stubLog(shooterLog, List.of(
                shooterResult(100L, earlier, competitor, "70", Division.OPEN),
                shooterResult(101L, later, competitor, "90", Division.PRODUCTION)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        assertEquals(Division.PRODUCTION, savedOverallResults().getFirst().getDivision());
    }

    @Test
    void testCalculateShooterLogOverall_whenCompetitorHasNoPercentage_thenLeavesRankAndPercentageNull()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        stubLog(shooterLog, List.of(shooterResult(100L, match, competitor(5L), null, Division.OPEN)), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        ShooterLogOverall saved = savedOverallResults().getFirst();
        assertNull(saved.getRankInLog());
        assertNull(saved.getPercentageInLog());
        assertNotNull(saved.getDateCalculated());
    }

    @Test
    void testCalculateShooterLogOverall_whenOverallResultsExist_thenUpdatesThemAndDeletesThoseWithoutResults()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor kept = competitor(5L);
        ShooterLogOverall existing = overall(50L, shooterLog, kept, LocalDateTime.of(2020, 1, 1, 0, 0));
        ShooterLogOverall stale = overall(51L, shooterLog, competitor(6L), LocalDateTime.of(2020, 1, 1, 0, 0));
        stubLog(shooterLog, List.of(shooterResult(100L, match, kept, "80", Division.OPEN)),
                List.of(existing, stale));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        ArgumentCaptor<List<ShooterLogOverall>> toSave = listCaptor();
        ArgumentCaptor<List<ShooterLogOverall>> toDelete = listCaptor();
        verify(transactionService).replaceShooterLogOverallResults(toSave.capture(), toDelete.capture());
        assertEquals(1, toSave.getValue().size());
        assertSame(existing, toSave.getValue().getFirst());
        assertEquals(List.of(stale), toDelete.getValue());
    }

    @Test
    void testCalculateShooterLogOverall_whenOverallResultsAreUpToDate_thenDoesNotRecalculate()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        result.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService, never()).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenAShooterResultWasCalculatedAfterTheOverall_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        result.setDateCalculated(LocalDateTime.of(2026, 10, 1, 10, 0));
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenAnOverallWasNeverCalculated_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, null);
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenAShooterResultWasNeverCalculated_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        result.setDateCalculated(null);
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenOverallsWereCalculatedAtDifferentTimes_thenComparesAgainstTheOldest()
            throws NonFatalException {
        // Arrange: the overalls are visited in competitor ID order, so the oldest one is neither first nor last
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor first = competitor(5L);
        Competitor second = competitor(6L);
        Competitor third = competitor(7L);
        List<ShooterResult> results = List.of(
                shooterResult(100L, match, first, "80", Division.OPEN),
                shooterResult(101L, match, second, "70", Division.OPEN),
                shooterResult(102L, match, third, "60", Division.OPEN));
        List<ShooterLogOverall> existing = List.of(
                overall(50L, shooterLog, first, LocalDateTime.of(2026, 10, 1, 9, 0)),
                overall(51L, shooterLog, second, LocalDateTime.of(2026, 10, 1, 10, 0)),
                overall(52L, shooterLog, third, LocalDateTime.of(2026, 10, 1, 8, 0)));
        stubLog(shooterLog, results, existing);

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService, never()).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenShooterLogWasUpdatedAfterTheOverall_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        shooterLog.setDateUpdated(LocalDateTime.of(2026, 10, 1, 9, 30));
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenShooterLogWasUpdatedBeforeTheOverall_thenDoesNotRecalculate()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        ShooterLog shooterLog = shooterLog(1L, match);
        shooterLog.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 30));
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService, never()).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenMatchIsAfterTheLogsEndDate_thenThrowsValidationException() {
        // Arrange
        IpscMatch inside = match(10L, 1);
        IpscMatch outside = match(11L, 2);
        outside.setScheduledDate(LocalDateTime.of(2026, 10, 5, 0, 0));
        ShooterLog shooterLog = shooterLog(1L, inside, outside);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(1L));
        assertTrue(exception.getMessage().startsWith("Match with ID 11 is scheduled for 2026-10-05"));
        verifyNoInteractions(ipscShooterResultService, shooterResultRepository, shooterLogOverallRepository,
                transactionService);
    }

    @Test
    void testCalculateShooterLogOverall_whenMatchesAreOnTheLogsFirstAndLastDay_thenIncludesThem()
            throws NonFatalException {
        // Arrange
        IpscMatch first = match(10L, 1);
        IpscMatch last = match(11L, 30);
        ShooterLog shooterLog = shooterLog(1L, first, last);
        stubLog(shooterLog, List.of(), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(ipscShooterResultService).calculateMatchShooterResult(10L);
        verify(ipscShooterResultService).calculateMatchShooterResult(11L);
        verify(shooterResultRepository).findAllByMatchIdInWithDetails(List.of(10L, 11L));
    }

    @Test
    void testCalculateShooterLogOverall_whenMatchIsBeforeTheLogsStartDate_thenThrowsValidationException() {
        // Arrange
        IpscMatch before = match(10L, 1);
        before.setScheduledDate(LocalDateTime.of(2026, 8, 31, 0, 0));
        ShooterLog shooterLog = shooterLog(1L, before);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(1L));
        assertTrue(exception.getMessage().startsWith("Match with ID 10 is scheduled for 2026-08-31"));
        verifyNoInteractions(ipscShooterResultService, shooterResultRepository, transactionService);
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasNoEndDate_thenIncludesEveryMatch() throws NonFatalException {
        // Arrange
        IpscMatch outside = match(10L, 1);
        outside.setScheduledDate(LocalDateTime.of(2027, 1, 5, 0, 0));
        ShooterLog shooterLog = shooterLog(1L, outside);
        shooterLog.setEndDate(null);
        stubLog(shooterLog, List.of(), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(ipscShooterResultService).calculateMatchShooterResult(10L);
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasNoStartDate_thenIncludesEveryMatch() throws NonFatalException {
        // Arrange
        IpscMatch outside = match(10L, 1);
        outside.setScheduledDate(LocalDateTime.of(2027, 1, 5, 0, 0));
        ShooterLog shooterLog = shooterLog(1L, outside);
        shooterLog.setStartDate(null);
        stubLog(shooterLog, List.of(), List.of());

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(ipscShooterResultService).calculateMatchShooterResult(10L);
    }

    @Test
    void testCalculateShooterLogOverall_whenAMatchWasUpdatedAfterTheOverall_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 10, 0));
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        result.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenAMatchWasUpdatedBeforeTheOverall_thenDoesNotRecalculate()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(10L, 1);
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 30));
        ShooterLog shooterLog = shooterLog(1L, match);
        Competitor competitor = competitor(5L);
        ShooterResult result = shooterResult(100L, match, competitor, "80", Division.OPEN);
        result.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        ShooterLogOverall existing = overall(50L, shooterLog, competitor, LocalDateTime.of(2026, 10, 1, 9, 0));
        stubLog(shooterLog, List.of(result), List.of(existing));

        // Act
        ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        verify(transactionService, never()).replaceShooterLogOverallResults(any(), any());
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasNoMatches_thenSavesNothingAndDeletesNothing()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L);
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(shooterLogOverallRepository.findAllByShooterLogIdWithDetails(1L)).thenReturn(List.of());

        // Act
        ShooterLogOverallResponseHolder holder = ipscShooterLogOverallService.calculateShooterLogOverall(1L);

        // Assert
        assertTrue(holder.getShooterLogOverallResults().isEmpty());
        verifyNoInteractions(ipscShooterResultService, shooterResultRepository, transactionService);
    }

    @Test
    void testCalculateShooterLogOverall_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(99L));
        assertEquals("No shooter log found with ID 99", exception.getMessage());
        verifyNoInteractions(shooterResultRepository, ipscShooterResultService, transactionService);
    }

    // getAllShooterLogOverallResults()
    @Test
    void testGetAllShooterLogOverallResults_whenNoShooterLogs_thenReturnsEmptyList() {
        // Arrange
        when(shooterLogOverallRepository.findAllWithDetails()).thenReturn(List.of());
        when(shooterLogRepository.findAllWithMatches()).thenReturn(List.of());

        // Act
        List<ShooterLogOverallResponseHolder> result = ipscShooterLogOverallService.getAllShooterLogOverallResults();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllShooterLogOverallResults_whenSeveralLogs_thenGroupsOverallResultsByLogIncludingLogsWithNone() {
        // Arrange
        ShooterLog withOverallResults = shooterLog(1L, match(10L, 1));
        ShooterLog withoutOverallResults = shooterLog(2L);
        ShooterLogOverall overall = overall(50L, withOverallResults, competitor(5L),
                LocalDateTime.of(2026, 10, 1, 9, 0));
        when(shooterLogOverallRepository.findAllWithDetails()).thenReturn(List.of(overall));
        when(shooterLogRepository.findAllWithMatches()).thenReturn(List.of(withOverallResults, withoutOverallResults));

        // Act
        List<ShooterLogOverallResponseHolder> result = ipscShooterLogOverallService.getAllShooterLogOverallResults();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getShooterLog().getShooterLogId());
        assertEquals(List.of(10L), result.get(0).getShooterLog().getMatchIds());
        assertEquals(1, result.get(0).getShooterLogOverallResults().size());
        assertEquals(2L, result.get(1).getShooterLog().getShooterLogId());
        assertTrue(result.get(1).getShooterLogOverallResults().isEmpty());
    }

    // getShooterLogOverallResults()
    @Test
    void testGetShooterLogOverallResults_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(shooterLogRepository.findByIdWithMatches(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallService.getShooterLogOverallResults(99L));
        verifyNoInteractions(shooterLogOverallRepository);
    }

    @Test
    void testGetShooterLogOverallResults_whenLogHasOverallResults_thenMapsEachToAResponse() throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = shooterLog(1L, match(10L, 1));
        ShooterLogOverall overall = overall(50L, shooterLog, competitor(5L), LocalDateTime.of(2026, 10, 1, 9, 0));
        overall.setRankInLog(3);
        overall.setPercentageInLog(new BigDecimal("87.5"));
        when(shooterLogRepository.findByIdWithMatches(1L)).thenReturn(Optional.of(shooterLog));
        when(shooterLogOverallRepository.findAllByShooterLogIdWithDetails(1L)).thenReturn(List.of(overall));

        // Act
        ShooterLogOverallResponseHolder holder = ipscShooterLogOverallService.getShooterLogOverallResults(1L);

        // Assert
        assertEquals(1L, holder.getShooterLog().getShooterLogId());
        ShooterLogOverallResponse response = holder.getShooterLogOverallResults().getFirst();
        assertEquals(50L, response.getShooterLogOverallId());
        assertEquals(5L, response.getCompetitorId());
        assertEquals(List.of("Jane Doe", "JD Doe"), response.getCompetitorNames());
        assertEquals(3, response.getRankInLog());
        assertEquals("87.50", response.getPercentageInLog());
    }

    // Helpers
    private void stubLog(ShooterLog shooterLog, List<ShooterResult> results, List<ShooterLogOverall> existing)
            throws NonFatalException {
        when(shooterLogRepository.findByIdWithMatches(shooterLog.getId())).thenReturn(Optional.of(shooterLog));
        when(shooterResultRepository.findAllByMatchIdInWithDetails(any())).thenReturn(results);
        when(shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId())).thenReturn(existing);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<ShooterLogOverall>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private List<ShooterLogOverall> savedOverallResults() {
        ArgumentCaptor<List<ShooterLogOverall>> captor = listCaptor();
        verify(transactionService).replaceShooterLogOverallResults(captor.capture(), any());
        return new ArrayList<>(captor.getValue());
    }

    private ShooterLogOverall overallOf(List<ShooterLogOverall> overallResults, Competitor competitor) {
        return overallResults.stream()
                .filter(overall -> overall.getCompetitor() == competitor)
                .findFirst()
                .orElseThrow();
    }

    private ShooterLog shooterLog(Long id, IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setId(id);
        shooterLog.setStartDate(LocalDate.of(2026, 9, 1));
        shooterLog.setEndDate(LocalDate.of(2026, 9, 30));
        shooterLog.setMatches(List.of(matches));
        return shooterLog;
    }

    private IpscMatch match(Long id, int day) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        match.setName("Match " + id);
        match.setScheduledDate(LocalDateTime.of(2026, 9, day, 0, 0));
        return match;
    }

    private Competitor competitor(Long id) {
        Competitor competitor = new Competitor();
        competitor.setId(id);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setIsVerified(true);
        competitor.setNickName("JD");
        return competitor;
    }

    private ShooterResult shooterResult(Long id, IpscMatch match, Competitor competitor, String percentage,
                                        Division division) {
        ShooterResult shooterResult = new ShooterResult();
        shooterResult.setId(id);
        shooterResult.setMatch(match);
        shooterResult.setCompetitor(competitor);
        shooterResult.setCompetitorCategory(CompetitorCategory.NONE);
        shooterResult.setDivision(division);
        shooterResult.setPercentageInLog(percentage == null ? null : new BigDecimal(percentage));
        shooterResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        return shooterResult;
    }

    private ShooterLogOverall overall(Long id, ShooterLog shooterLog, Competitor competitor,
                                      LocalDateTime dateCalculated) {
        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setId(id);
        overall.setShooterLog(shooterLog);
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.NONE);
        overall.setDivision(Division.OPEN);
        overall.setDateCalculated(dateCalculated);
        return overall;
    }
}
