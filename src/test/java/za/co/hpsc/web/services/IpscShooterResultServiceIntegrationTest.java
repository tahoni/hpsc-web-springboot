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

    // getShooterResults()
    @Test
    void testGetShooterResults_whenMatchHasResults_thenReturnsOnlyThatMatchBestPercentageFirst()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A");
        IpscMatch otherMatch = createMatch("Match B");
        Competitor first = createCompetitor("HPSC-SR-001");
        Competitor second = createCompetitor("HPSC-SR-002");
        Competitor third = createCompetitor("HPSC-SR-003");
        createMatchCompetitor(first, match, new BigDecimal("50"));
        createMatchCompetitor(second, match, null);
        createMatchCompetitor(third, match, new BigDecimal("90"));
        createMatchCompetitor(first, otherMatch, new BigDecimal("100"));

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(match.getId());

        // Assert
        List<ShooterResultResponse> results = holder.getShooterResults();
        assertEquals(match.getId(), holder.getMatch().getMatchId());
        assertEquals("Match A", holder.getMatch().getMatchName());
        assertEquals(3, results.size());
        assertEquals(third.getId(), results.get(0).getCompetitorId());
        assertEquals(first.getId(), results.get(1).getCompetitorId());
        assertEquals(second.getId(), results.get(2).getCompetitorId());
    }

    @Test
    void testGetShooterResults_whenMatchHasNoResults_thenReturnsTheMatchWithNoResults() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Empty Match");

        // Act
        ShooterResponseHolder holder = ipscShooterResultService.getShooterResults(match.getId());

        // Assert
        assertEquals(match.getId(), holder.getMatch().getMatchId());
        assertTrue(holder.getShooterResults().isEmpty());
    }

    @Test
    void testGetShooterResults_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultService.getShooterResults(999_999L));
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenSeveralMatches_thenGroupsThemByMatchIncludingOnesWithNoResults() {
        // Arrange
        IpscMatch match = createMatch("Match A");
        IpscMatch otherMatch = createMatch("Match B");
        Competitor competitor = createCompetitor("HPSC-SR-004");
        createMatchCompetitor(competitor, match, new BigDecimal("80"));
        createMatchCompetitor(competitor, otherMatch, new BigDecimal("70"));
        IpscMatch emptyMatch = createMatch("Match C");

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

    // Helpers
    private Competitor createCompetitor(String clubNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setClubNumber(clubNumber);
        return competitorRepository.save(competitor);
    }

    private IpscMatch createMatch(String name) {
        return createMatch(name, LocalDate.of(2026, 9, 12));
    }

    private IpscMatch createMatch(String name, LocalDate date) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(date.atStartOfDay());
        return ipscMatchRepository.save(match);
    }

    private void createMatchCompetitor(Competitor competitor, IpscMatch match, BigDecimal percentage) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPercentage(percentage);
        matchCompetitorRepository.saveAndFlush(matchCompetitor);
    }
}
