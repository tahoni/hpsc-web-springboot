package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.impl.IpscShooterResultServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @InjectMocks
    private IpscShooterResultServiceImpl ipscShooterResultService;

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
        assertEquals(new BigDecimal("87.5"), result.getPercentageInLog());
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
