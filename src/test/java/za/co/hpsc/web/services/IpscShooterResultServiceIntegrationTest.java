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
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscShooterResultService} - exercised through the interface type, with
 * a real Spring-wired {@code IpscShooterResultServiceImpl} bean backed by the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscShooterResultServiceIntegrationTest {

    @Autowired
    private IpscShooterResultService ipscShooterResultService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @Autowired
    private ShooterResultRepository shooterResultRepository;

    // calculateMatchShooterResult()
    @Test
    void testCalculateMatchShooterResult_whenCalledTwice_thenUpdatesTheSameResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        createMatchCompetitor(createCompetitor("HPSC-SR-006"), match, "100");
        createMatchCompetitor(createCompetitor("HPSC-SR-007"), match, "50");
        ipscShooterResultService.calculateMatchShooterResult(match.getId());

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.calculateMatchShooterResult(match.getId());

        // Assert
        assertEquals(2, holder.getShooterResults().size());
        assertEquals(2, shooterResultRepository.findAllByMatchIdWithDetails(match.getId()).size());
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultService.calculateMatchShooterResult(999_999L));
    }

    @Test
    void testCalculateMatchShooterResult_whenMatchHasCompetitors_thenPersistsRanksAndPercentages()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        Competitor winner = createCompetitor("HPSC-SR-008");
        Competitor runnerUp = createCompetitor("HPSC-SR-009");
        createMatchCompetitor(winner, match, "200");
        createMatchCompetitor(runnerUp, match, "150");

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.calculateMatchShooterResult(match.getId());

        // Assert
        assertEquals(2, holder.getShooterResults().size());
        ShooterResultResponse first = holder.getShooterResults().stream()
                .filter(result -> winner.getId().equals(result.getCompetitorId())).findFirst().orElseThrow();
        ShooterResultResponse second = holder.getShooterResults().stream()
                .filter(result -> runnerUp.getId().equals(result.getCompetitorId())).findFirst().orElseThrow();
        assertEquals(1, first.getRankInLog());
        assertEquals("100.00", first.getPercentageInLog());
        assertEquals(2, second.getRankInLog());
        assertEquals("75.00", second.getPercentageInLog());
        assertNotNull(second.getDateCalculated());
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenMatchesHaveDifferentDates_thenOrdersThemByDate() {
        // Arrange
        IpscMatch later = createMatch("Later Match", LocalDate.of(2090, 6, 20));
        IpscMatch earlier = createMatch("Earlier Match", LocalDate.of(2090, 1, 10));

        // Act
        List<Long> matchIds = ipscShooterResultService.getAllShooterResults().stream()
                .map(holder -> holder.getMatch().getMatchId())
                .toList();

        // Assert
        assertTrue(matchIds.indexOf(earlier.getId()) < matchIds.indexOf(later.getId()));
    }

    @Test
    void testGetAllShooterResults_whenSeveralMatches_thenGroupsThemByMatchIncludingOnesWithNoResults() {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch otherMatch = createMatch("Match B", LocalDate.of(2026, 9, 12));
        Competitor competitor = createCompetitor("HPSC-SR-004");
        createShooterResult(competitor, match, "80");
        createShooterResult(competitor, otherMatch, "70");
        IpscMatch emptyMatch = createMatch("Match C", LocalDate.of(2026, 9, 12));

        // Act
        List<ShooterResponseHolder> holders = ipscShooterResultService.getAllShooterResults();

        // Assert
        assertTrue(holders.stream().anyMatch(holder -> match.getId().equals(holder.getMatch().getMatchId())
                && (holder.getShooterResults().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> otherMatch.getId().equals(holder.getMatch().getMatchId())
                && (holder.getShooterResults().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> emptyMatch.getId().equals(holder.getMatch().getMatchId())
                && holder.getShooterResults().isEmpty()));
    }

    // getShooterResults()
    @Test
    void testGetShooterResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultService.getShooterResults(999_999L));
    }

    @Test
    void testGetShooterResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Empty Match", LocalDate.of(2026, 9, 12));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(match.getId());

        // Assert
        assertEquals(match.getId(), holder.getMatch().getMatchId());
        assertTrue(holder.getShooterResults().isEmpty());
    }

    @Test
    void testGetShooterResults_whenMatchHasResults_thenReturnsOnlyThatMatchsResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch otherMatch = createMatch("Match B", LocalDate.of(2026, 9, 12));
        Competitor first = createCompetitor("HPSC-SR-001");
        Competitor second = createCompetitor("HPSC-SR-002");
        ShooterResult firstResult = createShooterResult(first, match, "90");
        ShooterResult secondResult = createShooterResult(second, match, "80");
        createShooterResult(first, otherMatch, "100");

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(match.getId());

        // Assert
        assertEquals("Match A", holder.getMatch().getMatchName());
        assertEquals(List.of(firstResult.getId(), secondResult.getId()),
                holder.getShooterResults().stream().map(ShooterResultResponse::getShooterResultId).toList());
    }

    // Helpers
    private Competitor createCompetitor(String clubNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
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

    private ShooterResult createShooterResult(Competitor competitor, IpscMatch match, String percentageInLog) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitorRepository.saveAndFlush(matchCompetitor);

        ShooterResult shooterResult = new ShooterResult();
        shooterResult.setMatch(match);
        shooterResult.setCompetitor(competitor);
        shooterResult.setMatchCompetitor(matchCompetitor);
        shooterResult.setCompetitorCategory(CompetitorCategory.JUNIOR);
        shooterResult.setDivision(Division.OPEN);
        shooterResult.setPercentageInLog(new BigDecimal(percentageInLog));
        return shooterResultRepository.saveAndFlush(shooterResult);
    }
}
