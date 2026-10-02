package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring-context integration test for {@link ShooterLogOverallRepository}'s {@code existsByCompetitorId} query — the
 * check behind the reject-not-cascade competitor delete — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterLogOverallRepositoryIntegrationTest {

    @Autowired
    private ShooterLogOverallRepository shooterLogOverallRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByCompetitorId()
    @Test
    void testExistsByCompetitorId_whenCompetitorHasAnOverallRow_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        persistOverall(competitor);

        // Act & Assert
        assertTrue(shooterLogOverallRepository.existsByCompetitorId(competitor.getId()));
    }

    @Test
    void testExistsByCompetitorId_whenOnlyAnotherCompetitorHasAnOverallRow_thenReturnsFalse() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        persistOverall(ScoringFixtures.competitor(entityManager, "John"));

        // Act & Assert
        assertFalse(shooterLogOverallRepository.existsByCompetitorId(competitor.getId()));
    }

    private void persistOverall(Competitor competitor) {
        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setShooterLog(ScoringFixtures.shooterLog(entityManager));
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.NONE);
        overall.setDivision(Division.OPEN);
        entityManager.persist(overall);
    }
}
