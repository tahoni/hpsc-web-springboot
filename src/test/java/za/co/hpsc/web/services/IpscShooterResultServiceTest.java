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
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.impl.IpscShooterResultServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscShooterResultServiceTest {

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @InjectMocks
    private IpscShooterResultServiceImpl ipscShooterResultService;

    private static final Sort MATCH_ORDER = Sort.by("scheduledDate", "id");

    // getShooterResults()
    @Test
    void testGetShooterResults_whenMatchExists_thenMapsEachMatchCompetitor() throws NonFatalException {
        // Arrange
        MatchCompetitor matchCompetitor = matchCompetitor();
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(matchCompetitor.getMatch()));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertEquals("Club Championship", holder.getMatch().getMatchName());
        assertEquals(LocalDateTime.of(2026, 9, 12, 0, 0), holder.getMatch().getMatchDate());
        assertEquals(1, holder.getShooterResults().size());
        ShooterResultResponse result = holder.getShooterResults().getFirst();
        assertEquals(1L, result.getCompetitorId());
        assertEquals(List.of("Jane Doe", "JD Doe"), result.getCompetitorNames());
        assertEquals(7, result.getCompetitorNumber());
        assertEquals(ClubIdentifier.HPSC, result.getMatchClub());
        assertEquals(CompetitorCategory.JUNIOR, result.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, result.getFirearmType());
        assertEquals(Division.OPEN, result.getDivision());
        assertEquals(PowerFactor.MAJOR, result.getPowerFactor());
        assertEquals(new BigDecimal("95.5"), result.getPoints());
        assertEquals(new BigDecimal("98.25"), result.getPercentage());
        assertEquals(new BigDecimal("41.5"), result.getTime());
        assertEquals(new BigDecimal("3"), result.getOverallRanking());
        assertEquals(new BigDecimal("2"), result.getClubRanking());
        assertEquals(Boolean.FALSE, result.getIsVisitor());
    }

    @Test
    void testGetShooterResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(matchCompetitor().getMatch()));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L)).thenReturn(List.of());

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(2L, holder.getMatch().getMatchId());
        assertTrue(holder.getShooterResults().isEmpty());
    }

    @Test
    void testGetShooterResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscShooterResultService.getShooterResults(99L));
        assertEquals("No IPSC match found with ID 99", exception.getMessage());
        verifyNoInteractions(matchCompetitorRepository);
    }

    @Test
    void testGetShooterResults_whenNicknameIsAbsent_thenListsOnlyTheFullName() throws NonFatalException {
        // Arrange
        MatchCompetitor matchCompetitor = matchCompetitor();
        matchCompetitor.getCompetitor().setNickName(null);
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(matchCompetitor.getMatch()));
        when(matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(2L))
                .thenReturn(List.of(matchCompetitor));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(2L);

        // Assert
        assertEquals(List.of("Jane Doe"), holder.getShooterResults().getFirst().getCompetitorNames());
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenResultsExist_thenGroupsThemByMatch() {
        // Arrange
        MatchCompetitor first = matchCompetitor();
        MatchCompetitor second = matchCompetitor();
        MatchCompetitor other = matchCompetitor();
        other.getMatch().setId(3L);
        other.getMatch().setName("Other Match");
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch()).thenReturn(List.of(first, other, second));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(first.getMatch(), other.getMatch()));

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertEquals(2, holders.size());
        assertEquals(2L, holders.get(0).getMatch().getMatchId());
        assertEquals(2, holders.get(0).getShooterResults().size());
        assertEquals(3L, holders.get(1).getMatch().getMatchId());
        assertEquals("Other Match", holders.get(1).getMatch().getMatchName());
        assertEquals(1, holders.get(1).getShooterResults().size());
    }

    @Test
    void testGetAllShooterResults_whenMatchHasNoResults_thenReturnsItWithNoResults() {
        // Arrange
        MatchCompetitor matchCompetitor = matchCompetitor();
        IpscMatch emptyMatch = new IpscMatch();
        emptyMatch.setId(4L);
        emptyMatch.setName("Empty Match");
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch()).thenReturn(List.of(matchCompetitor));
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of(matchCompetitor.getMatch(), emptyMatch));

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
        IpscMatch earlier = new IpscMatch();
        earlier.setId(8L);
        earlier.setName("Earlier Match");
        earlier.setScheduledDate(LocalDateTime.of(2026, 1, 10, 0, 0));
        IpscMatch later = new IpscMatch();
        later.setId(5L);
        later.setName("Later Match");
        later.setScheduledDate(LocalDateTime.of(2026, 6, 20, 0, 0));
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch()).thenReturn(List.of());
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
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch()).thenReturn(List.of());
        when(ipscMatchRepository.findAll(MATCH_ORDER)).thenReturn(List.of());

        // Act
        List<ShooterResponseHolder> results = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertTrue(results.isEmpty());
    }

    // Helpers
    private MatchCompetitor matchCompetitor() {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setNickName("JD");
        competitor.setCompetitorNumber(7);

        IpscMatch match = new IpscMatch();
        match.setId(2L);
        match.setName("Club Championship");
        match.setScheduledDate(LocalDateTime.of(2026, 9, 12, 0, 0));

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setMatchClub(ClubIdentifier.HPSC);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPoints(new BigDecimal("95.5"));
        matchCompetitor.setPercentage(new BigDecimal("98.25"));
        matchCompetitor.setTime(new BigDecimal("41.5"));
        matchCompetitor.setOverallRanking(new BigDecimal("3"));
        matchCompetitor.setClubRanking(new BigDecimal("2"));
        matchCompetitor.setIsVisitor(false);
        return matchCompetitor;
    }
}
