package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponseHolder;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchResultRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscMatchResultService} - exercised through the interface type, with
 * a real Spring-wired {@code IpscMatchResultServiceImpl} bean backed by the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscMatchResultServiceIntegrationTest {

    @Autowired
    private IpscMatchResultService ipscMatchResultService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    // calculateMatchResults()
    @Test
    void testCalculateMatchResults_whenCalledTwice_thenUpdatesTheSameResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        createMatchCompetitor(createCompetitor("HPSC-SR-006"), match, "100");
        createMatchCompetitor(createCompetitor("HPSC-SR-007"), match, "50");
        ipscMatchResultService.calculateMatchResults(match.getId());

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.calculateMatchResults(match.getId());

        // Assert
        assertEquals(2, holder.getMatchResults().size());
        assertEquals(2, matchResultRepository.findAllByMatchIdWithDetails(match.getId()).size());
    }

    @Test
    void testCalculateMatchResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchResultService.calculateMatchResults(999_999L));
    }

    @Test
    void testCalculateMatchResults_whenMatchHasCompetitors_thenPersistsRanksAndPercentages()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        Competitor winner = createCompetitor("HPSC-SR-008");
        Competitor runnerUp = createCompetitor("HPSC-SR-009");
        createMatchCompetitor(winner, match, "200");
        createMatchCompetitor(runnerUp, match, "150");

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.calculateMatchResults(match.getId());

        // Assert
        assertEquals(2, holder.getMatchResults().size());
        MatchResultResponse first = holder.getMatchResults().stream()
                .filter(result -> winner.getId().equals(result.getCompetitorId())).findFirst().orElseThrow();
        MatchResultResponse second = holder.getMatchResults().stream()
                .filter(result -> runnerUp.getId().equals(result.getCompetitorId())).findFirst().orElseThrow();
        assertEquals(1, first.getRankInLog());
        assertEquals("100.00", first.getPercentageInLog());
        assertEquals(2, second.getRankInLog());
        assertEquals("75.00", second.getPercentageInLog());
        assertNotNull(second.getDateCalculated());
    }

    @Test
    void testCalculateMatchResults_whenPointsAreLaterCleared_thenRemovesThatCompetitorsResult()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        Competitor kept = createCompetitor("HPSC-SR-010");
        Competitor cleared = createCompetitor("HPSC-SR-011");
        createMatchCompetitor(kept, match, "100");
        MatchCompetitor clearedMatchCompetitor = createMatchCompetitor(cleared, match, "50");
        createMatchCompetitor(createCompetitor("HPSC-SR-012"), match, "0");
        assertEquals(2, ipscMatchResultService.calculateMatchResults(match.getId())
                .getMatchResults().size());
        clearedMatchCompetitor.setPoints(null);
        matchCompetitorRepository.saveAndFlush(clearedMatchCompetitor);

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.calculateMatchResults(match.getId());

        // Assert
        assertEquals(1, holder.getMatchResults().size());
        assertEquals(kept.getId(), holder.getMatchResults().getFirst().getCompetitorId());
        assertEquals(1, matchResultRepository.findAllByMatchIdWithDetails(match.getId()).size());
    }

    // getAllMatchResults()
    @Test
    void testGetAllMatchResults_whenMatchesHaveDifferentDates_thenOrdersThemByDate() {
        // Arrange
        IpscMatch later = createMatch("Later Match", LocalDate.of(2090, 6, 20));
        IpscMatch earlier = createMatch("Earlier Match", LocalDate.of(2090, 1, 10));

        // Act
        List<Long> matchIds = ipscMatchResultService.getAllMatchResults().stream()
                .map(holder -> holder.getMatch().getMatchId())
                .toList();

        // Assert
        assertTrue(matchIds.indexOf(earlier.getId()) < matchIds.indexOf(later.getId()));
    }

    @Test
    void testGetAllMatchResults_whenSeveralMatches_thenGroupsThemByMatchIncludingOnesWithNoResults() {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch otherMatch = createMatch("Match B", LocalDate.of(2026, 9, 12));
        Competitor competitor = createCompetitor("HPSC-SR-004");
        createMatchResult(competitor, match, "80");
        createMatchResult(competitor, otherMatch, "70");
        IpscMatch emptyMatch = createMatch("Match C", LocalDate.of(2026, 9, 12));

        // Act
        List<MatchResultResponseHolder> holders = ipscMatchResultService.getAllMatchResults();

        // Assert
        assertTrue(holders.stream().anyMatch(holder -> match.getId().equals(holder.getMatch().getMatchId())
                && (holder.getMatchResults().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> otherMatch.getId().equals(holder.getMatch().getMatchId())
                && (holder.getMatchResults().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> emptyMatch.getId().equals(holder.getMatch().getMatchId())
                && holder.getMatchResults().isEmpty()));
    }

    // getMatchResults()
    @Test
    void testGetMatchResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchResultService.getMatchResults(999_999L));
    }

    @Test
    void testGetMatchResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Empty Match", LocalDate.of(2026, 9, 12));

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.getMatchResults(match.getId());

        // Assert
        assertEquals(match.getId(), holder.getMatch().getMatchId());
        assertTrue(holder.getMatchResults().isEmpty());
    }

    @Test
    void testGetMatchResults_whenMatchHasResults_thenReturnsOnlyThatMatchsResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch otherMatch = createMatch("Match B", LocalDate.of(2026, 9, 12));
        Competitor first = createCompetitor("HPSC-SR-001");
        Competitor second = createCompetitor("HPSC-SR-002");
        MatchResult firstResult = createMatchResult(first, match, "90");
        MatchResult secondResult = createMatchResult(second, match, "80");
        createMatchResult(first, otherMatch, "100");

        // Act
        MatchResultResponseHolder holder = ipscMatchResultService.getMatchResults(match.getId());

        // Assert
        assertEquals("Match A", holder.getMatch().getMatchName());
        assertEquals(List.of(firstResult.getId(), secondResult.getId()),
                holder.getMatchResults().stream().map(MatchResultResponse::getMatchResultId).toList());
    }

    // Helpers
    private Competitor createCompetitor(String clubNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setIsVerified(true);
        competitor.setClubNumber(clubNumber);
        return competitorRepository.save(competitor);
    }

    private IpscMatch createMatch(String name, LocalDate date) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(date.atStartOfDay());
        return ipscMatchRepository.save(match);
    }

    private MatchCompetitor createMatchCompetitor(Competitor competitor, IpscMatch match, String points) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPoints(new BigDecimal(points));
        return matchCompetitorRepository.saveAndFlush(matchCompetitor);
    }

    private MatchResult createMatchResult(Competitor competitor, IpscMatch match, String percentageInLog) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitorRepository.saveAndFlush(matchCompetitor);

        MatchResult matchResult = new MatchResult();
        matchResult.setMatch(match);
        matchResult.setCompetitor(competitor);
        matchResult.setMatchCompetitor(matchCompetitor);
        matchResult.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchResult.setDivision(Division.OPEN);
        matchResult.setPercentageInLog(new BigDecimal(percentageInLog));
        return matchResultRepository.saveAndFlush(matchResult);
    }
}
