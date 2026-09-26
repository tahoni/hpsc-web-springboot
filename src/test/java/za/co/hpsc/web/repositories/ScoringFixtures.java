package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogCompetitor;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;

import java.time.LocalDate;

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

    static IpscMatch matchWithStage(EntityManager entityManager, String name) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        IpscMatchStage stage = new IpscMatchStage();
        stage.setMatch(match);
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");
        match.getStages().add(stage);
        entityManager.persist(match);
        return match;
    }

    static MatchCompetitor matchCompetitor(EntityManager entityManager, Competitor competitor, IpscMatch match) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        entityManager.persist(matchCompetitor);
        return matchCompetitor;
    }

    static MatchStageCompetitor matchStageCompetitor(EntityManager entityManager, MatchCompetitor matchCompetitor) {
        MatchStageCompetitor matchStageCompetitor = new MatchStageCompetitor();
        matchStageCompetitor.setMatchCompetitor(matchCompetitor);
        matchStageCompetitor.setMatchStage(matchCompetitor.getMatch().getStages().getFirst());
        entityManager.persist(matchStageCompetitor);
        return matchStageCompetitor;
    }

    static ShooterLog shooterLog(EntityManager entityManager, Competitor competitor, Club club) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setCompetitor(competitor);
        shooterLog.setClub(club);
        shooterLog.setFirearmType(FirearmType.HANDGUN);
        shooterLog.setPowerFactor(PowerFactor.MINOR);
        shooterLog.setCalculatedDate(LocalDate.of(2026, 9, 30).atStartOfDay());
        entityManager.persist(shooterLog);
        return shooterLog;
    }

    static ShooterLogCompetitor shooterLogCompetitor(EntityManager entityManager, ShooterLog shooterLog,
                                                     MatchCompetitor matchCompetitor) {
        ShooterLogCompetitor shooterLogCompetitor = new ShooterLogCompetitor();
        shooterLogCompetitor.setShooterLog(shooterLog);
        shooterLogCompetitor.setMatchCompetitor(matchCompetitor);
        shooterLogCompetitor.setMatch(matchCompetitor.getMatch());
        entityManager.persist(shooterLogCompetitor);
        return shooterLogCompetitor;
    }
}
