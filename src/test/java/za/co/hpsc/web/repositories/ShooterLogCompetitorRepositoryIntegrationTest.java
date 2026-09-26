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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link ShooterLogCompetitorRepository}'s {@code existsByMatchId} query — the checks behind the
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

    // existsByMatchId()
    @Test
    void testExistsByMatchId_whenMatchIsInAShooterLog_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager, competitor, match);
        ScoringFixtures.shooterLogCompetitor(entityManager,
                ScoringFixtures.shooterLog(entityManager, competitor, ScoringFixtures.club(entityManager)), matchCompetitor);

        // Act & Assert
        assertTrue(shooterLogCompetitorRepository.existsByMatchId(match.getId()));
    }

    @Test
    void testExistsByMatchId_whenMatchIsInNoShooterLog_thenReturnsFalse() {
        // Arrange
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");
        ScoringFixtures.matchCompetitor(entityManager, ScoringFixtures.competitor(entityManager, "Jane"), match);

        // Act & Assert
        assertFalse(shooterLogCompetitorRepository.existsByMatchId(match.getId()));
    }
}
