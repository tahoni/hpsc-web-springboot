package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link ShooterLogRepository}'s {@code existsByCompetitorId} query — the checks behind the
 * reject-not-cascade competitor/match deletes — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterLogRepositoryIntegrationTest {

    @Autowired
    private ShooterLogRepository shooterLogRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByCompetitorId()
    @Test
    void testExistsByCompetitorId_whenCompetitorHasAShooterLog_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        ScoringFixtures.shooterLog(entityManager, competitor, ScoringFixtures.club(entityManager));

        // Act & Assert
        assertTrue(shooterLogRepository.existsByCompetitorId(competitor.getId()));
    }

    @Test
    void testExistsByCompetitorId_whenOnlyAnotherCompetitorHasAShooterLog_thenReturnsFalse() {
        // Arrange
        Club club = ScoringFixtures.club(entityManager);
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        ScoringFixtures.shooterLog(entityManager, ScoringFixtures.competitor(entityManager, "John"), club);

        // Act & Assert
        assertFalse(shooterLogRepository.existsByCompetitorId(competitor.getId()));
    }
}
