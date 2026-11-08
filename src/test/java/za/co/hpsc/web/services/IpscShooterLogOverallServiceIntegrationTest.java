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
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponse;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscShooterLogOverallService} - exercised through the interface type,
 * with a real Spring-wired {@code IpscShooterLogOverallServiceImpl} bean backed by the H2 {@code test} profile
 * database.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscShooterLogOverallServiceIntegrationTest {

    @Autowired
    private IpscShooterLogOverallService ipscShooterLogOverallService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @Autowired
    private ShooterLogRepository shooterLogRepository;

    @Autowired
    private ShooterLogOverallRepository shooterLogOverallRepository;

    // calculateShooterLogOverall()
    @Test
    void testCalculateShooterLogOverall_whenCalledTwice_thenUpdatesTheSameOveralls() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog shooterLog = createShooterLog(match);
        createMatchCompetitor(createCompetitor("HPSC-SLO-001"), match, "100");
        createMatchCompetitor(createCompetitor("HPSC-SLO-002"), match, "50");
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        assertEquals(2, holder.getShooterLogOveralls().size());
        assertEquals(2, shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId()).size());
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasSeveralMatches_thenAveragesAndRanksAcrossThem()
            throws NonFatalException {
        // Arrange
        IpscMatch first = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch second = createMatch("Match B", LocalDate.of(2026, 9, 19));
        ShooterLog shooterLog = createShooterLog(first, second);
        Competitor consistent = createCompetitor("HPSC-SLO-003");
        Competitor streaky = createCompetitor("HPSC-SLO-004");
        createMatchCompetitor(consistent, first, "90");
        createMatchCompetitor(streaky, first, "100");
        createMatchCompetitor(consistent, second, "100");
        createMatchCompetitor(streaky, second, "60");

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        // consistent: (90 / 100 * 100 + 100) / 2 = 95.00; streaky: (100 + 60 / 100 * 100) / 2 = 80.00
        ShooterLogOverallResponse consistentOverall = overallOf(holder, consistent);
        ShooterLogOverallResponse streakyOverall = overallOf(holder, streaky);
        assertEquals("95.00", consistentOverall.getPercentageInLog());
        assertEquals(1, consistentOverall.getRankInLog());
        assertEquals("80.00", streakyOverall.getPercentageInLog());
        assertEquals(2, streakyOverall.getRankInLog());
        assertNotNull(streakyOverall.getDateCalculated());
    }

    @Test
    void testCalculateShooterLogOverall_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(999_999L));
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasNoMatches_thenReturnsTheLogWithNoOveralls()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createShooterLog();

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        assertEquals(shooterLog.getId(), holder.getShooterLog().getShooterLogId());
        assertTrue(holder.getShooterLogOveralls().isEmpty());
    }

    // getAllShooterLogOveralls()
    @Test
    void testGetAllShooterLogOveralls_whenSeveralLogs_thenGroupsThemByLogIncludingOnesWithNoOveralls()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog calculated = createShooterLog(match);
        createMatchCompetitor(createCompetitor("HPSC-SLO-005"), match, "80");
        ipscShooterLogOverallService.calculateShooterLogOverall(calculated.getId());
        ShooterLog empty = createShooterLog();

        // Act
        List<ShooterLogOverallResponseHolder> holders = ipscShooterLogOverallService.getAllShooterLogOveralls();

        // Assert
        assertTrue(holders.stream().anyMatch(holder -> calculated.getId().equals(holder.getShooterLog()
                .getShooterLogId()) && (holder.getShooterLogOveralls().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> empty.getId().equals(holder.getShooterLog().getShooterLogId())
                && holder.getShooterLogOveralls().isEmpty()));
    }

    // getShooterLogOveralls()
    @Test
    void testGetShooterLogOveralls_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallService.getShooterLogOveralls(999_999L));
    }

    @Test
    void testGetShooterLogOveralls_whenLogHasOveralls_thenReturnsOnlyThatLogsOveralls() throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog shooterLog = createShooterLog(match);
        ShooterLog otherLog = createShooterLog(match);
        Competitor competitor = createCompetitor("HPSC-SLO-006");
        ShooterLogOverall overall = createShooterLogOverall(shooterLog, competitor);
        createShooterLogOverall(otherLog, competitor);

        // Act
        ShooterLogOverallResponseHolder holder = ipscShooterLogOverallService.getShooterLogOveralls(shooterLog.getId());

        // Assert
        assertEquals(shooterLog.getId(), holder.getShooterLog().getShooterLogId());
        assertEquals(List.of(match.getId()), holder.getShooterLog().getMatchIds());
        assertEquals(List.of(overall.getId()),
                holder.getShooterLogOveralls().stream().map(ShooterLogOverallResponse::getShooterLogOverallId)
                        .toList());
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

    private ShooterLog createShooterLog(IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setStartDate(LocalDate.of(2026, 9, 1));
        shooterLog.setEndDate(LocalDate.of(2026, 9, 30));
        shooterLog.setMatches(new ArrayList<>(List.of(matches)));
        return shooterLogRepository.saveAndFlush(shooterLog);
    }

    private ShooterLogOverall createShooterLogOverall(ShooterLog shooterLog, Competitor competitor) {
        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setShooterLog(shooterLog);
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.JUNIOR);
        overall.setDivision(Division.OPEN);
        return shooterLogOverallRepository.saveAndFlush(overall);
    }

    private ShooterLogOverallResponse overallOf(ShooterLogOverallResponseHolder holder, Competitor competitor) {
        return holder.getShooterLogOveralls().stream()
                .filter(overall -> competitor.getId().equals(overall.getCompetitorId()))
                .findFirst()
                .orElseThrow();
    }
}
