package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.impl.IpscShooterResultServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscShooterResultServiceTest {

    private static final Sort MATCH_ORDER = Sort.by("scheduledDate", "id");

    @Mock
    private ShooterResultRepository shooterResultRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private IpscShooterResultServiceImpl ipscShooterResultService;

    // calculateMatchShooterResult()
    @Test
    void testCalculateMatchShooterResult_whenCompetitorsHaveNoPoints_thenExcludesThemFromTheResults()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor unscored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L)).thenReturn(List.of(unscored));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenCompetitorIsNotVerified_thenExcludesThemAndRanksTheOthers()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor verified = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor unverified = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "100");
        unverified.getCompetitor().setIsVerified(false);
        MatchCompetitor unrecorded = matchCompetitor(3L, FirearmType.HANDGUN, Division.OPEN, "90");
        unrecorded.getCompetitor().setIsVerified(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(unverified, unrecorded, verified));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        // The unverified competitors scored higher, but neither counts as the winner
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, verified));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, verified));
    }

    @Test
    void testCalculateMatchShooterResult_whenCompetitorIsNoLongerVerified_thenDeletesTheStaleResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor unverified = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        unverified.getCompetitor().setIsVerified(false);
        ShooterResult stale = shooterResult(10L, 2L);
        stale.setMatchCompetitor(unverified);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(unverified));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(stale));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertTrue(savedShooterResults().isEmpty());
        assertEquals(List.of(stale), deletedShooterResults());
    }

    @Test
    void testCalculateMatchShooterResult_whenCompetitorHasNullPoints_thenExcludesThemAndRanksTheOthers()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor unscored = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(unscored, scored));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, scored));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, scored));
    }

    @Test
    void testCalculateMatchShooterResult_whenCompetitorHasZeroPoints_thenExcludesThemAndRanksTheOthers()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor zero = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "0");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(zero, scored));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, scored));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, scored));
    }

    @Test
    void testCalculateMatchShooterResult_whenPointsWereClearedAfterCalculation_thenDeletesTheStaleResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor cleared = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, null);
        ShooterResult existingScored = shooterResult(10L, 2L);
        existingScored.setMatchCompetitor(scored);
        ShooterResult stale = shooterResult(11L, 2L);
        stale.setMatchCompetitor(cleared);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(scored, cleared));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L))
                .thenReturn(List.of(existingScored, stale));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(List.of(existingScored), savedShooterResults());
        assertEquals(List.of(stale), deletedShooterResults());
    }

    @Test
    void testCalculateMatchShooterResult_whenOnlyStaleResultsRemain_thenDeletesThemAndSavesNone()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor cleared = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "0");
        ShooterResult stale = shooterResult(10L, 2L);
        stale.setMatchCompetitor(cleared);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(cleared));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(stale));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertTrue(savedShooterResults().isEmpty());
        assertEquals(List.of(stale), deletedShooterResults());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchCompetitorsExist_thenPopulatesEachShooterResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        ShooterResult saved = savedShooterResults().getFirst();
        assertSame(match, saved.getMatch());
        assertSame(matchCompetitor.getCompetitor(), saved.getCompetitor());
        assertSame(matchCompetitor, saved.getMatchCompetitor());
        assertEquals(CompetitorCategory.JUNIOR, saved.getCompetitorCategory());
        assertEquals(Division.OPEN, saved.getDivision());
        assertEquals(1, saved.getRankInLog());
        assertEquals(new BigDecimal("100.00"), saved.getPercentageInLog());
        assertNotNull(saved.getDateCalculated());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterResultService.calculateMatchShooterResult(99L));
        assertEquals("No IPSC match found with ID 99", exception.getMessage());
        verifyNoInteractions(matchCompetitorRepository, transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenResultAlreadyExists_thenUpdatesItInsteadOfAddingOne()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        matchCompetitor.setDateUpdated(LocalDateTime.of(2026, 10, 2, 8, 0));
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        existing.setRankInLog(9);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, saved.size());
        assertSame(existing, saved.getFirst());
        assertEquals(1, existing.getRankInLog());
    }

    @Test
    void testCalculateMatchShooterResult_whenResultsAreUpToDate_thenReturnsThemWithoutRecalculating()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 9, 30, 8, 0));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        matchCompetitor.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 0));
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatch(match);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(1, holder.getShooterResults().size());
        assertEquals(10L, holder.getShooterResults().getFirst().getShooterResultId());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchWasUpdatedAfterCalculation_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 2, 8, 0));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(1, savedShooterResults().size());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchWasUpdatedLessThanFiveMinutesAfterCalculation_thenDoesNotRecalculate()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 4, 59));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchWasUpdatedFiveMinutesAfterCalculation_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 5));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(1, savedShooterResults().size());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchCompetitorHasNoResultYet_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor withResult = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor withoutResult = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "40");
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(withResult);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(withResult, withoutResult));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(2, savedShooterResults().size());
    }

    @Test
    void testCalculateMatchShooterResult_whenCompetitorHasNoCategory_thenRanksThemWithTheNoneCategory()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor uncategorised = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "50");
        MatchCompetitor none = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "25");
        uncategorised.setCompetitorCategory(null);
        none.setCompetitorCategory(CompetitorCategory.NONE);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(uncategorised, none));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, rankOf(saved, uncategorised));
        assertEquals(2, rankOf(saved, none));
    }

    @Test
    void testCalculateMatchShooterResult_whenExistingResultWasNeverCalculated_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        ShooterResult existing = shooterResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        existing.setDateCalculated(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertEquals(1, savedShooterResults().size());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchHasNoCompetitors_thenReturnsWithoutRecalculating()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L)).thenReturn(List.of());
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        assertTrue(holder.getShooterResults().isEmpty());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenResultsWereCalculatedAtDifferentTimes_thenComparesAgainstTheOldest()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 7, 0));
        MatchCompetitor newer = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor older = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "40");
        ShooterResult newerResult = shooterResult(10L, 2L);
        newerResult.setMatchCompetitor(newer);
        newerResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        ShooterResult olderResult = shooterResult(11L, 2L);
        olderResult.setMatchCompetitor(older);
        olderResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 7, 30));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(newer, older));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(newerResult, olderResult));

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchShooterResult_whenSeveralCompetitorCategories_thenRanksEachCategorySeparately()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor junior = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "50");
        MatchCompetitor lady = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "40");
        MatchCompetitor none = matchCompetitor(3L, FirearmType.HANDGUN, Division.OPEN, "30");
        MatchCompetitor otherNone = matchCompetitor(4L, FirearmType.HANDGUN, Division.OPEN, "15");
        lady.setCompetitorCategory(CompetitorCategory.LADY);
        none.setCompetitorCategory(CompetitorCategory.NONE);
        otherNone.setCompetitorCategory(CompetitorCategory.NONE);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(junior, lady, none, otherNone));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, rankOf(saved, junior));
        assertEquals(1, rankOf(saved, lady));
        assertEquals(1, rankOf(saved, none));
        assertEquals(2, rankOf(saved, otherNone));
        assertEquals(new BigDecimal("50.00"), percentageOf(saved, otherNone));
    }

    @Test
    void testCalculateMatchShooterResult_whenSeveralFirearmTypesAndDivisions_thenRanksEachGroupSeparately()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor handgunOpen = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "50");
        MatchCompetitor rifleOpen = matchCompetitor(2L, FirearmType.RIFLE, Division.OPEN, "40");
        MatchCompetitor handgunProduction = matchCompetitor(3L, FirearmType.HANDGUN, Division.PRODUCTION, "30");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(handgunOpen, rifleOpen, handgunProduction));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(3, saved.size());
        for (ShooterResult shooterResult : saved) {
            assertEquals(1, shooterResult.getRankInLog());
            assertEquals(new BigDecimal("100.00"), shooterResult.getPercentageInLog());
        }
    }

    @Test
    void testCalculateMatchShooterResult_whenSharedGroupHasTies_thenRanksTiedCompetitorsEquallyAndSkipsARank()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor first = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "90");
        MatchCompetitor tied = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "90");
        MatchCompetitor third = matchCompetitor(3L, FirearmType.HANDGUN, Division.OPEN, "45");
        MatchCompetitor unscored = matchCompetitor(4L, FirearmType.HANDGUN, Division.OPEN, null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(third, first, unscored, tied));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscShooterResultService.calculateMatchShooterResult(2L);

        // Assert
        List<ShooterResult> saved = savedShooterResults();
        assertEquals(1, rankOf(saved, first));
        assertEquals(1, rankOf(saved, tied));
        assertEquals(3, rankOf(saved, third));
        assertEquals(new BigDecimal("50.00"), percentageOf(saved, third));
        assertEquals(3, saved.size());
        assertTrue(saved.stream().noneMatch(result -> result.getMatchCompetitor() == unscored));
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenMatchHasNoResults_thenReturnsItWithNoResults() {
        // Arrange
        ShooterResult shooterResult = shooterResult(10L, 2L);
        IpscMatch emptyMatch = match(4L, "Empty Match");
        when(shooterResultRepository.findAllWithDetails()).thenReturn(List.of(shooterResult));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(shooterResult.getMatch(), emptyMatch));

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertEquals(2, holders.size());
        assertEquals(1, holders.get(0).getShooterResults().size());
        assertEquals(4L, holders.get(1).getMatch().getMatchId());
        assertEquals("Empty Match", holders.get(1).getMatch().getMatchName());
        assertTrue(holders.get(1).getShooterResults().isEmpty());
    }

    @Test
    void testGetAllShooterResults_whenMatchesAreReturned_thenKeepsTheRepositoryOrderByDate() {
        // Arrange
        IpscMatch earlier = match(8L, "Earlier Match");
        IpscMatch later = match(5L, "Later Match");
        when(shooterResultRepository.findAllWithDetails()).thenReturn(List.of());
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(earlier, later));

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertEquals(List.of(8L, 5L), holders.stream().map(holder -> holder.getMatch().getMatchId()).toList());
        verify(ipscMatchRepository).findAll(MATCH_ORDER);
    }

    @Test
    void testGetAllShooterResults_whenNoMatches_thenReturnsEmptyList() {
        // Arrange
        when(shooterResultRepository.findAllWithDetails()).thenReturn(List.of());
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of());

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertTrue(holders.isEmpty());
    }

    @Test
    void testGetAllShooterResults_whenResultsExist_thenGroupsThemByMatch() {
        // Arrange
        ShooterResult first = shooterResult(10L, 2L);
        ShooterResult second = shooterResult(11L, 2L);
        ShooterResult other = shooterResult(12L, 3L);
        when(shooterResultRepository.findAllWithDetails()).thenReturn(List.of(first, other, second));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(first.getMatch(), other.getMatch()));

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertEquals(2, holders.size());
        assertEquals(2L, holders.get(0).getMatch().getMatchId());
        assertEquals(List.of(10L, 11L),
                holders.get(0).getShooterResults().stream().map(ShooterResultResponse::getShooterResultId).toList());
        assertEquals(3L, holders.get(1).getMatch().getMatchId());
        assertEquals(1, holders.get(1).getShooterResults().size());
    }

    // getShooterResults()
    @Test
    void testGetShooterResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterResultService.getShooterResults(99L));
        assertEquals("No IPSC match found with ID 99", exception.getMessage());
        verifyNoInteractions(shooterResultRepository);
    }

    @Test
    void testGetShooterResults_whenMatchExists_thenMapsEachShooterResult() throws NonFatalException {
        // Arrange
        ShooterResult shooterResult = shooterResult(10L, 2L);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(shooterResult.getMatch()));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(shooterResult));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertEquals("Club Championship", holder.getMatch().getMatchName());
        assertEquals(LocalDateTime.of(2026, 9, 12, 0, 0), holder.getMatch().getMatchDate());
        assertEquals(1, holder.getShooterResults().size());
        ShooterResultResponse result = holder.getShooterResults().getFirst();
        assertEquals(10L, result.getShooterResultId());
        assertEquals(1L, result.getCompetitorId());
        assertEquals(5L, result.getMatchCompetitorId());
        assertEquals(List.of("Jane Doe", "JD Doe"), result.getCompetitorNames());
        assertEquals(7, result.getCompetitorNumber());
        assertEquals(CompetitorCategory.JUNIOR, result.getCompetitorCategory());
        assertEquals(Division.OPEN, result.getDivision());
        assertEquals(3, result.getRankInLog());
        assertEquals("87.50", result.getPercentageInLog());
        assertEquals(LocalDateTime.of(2026, 10, 1, 8, 0), result.getDateCalculated());
    }

    @Test
    void testGetShooterResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match(2L, "Club Championship")));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertTrue(holder.getShooterResults().isEmpty());
    }

    @Test
    void testGetShooterResults_whenNicknameIsAbsent_thenListsOnlyTheFullName() throws NonFatalException {
        // Arrange
        ShooterResult shooterResult = shooterResult(10L, 2L);
        shooterResult.getCompetitor().setNickName(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(shooterResult.getMatch()));
        when(shooterResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(shooterResult));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(List.of("Jane Doe"), holder.getShooterResults().getFirst().getCompetitorNames());
    }

    // Helpers
    private MatchCompetitor matchCompetitor(Long id, FirearmType firearmType, Division division, String points) {
        Competitor competitor = new Competitor();
        competitor.setId(id);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setIsVerified(true);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(id);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(firearmType);
        matchCompetitor.setDivision(division);
        matchCompetitor.setPoints(points == null ? null : new BigDecimal(points));
        return matchCompetitor;
    }

    private Integer rankOf(List<ShooterResult> shooterResults, MatchCompetitor matchCompetitor) {
        return resultOf(shooterResults, matchCompetitor).getRankInLog();
    }

    private BigDecimal percentageOf(List<ShooterResult> shooterResults, MatchCompetitor matchCompetitor) {
        return resultOf(shooterResults, matchCompetitor).getPercentageInLog();
    }

    private ShooterResult resultOf(List<ShooterResult> shooterResults, MatchCompetitor matchCompetitor) {
        return shooterResults.stream()
                .filter(shooterResult -> shooterResult.getMatchCompetitor() == matchCompetitor)
                .findFirst()
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private List<ShooterResult> savedShooterResults() {
        ArgumentCaptor<List<ShooterResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).replaceShooterResults(captor.capture(), anyList());
        return new ArrayList<>(captor.getValue());
    }

    @SuppressWarnings("unchecked")
    private List<ShooterResult> deletedShooterResults() {
        ArgumentCaptor<List<ShooterResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).replaceShooterResults(anyList(), captor.capture());
        return new ArrayList<>(captor.getValue());
    }

    private IpscMatch match(Long id, String name) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        match.setName(name);
        match.setScheduledDate(LocalDateTime.of(2026, 9, 12, 0, 0));
        return match;
    }

    private ShooterResult shooterResult(Long id, Long matchId) {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setIsVerified(true);
        competitor.setNickName("JD");
        competitor.setCompetitorNumber(7);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(5L);

        ShooterResult shooterResult = new ShooterResult();
        shooterResult.setId(id);
        shooterResult.setMatch(match(matchId, matchId == 2L ? "Club Championship" : "Other Match"));
        shooterResult.setCompetitor(competitor);
        shooterResult.setMatchCompetitor(matchCompetitor);
        shooterResult.setCompetitorCategory(CompetitorCategory.JUNIOR);
        shooterResult.setDivision(Division.OPEN);
        shooterResult.setRankInLog(3);
        shooterResult.setPercentageInLog(new BigDecimal("87.5"));
        shooterResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        return shooterResult;
    }
}
