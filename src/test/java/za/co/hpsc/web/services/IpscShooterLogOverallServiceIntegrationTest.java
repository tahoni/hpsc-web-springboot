package za.co.hpsc.web.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
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
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponse;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ShooterResultRepository shooterResultRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // calculateShooterLogOverall()
    @Test
    void testCalculateShooterLogOverall_whenCalledTwice_thenUpdatesTheSameOverallResults() throws NonFatalException {
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
        assertEquals(2, holder.getShooterLogOverallResults().size());
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
    void testCalculateShooterLogOverall_whenAMatchIsLaterRescheduledOutsideTheLog_thenThrowsAndKeepsTheOverallResults()
            throws NonFatalException {
        // Arrange
        IpscMatch first = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch second = createMatch("Match B", LocalDate.of(2026, 9, 19));
        ShooterLog shooterLog = createShooterLog(first, second);
        Competitor shooter = createCompetitor("HPSC-SLO-005");
        Competitor rival = createCompetitor("HPSC-SLO-006");
        createMatchCompetitor(shooter, first, "50");
        createMatchCompetitor(rival, first, "100");
        createMatchCompetitor(shooter, second, "80");
        // shooter: (50 / 100 * 100 + 100) / 2 = 75.00, as the only competitor in the second match
        assertEquals("75.00", overallOf(ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId()),
                shooter).getPercentageInLog());
        second.setScheduledDate(LocalDate.of(2026, 10, 5).atStartOfDay());
        ipscMatchRepository.saveAndFlush(second);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId()));
        assertTrue(exception.getMessage().contains("Match with ID " + second.getId() + " is scheduled for 2026-10-05"));
        assertEquals("75.00", overallOf(ipscShooterLogOverallService.getShooterLogOverallResults(shooterLog.getId()),
                shooter).getPercentageInLog());
    }

    @Test
    void testCalculateShooterLogOverall_whenAMatchIsRescheduledWithinTheLog_thenRecalculates()
            throws NonFatalException {
        // Arrange
        IpscMatch first = createMatch("Match A", LocalDate.of(2026, 9, 12));
        IpscMatch second = createMatch("Match B", LocalDate.of(2026, 9, 19));
        ShooterLog shooterLog = createShooterLog(first, second);
        Competitor shooter = createCompetitor("HPSC-SLO-007");
        createMatchCompetitor(shooter, first, "50");
        createMatchCompetitor(createCompetitor("HPSC-SLO-008"), first, "100");
        createMatchCompetitor(shooter, second, "80");
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());
        second.setScheduledDate(LocalDate.of(2026, 9, 25).atStartOfDay());
        ipscMatchRepository.saveAndFlush(second);

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        assertEquals("75.00", overallOf(holder, shooter).getPercentageInLog());
        assertEquals(2, holder.getShooterLogOverallResults().size());
    }
    @Test
    void testCalculateShooterLogOverall_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscShooterLogOverallService.calculateShooterLogOverall(999_999L));
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasNoMatches_thenReturnsTheLogWithNoOverallResults()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createShooterLog();

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        assertEquals(shooterLog.getId(), holder.getShooterLog().getShooterLogId());
        assertTrue(holder.getShooterLogOverallResults().isEmpty());
    }

    @Test
    void testCalculateShooterLogOverall_whenLogHasMoreMatchesThanCount_thenRecordsEachCompetitorsBestMatches()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createFiveMatchLogWithTwoCompetitors();
        List<IpscMatch> matches = shooterLog.getMatches();
        Competitor shooter = competitorRepository.findAll().stream()
                .filter(competitor -> "HPSC-SLO-101".equals(competitor.getClubNumber())).findFirst().orElseThrow();
        Competitor rival = competitorRepository.findAll().stream()
                .filter(competitor -> "HPSC-SLO-102".equals(competitor.getClubNumber())).findFirst().orElseThrow();

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        // The shooter's best four are the first four matches (90, 80, 70 and 60), dropping the 50
        assertEquals(Set.of(matches.get(0).getId(), matches.get(1).getId(), matches.get(2).getId(),
                matches.get(3).getId()), Set.copyOf(overallOf(holder, shooter).getMatchIds()));
        // The rival is on 100 in every match, so the oldest match is the one dropped
        assertEquals(Set.of(matches.get(1).getId(), matches.get(2).getId(), matches.get(3).getId(),
                matches.get(4).getId()), Set.copyOf(overallOf(holder, rival).getMatchIds()));
    }

    @Test
    void testCalculateShooterLogOverall_whenCalledAgainAfterMatchesWereRecorded_thenDoesNotDuplicateThem()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createFiveMatchLogWithTwoCompetitors();
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Assert
        assertEquals(2, holder.getShooterLogOverallResults().size());
        holder.getShooterLogOverallResults().forEach(overall -> assertEquals(4, overall.getMatchIds().size()));
        assertEquals(8, countOverallMatchRows(shooterLog));
        // Fetching the matches joins one row per match, so each overall must still be returned once
        assertEquals(2, shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId()).size());
        assertEquals(2, shooterLogOverallRepository.findAllWithDetails().stream()
                .filter(overall -> shooterLog.getId().equals(overall.getShooterLog().getId())).count());
    }

    @Test
    void testCalculateShooterLogOverall_whenAnOverallIsDeleted_thenItsMatchRowsAreRemoved()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createFiveMatchLogWithTwoCompetitors();
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());
        assertEquals(8, countOverallMatchRows(shooterLog));

        // Act
        shooterLogOverallRepository.deleteAll(shooterLogOverallRepository.findAllByShooterLogId(shooterLog.getId()));
        shooterLogOverallRepository.flush();

        // Assert
        assertEquals(0, countOverallMatchRows(shooterLog));
    }

    // Without a surrounding transaction
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testCalculateShooterLogOverall_whenNoTransactionIsActive_thenCalculatesAndReadsBackWithoutAnOpenSession()
            throws NonFatalException {
        // Without this test's usual rolled-back transaction a match's club, and the matches of an overall, are lazy
        // associations that can no longer be loaded once a service call returns, so nothing may read them afterwards.
        Club club = null;
        List<IpscMatch> matches = new ArrayList<>();
        List<Competitor> competitors = new ArrayList<>();
        List<MatchCompetitor> matchCompetitors = new ArrayList<>();
        ShooterLog shooterLog = null;
        try {
            // Arrange
            club = new Club();
            club.setName("Overall Test Club");
            club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
            club = clubRepository.save(club);
            for (int index = 0; index < 5; index++) {
                IpscMatch match = new IpscMatch();
                match.setName("Match " + (index + 1));
                match.setScheduledDate(LocalDate.of(2026, 9, 1 + (index * 7)).atStartOfDay());
                match.setClub(club);
                matches.add(ipscMatchRepository.save(match));
            }
            shooterLog = createShooterLog(matches.toArray(new IpscMatch[0]));
            Competitor shooter = createCompetitor("HPSC-SLO-NT-1");
            Competitor rival = createCompetitor("HPSC-SLO-NT-2");
            competitors.add(shooter);
            competitors.add(rival);
            String[] shooterPoints = {"90", "80", "70", "60", "50"};
            for (int index = 0; index < 5; index++) {
                matchCompetitors.add(createMatchCompetitor(shooter, matches.get(index), shooterPoints[index]));
                matchCompetitors.add(createMatchCompetitor(rival, matches.get(index), "100"));
            }
            Long shooterLogId = shooterLog.getId();

            // Act
            ShooterLogOverallResponseHolder calculated = ipscShooterLogOverallService.calculateShooterLogOverall(shooterLogId);
            ShooterLogOverallResponseHolder recalculated = ipscShooterLogOverallService.calculateShooterLogOverall(shooterLogId);
            ShooterLogOverallResponseHolder read = ipscShooterLogOverallService.getShooterLogOverallResults(shooterLogId);
            List<ShooterLogOverallResponseHolder> all = ipscShooterLogOverallService.getAllShooterLogOverallResults();

            // Assert
            for (ShooterLogOverallResponseHolder holder : List.of(calculated, recalculated, read)) {
                assertEquals(2, holder.getShooterLogOverallResults().size());
                holder.getShooterLogOverallResults()
                        .forEach(overall -> assertEquals(4, overall.getMatchIds().size()));
            }
            assertTrue(all.stream().anyMatch(holder -> shooterLogId.equals(holder.getShooterLog().getShooterLogId())
                    && (holder.getShooterLogOverallResults().size() == 2)));
        } finally {
            if (shooterLog != null) {
                shooterLogOverallRepository.deleteAll(shooterLogOverallRepository.findAllByShooterLogId(shooterLog.getId()));
            }
            matches.forEach(match -> shooterResultRepository.deleteAll(
                    shooterResultRepository.findAllByMatchIdWithDetails(match.getId())));
            if (shooterLog != null) {
                shooterLogRepository.deleteById(shooterLog.getId());
            }
            matchCompetitorRepository.deleteAll(matchCompetitors);
            ipscMatchRepository.deleteAll(matches);
            competitorRepository.deleteAll(competitors);
            if (club != null) {
                clubRepository.delete(club);
            }
        }
    }

    // getAllShooterLogOverallResults()
    @Test
    void testGetAllShooterLogOverallResults_whenOverallsWereCalculated_thenIncludesTheirMatchIds()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog shooterLog = createShooterLog(match);
        createMatchCompetitor(createCompetitor("HPSC-SLO-103"), match, "80");
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Act
        List<ShooterLogOverallResponseHolder> holders = ipscShooterLogOverallService.getAllShooterLogOverallResults();

        // Assert
        ShooterLogOverallResponseHolder holder = holders.stream()
                .filter(candidate -> shooterLog.getId().equals(candidate.getShooterLog().getShooterLogId()))
                .findFirst().orElseThrow();
        assertEquals(List.of(match.getId()), holder.getShooterLogOverallResults().getFirst().getMatchIds());
    }

    @Test
    void testGetAllShooterLogOverallResults_whenSeveralLogs_thenGroupsThemByLogIncludingOnesWithNoOverallResults()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog calculated = createShooterLog(match);
        createMatchCompetitor(createCompetitor("HPSC-SLO-005"), match, "80");
        ipscShooterLogOverallService.calculateShooterLogOverall(calculated.getId());
        ShooterLog empty = createShooterLog();

        // Act
        List<ShooterLogOverallResponseHolder> holders = ipscShooterLogOverallService.getAllShooterLogOverallResults();

        // Assert
        assertTrue(holders.stream().anyMatch(holder -> calculated.getId().equals(holder.getShooterLog()
                .getShooterLogId()) && (holder.getShooterLogOverallResults().size() == 1)));
        assertTrue(holders.stream().anyMatch(holder -> empty.getId().equals(holder.getShooterLog().getShooterLogId())
                && holder.getShooterLogOverallResults().isEmpty()));
    }

    // getShooterLogOverallResults()
    @Test
    void testGetShooterLogOverallResults_whenOverallWasCalculated_thenIncludesItsMatchIds()
            throws NonFatalException {
        // Arrange
        ShooterLog shooterLog = createFiveMatchLogWithTwoCompetitors();
        ipscShooterLogOverallService.calculateShooterLogOverall(shooterLog.getId());

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.getShooterLogOverallResults(shooterLog.getId());

        // Assert
        assertEquals(2, holder.getShooterLogOverallResults().size());
        holder.getShooterLogOverallResults().forEach(overall -> assertEquals(4, overall.getMatchIds().size()));
    }

    @Test
    void testGetShooterLogOverallResults_whenShooterLogDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallService.getShooterLogOverallResults(999_999L));
    }

    @Test
    void testGetShooterLogOverallResults_whenLogHasOverallResults_thenReturnsOnlyThatLogsOverallResults()
            throws NonFatalException {
        // Arrange
        IpscMatch match = createMatch("Match A", LocalDate.of(2026, 9, 12));
        ShooterLog shooterLog = createShooterLog(match);
        ShooterLog otherLog = createShooterLog(match);
        Competitor competitor = createCompetitor("HPSC-SLO-006");
        ShooterLogOverall overall = createShooterLogOverall(shooterLog, competitor);
        createShooterLogOverall(otherLog, competitor);

        // Act
        ShooterLogOverallResponseHolder holder =
                ipscShooterLogOverallService.getShooterLogOverallResults(shooterLog.getId());

        // Assert
        assertEquals(shooterLog.getId(), holder.getShooterLog().getShooterLogId());
        assertEquals(List.of(match.getId()), holder.getShooterLog().getMatchIds());
        assertEquals(List.of(overall.getId()),
                holder.getShooterLogOverallResults().stream().map(ShooterLogOverallResponse::getShooterLogOverallId)
                        .toList());
    }

    // Helpers
    /**
     * A log of five matches, a week apart, with two competitors in each: the shooter on 90, 80, 70, 60 and 50 against
     * a rival on 100 in every match. The first competitor's club number is HPSC-SLO-101 and the rival's is
     * HPSC-SLO-102.
     */
    private ShooterLog createFiveMatchLogWithTwoCompetitors() {
        List<IpscMatch> matches = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            matches.add(createMatch("Match " + (index + 1), LocalDate.of(2026, 9, 1 + (index * 7))));
        }
        ShooterLog shooterLog = createShooterLog(matches.toArray(new IpscMatch[0]));
        Competitor shooter = createCompetitor("HPSC-SLO-101");
        Competitor rival = createCompetitor("HPSC-SLO-102");
        String[] shooterPoints = {"90", "80", "70", "60", "50"};
        for (int index = 0; index < 5; index++) {
            createMatchCompetitor(shooter, matches.get(index), shooterPoints[index]);
            createMatchCompetitor(rival, matches.get(index), "100");
        }
        return shooterLog;
    }

    private long countOverallMatchRows(ShooterLog shooterLog) {
        return ((Number) entityManager.createNativeQuery("select count(*) from shooter_log_overall_match "
                        + "where shooter_log_overall_id in "
                        + "(select id from shooter_log_overall where shooter_log_id = :shooterLogId)")
                .setParameter("shooterLogId", shooterLog.getId())
                .getSingleResult()).longValue();
    }

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
        return holder.getShooterLogOverallResults().stream()
                .filter(overall -> competitor.getId().equals(overall.getCompetitorId()))
                .findFirst()
                .orElseThrow();
    }
}
