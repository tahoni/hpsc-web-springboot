package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogCompetitor;
import za.co.hpsc.web.enums.CompetitorCategory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link ShooterLogCompetitorRepository}'s {@code existsByCompetitorId}/{@code existsByMatchCompetitorMatchId} queries — the checks behind the
 * reject-not-cascade competitor/match deletes — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterLogCompetitorRepositoryIntegrationTest {

    @Autowired
    private ShooterLogCompetitorRepository shooterLogCompetitorRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByCompetitorId()
    @Test
    void testExistsByCompetitorId_whenCompetitorIsInAShooterLog_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager, competitor, match);
        ScoringFixtures.shooterLogCompetitor(entityManager, ScoringFixtures.shooterLog(entityManager), matchCompetitor);

        // Act & Assert
        assertTrue(shooterLogCompetitorRepository.existsByCompetitorId(competitor.getId()));
    }

    @Test
    void testExistsByCompetitorId_whenOnlyAnotherCompetitorIsInAShooterLog_thenReturnsFalse() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        Competitor other = ScoringFixtures.competitor(entityManager, "John");
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        MatchCompetitor otherEntry = ScoringFixtures.matchCompetitor(entityManager, other, match);
        ScoringFixtures.shooterLogCompetitor(entityManager, ScoringFixtures.shooterLog(entityManager), otherEntry);

        // Act & Assert
        assertFalse(shooterLogCompetitorRepository.existsByCompetitorId(competitor.getId()));
    }

    // existsByMatchCompetitorMatchId()
    @Test
    void testExistsByMatchCompetitorMatchId_whenMatchIsInAShooterLog_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager, competitor, match);
        ScoringFixtures.shooterLogCompetitor(entityManager,
                ScoringFixtures.shooterLog(entityManager), matchCompetitor);

        // Act & Assert
        assertTrue(shooterLogCompetitorRepository.existsByMatchCompetitorMatchId(match.getId()));
    }

    @Test
    void testExistsByMatchCompetitorMatchId_whenMatchIsInNoShooterLog_thenReturnsFalse() {
        // Arrange
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        ScoringFixtures.matchCompetitor(entityManager, ScoringFixtures.competitor(entityManager, "Jane"), match);

        // Act & Assert
        assertFalse(shooterLogCompetitorRepository.existsByMatchCompetitorMatchId(match.getId()));
    }

    // findAllByShooterLogId()
    @Test
    void testFindAllByShooterLogId_whenSeveralCategories_thenReturnsThemAllFromTheDatabase() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager, competitor, match);
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        ShooterLogCompetitor entry = ScoringFixtures.shooterLogCompetitor(entityManager, shooterLog, matchCompetitor);
        entry.setCompetitorCategories(List.of(CompetitorCategory.JUNIOR, CompetitorCategory.LADY));
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ShooterLogCompetitor> result = shooterLogCompetitorRepository.findAllByShooterLogId(shooterLog.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(List.of(CompetitorCategory.JUNIOR, CompetitorCategory.LADY),
                result.getFirst().getCompetitorCategories());
    }
}
