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
import za.co.hpsc.web.domain.MatchResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponseHolder;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchResultRepository;
import za.co.hpsc.web.services.impl.IpscMatchResultServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscMatchResultServiceTest {

    private static final Sort MATCH_ORDER = Sort.by("scheduledDate", "id");

    @Mock
    private MatchResultRepository matchResultRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private IpscMatchResultServiceImpl ipscMatchResultService;

    // calculateMatchResults()
    @Test
    void testCalculateMatchResults_whenCompetitorsHaveNoPoints_thenExcludesThemFromTheResults()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor unscored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L)).thenReturn(List.of(unscored));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchResults_whenCompetitorIsNotVerified_thenExcludesThemAndRanksTheOthers()
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
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        // The unverified competitors scored higher, but neither counts as the winner
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, verified));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, verified));
    }

    @Test
    void testCalculateMatchResults_whenCompetitorIsNoLongerVerified_thenDeletesTheStaleResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor unverified = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        unverified.getCompetitor().setIsVerified(false);
        MatchResult stale = matchResult(10L, 2L);
        stale.setMatchCompetitor(unverified);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(unverified));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(stale));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertTrue(savedMatchResults().isEmpty());
        assertEquals(List.of(stale), deletedMatchResults());
    }

    @Test
    void testCalculateMatchResults_whenCompetitorHasNullPoints_thenExcludesThemAndRanksTheOthers()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor unscored = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(unscored, scored));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, scored));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, scored));
    }

    @Test
    void testCalculateMatchResults_whenCompetitorHasZeroPoints_thenExcludesThemAndRanksTheOthers()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor zero = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "0");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(zero, scored));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, saved.size());
        assertEquals(1, rankOf(saved, scored));
        assertEquals(new BigDecimal("100.00"), percentageOf(saved, scored));
    }

    @Test
    void testCalculateMatchResults_whenPointsWereClearedAfterCalculation_thenDeletesTheStaleResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor scored = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor cleared = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, null);
        MatchResult existingScored = matchResult(10L, 2L);
        existingScored.setMatchCompetitor(scored);
        MatchResult stale = matchResult(11L, 2L);
        stale.setMatchCompetitor(cleared);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(scored, cleared));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L))
                .thenReturn(List.of(existingScored, stale));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(List.of(existingScored), savedMatchResults());
        assertEquals(List.of(stale), deletedMatchResults());
    }

    @Test
    void testCalculateMatchResults_whenOnlyStaleResultsRemain_thenDeletesThemAndSavesNone()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor cleared = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "0");
        MatchResult stale = matchResult(10L, 2L);
        stale.setMatchCompetitor(cleared);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(cleared));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(stale));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertTrue(savedMatchResults().isEmpty());
        assertEquals(List.of(stale), deletedMatchResults());
    }

    @Test
    void testCalculateMatchResults_whenMatchCompetitorsExist_thenPopulatesEachMatchResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        MatchResult saved = savedMatchResults().getFirst();
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
    void testCalculateMatchResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchResultService.calculateMatchResults(99L));
        verifyNoInteractions(matchCompetitorRepository, transactionService);
    }

    @Test
    void testCalculateMatchResults_whenResultAlreadyExists_thenUpdatesItInsteadOfAddingOne()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        matchCompetitor.setDateUpdated(LocalDateTime.of(2026, 10, 2, 8, 0));
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        existing.setRankInLog(9);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, saved.size());
        assertSame(existing, saved.getFirst());
        assertEquals(1, existing.getRankInLog());
    }

    @Test
    void testCalculateMatchResults_whenResultsAreUpToDate_thenReturnsThemWithoutRecalculating()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 9, 30, 8, 0));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        matchCompetitor.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 0));
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatch(match);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(1, holder.getMatchResults().size());
        assertEquals(10L, holder.getMatchResults().getFirst().getMatchResultId());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchResults_whenMatchWasUpdatedAfterCalculation_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 2, 8, 0));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(1, savedMatchResults().size());
    }

    @Test
    void testCalculateMatchResults_whenMatchWasUpdatedLessThanFiveMinutesAfterCalculation_thenDoesNotRecalculate()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 4, 59));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchResults_whenMatchWasUpdatedFiveMinutesAfterCalculation_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 8, 5));
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(1, savedMatchResults().size());
    }

    @Test
    void testCalculateMatchResults_whenMatchCompetitorHasNoResultYet_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor withResult = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor withoutResult = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "40");
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(withResult);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(withResult, withoutResult));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(2, savedMatchResults().size());
    }

    @Test
    void testCalculateMatchResults_whenExistingResultWasNeverCalculated_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor matchCompetitor = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchResult existing = matchResult(10L, 2L);
        existing.setMatchCompetitor(matchCompetitor);
        existing.setDateCalculated(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(existing));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertEquals(1, savedMatchResults().size());
    }

    @Test
    void testCalculateMatchResults_whenMatchHasNoCompetitors_thenReturnsWithoutRecalculating()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L)).thenReturn(List.of());
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        assertTrue(holder.getMatchResults().isEmpty());
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchResults_whenResultsWereCalculatedAtDifferentTimes_thenComparesAgainstTheOldest()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        match.setDateUpdated(LocalDateTime.of(2026, 10, 1, 7, 0));
        MatchCompetitor newer = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "80");
        MatchCompetitor older = matchCompetitor(2L, FirearmType.HANDGUN, Division.OPEN, "40");
        MatchResult newerResult = matchResult(10L, 2L);
        newerResult.setMatchCompetitor(newer);
        newerResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        MatchResult olderResult = matchResult(11L, 2L);
        olderResult.setMatchCompetitor(older);
        olderResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 7, 30));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(newer, older));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(newerResult, olderResult));

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        verifyNoInteractions(transactionService);
    }

    @Test
    void testCalculateMatchResults_whenSeveralCompetitorCategories_thenRanksThemTogether()
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
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, rankOf(saved, junior));
        assertEquals(2, rankOf(saved, lady));
        assertEquals(3, rankOf(saved, none));
        assertEquals(4, rankOf(saved, otherNone));
        assertEquals(new BigDecimal("30.00"), percentageOf(saved, otherNone));
    }

    @Test
    void testCalculateMatchResults_whenSeveralFirearmTypesAndDivisions_thenRanksThemTogether()
            throws NonFatalException {
        // Arrange
        IpscMatch match = match(2L, "Club Championship");
        MatchCompetitor handgunOpen = matchCompetitor(1L, FirearmType.HANDGUN, Division.OPEN, "50");
        MatchCompetitor rifleOpen = matchCompetitor(2L, FirearmType.RIFLE, Division.OPEN, "40");
        MatchCompetitor handgunProduction = matchCompetitor(3L, FirearmType.HANDGUN, Division.PRODUCTION, "30");
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(handgunOpen, rifleOpen, handgunProduction));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(3, saved.size());
        assertEquals(1, rankOf(saved, handgunOpen));
        assertEquals(2, rankOf(saved, rifleOpen));
        assertEquals(3, rankOf(saved, handgunProduction));
        assertEquals(new BigDecimal("80.00"), percentageOf(saved, rifleOpen));
        assertEquals(new BigDecimal("60.00"), percentageOf(saved, handgunProduction));
    }

    @Test
    void testCalculateMatchResults_whenCompetitorsAreTied_thenRanksTiedCompetitorsEquallyAndSkipsARank()
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
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        ipscMatchResultService.calculateMatchResults(2L);

        // Assert
        List<MatchResult> saved = savedMatchResults();
        assertEquals(1, rankOf(saved, first));
        assertEquals(1, rankOf(saved, tied));
        assertEquals(3, rankOf(saved, third));
        assertEquals(new BigDecimal("50.00"), percentageOf(saved, third));
        assertEquals(3, saved.size());
        assertTrue(saved.stream().noneMatch(result -> result.getMatchCompetitor() == unscored));
    }

    // getAllMatchResults()
    @Test
    void testGetAllMatchResults_whenMatchHasNoResults_thenReturnsItWithNoResults() {
        // Arrange
        MatchResult matchResult = matchResult(10L, 2L);
        IpscMatch emptyMatch = match(4L, "Empty Match");
        when(matchResultRepository.findAllWithDetails()).thenReturn(List.of(matchResult));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(matchResult.getMatch(), emptyMatch));

        // Act
        List<MatchResultResponseHolder> holders = ipscMatchResultService.getAllMatchResults();

        // Assert
        assertEquals(2, holders.size());
        assertEquals(1, holders.get(0).getMatchResults().size());
        assertEquals(4L, holders.get(1).getMatch().getMatchId());
        assertEquals("Empty Match", holders.get(1).getMatch().getMatchName());
        assertTrue(holders.get(1).getMatchResults().isEmpty());
    }

    @Test
    void testGetAllMatchResults_whenNoMatches_thenReturnsEmptyList() {
        // Arrange
        when(matchResultRepository.findAllWithDetails()).thenReturn(List.of());
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of());

        // Act
        List<MatchResultResponseHolder> holders = ipscMatchResultService.getAllMatchResults();

        // Assert
        assertTrue(holders.isEmpty());
    }

    @Test
    void testGetAllMatchResults_whenResultsExist_thenGroupsThemByMatch() {
        // Arrange
        MatchResult first = matchResult(10L, 2L);
        MatchResult second = matchResult(11L, 2L);
        MatchResult other = matchResult(12L, 3L);
        when(matchResultRepository.findAllWithDetails()).thenReturn(List.of(first, other, second));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(first.getMatch(), other.getMatch()));

        // Act
        List<MatchResultResponseHolder> holders = ipscMatchResultService.getAllMatchResults();

        // Assert
        assertEquals(2, holders.size());
        assertEquals(2L, holders.get(0).getMatch().getMatchId());
        assertEquals(List.of(10L, 11L),
                holders.get(0).getMatchResults().stream().map(MatchResultResponse::getMatchResultId).toList());
        assertEquals(3L, holders.get(1).getMatch().getMatchId());
        assertEquals(1, holders.get(1).getMatchResults().size());
    }

    // getMatchResults()
    @Test
    void testGetMatchResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchResultService.getMatchResults(99L));
        verifyNoInteractions(matchResultRepository);
    }

    @Test
    void testGetMatchResults_whenMatchExists_thenMapsEachMatchResult() throws NonFatalException {
        // Arrange
        MatchResult matchResult = matchResult(10L, 2L);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(matchResult.getMatch()));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(matchResult));

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.getMatchResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertEquals("Club Championship", holder.getMatch().getMatchName());
        assertEquals(1, holder.getMatchResults().size());
        MatchResultResponse result = holder.getMatchResults().getFirst();
        assertEquals(10L, result.getMatchResultId());
        assertEquals(3, result.getRankInLog());
        assertEquals("87.50", result.getPercentageInLog());
    }

    @Test
    void testGetMatchResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match(2L, "Club Championship")));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of());

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.getMatchResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertTrue(holder.getMatchResults().isEmpty());
    }

    @Test
    void testGetMatchResults_whenNicknameIsAbsent_thenListsOnlyTheFullName() throws NonFatalException {
        // Arrange
        MatchResult matchResult = matchResult(10L, 2L);
        matchResult.getCompetitor().setNickName(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(matchResult.getMatch()));
        when(matchResultRepository.findAllByMatchIdWithDetails(2L)).thenReturn(List.of(matchResult));

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.getMatchResults(2L);

        // Assert
        assertEquals(List.of("Jane Doe"), holder.getMatchResults().getFirst().getCompetitorNames());
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

    private Integer rankOf(List<MatchResult> matchResults, MatchCompetitor matchCompetitor) {
        return resultOf(matchResults, matchCompetitor).getRankInLog();
    }

    private BigDecimal percentageOf(List<MatchResult> matchResults, MatchCompetitor matchCompetitor) {
        return resultOf(matchResults, matchCompetitor).getPercentageInLog();
    }

    private MatchResult resultOf(List<MatchResult> matchResults, MatchCompetitor matchCompetitor) {
        return matchResults.stream()
                .filter(matchResult -> matchResult.getMatchCompetitor() == matchCompetitor)
                .findFirst()
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private List<MatchResult> savedMatchResults() {
        ArgumentCaptor<List<MatchResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).replaceMatchResults(captor.capture(), anyList());
        return new ArrayList<>(captor.getValue());
    }

    @SuppressWarnings("unchecked")
    private List<MatchResult> deletedMatchResults() {
        ArgumentCaptor<List<MatchResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionService).replaceMatchResults(anyList(), captor.capture());
        return new ArrayList<>(captor.getValue());
    }

    private IpscMatch match(Long id, String name) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        match.setName(name);
        match.setScheduledDate(LocalDateTime.of(2026, 9, 12, 0, 0));
        return match;
    }

    private MatchResult matchResult(Long id, Long matchId) {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setIsVerified(true);
        competitor.setNickName("JD");
        competitor.setCompetitorNumber(7);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(5L);

        MatchResult matchResult = new MatchResult();
        matchResult.setId(id);
        matchResult.setMatch(match(matchId, matchId == 2L ? "Club Championship" : "Other Match"));
        matchResult.setCompetitor(competitor);
        matchResult.setMatchCompetitor(matchCompetitor);
        matchResult.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchResult.setDivision(Division.OPEN);
        matchResult.setRankInLog(3);
        matchResult.setPercentageInLog(new BigDecimal("87.5"));
        matchResult.setDateCalculated(LocalDateTime.of(2026, 10, 1, 8, 0));
        return matchResult;
    }
}
