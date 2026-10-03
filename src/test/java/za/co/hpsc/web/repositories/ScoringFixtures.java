package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogCompetitor;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists the minimal valid scoring and shooter-log records the repository integration tests
 * need, so each test only states what it's actually checking. A plain helper rather than a Spring
 * bean, so it never takes part in component scanning.
 */
final class ScoringFixtures {

    private ScoringFixtures() {
    }

    static Club club(EntityManager entityManager) {
        Club club = new Club();
        club.setName("Test Club");
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        entityManager.persist(club);
        return club;
    }

    static Competitor competitor(EntityManager entityManager, String firstName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName("Doe");
        entityManager.persist(competitor);
        return competitor;
    }

    static IpscMatch match(EntityManager entityManager, String name) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        entityManager.persist(match);
        return match;
    }

    static MatchCompetitor matchCompetitor(EntityManager entityManager, Competitor competitor, IpscMatch match) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.NONE);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        entityManager.persist(matchCompetitor);
        return matchCompetitor;
    }

    static ShooterLog shooterLog(EntityManager entityManager) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setStartDate(LocalDate.of(2026, 9, 1));
        shooterLog.setEndDate(LocalDate.of(2026, 9, 30));
        entityManager.persist(shooterLog);
        return shooterLog;
    }

    static ShooterLogCompetitor shooterLogCompetitor(EntityManager entityManager, ShooterLog shooterLog,
                                                     MatchCompetitor matchCompetitor) {
        ShooterLogCompetitor shooterLogCompetitor = new ShooterLogCompetitor();
        shooterLogCompetitor.setShooterLog(shooterLog);
        shooterLogCompetitor.setCompetitor(matchCompetitor.getCompetitor());
        shooterLogCompetitor.setMatchCompetitor(matchCompetitor);
        shooterLogCompetitor.setCompetitorCategory(matchCompetitor.getCompetitorCategory());
        shooterLogCompetitor.setDivision(matchCompetitor.getDivision());
        entityManager.persist(shooterLogCompetitor);
        return shooterLogCompetitor;
    }
}
