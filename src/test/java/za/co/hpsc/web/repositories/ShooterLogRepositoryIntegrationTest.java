package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring-context integration test for {@link ShooterLogRepository}'s {@code existsByMatchesId} query — the check
 * behind the reject-not-cascade match delete — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterLogRepositoryIntegrationTest {

    @Autowired
    private ShooterLogRepository shooterLogRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByMatchesId()
    @Test
    void testExistsByMatchesId_whenMatchIsLinkedToAShooterLog_thenReturnsTrue() {
        // Arrange
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        shooterLog.setMatches(List.of(match));
        entityManager.flush();

        // Act & Assert
        assertTrue(shooterLogRepository.existsByMatchesId(match.getId()));
    }

    @Test
    void testExistsByMatchesId_whenMatchIsInNoShooterLog_thenReturnsFalse() {
        // Arrange
        IpscMatch match = ScoringFixtures.match(entityManager, "Match");
        ScoringFixtures.shooterLog(entityManager);

        // Act & Assert
        assertFalse(shooterLogRepository.existsByMatchesId(match.getId()));
    }
}
