package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link MatchCompetitorRepository}'s {@code existsByCompetitorId}/{@code existsByMatchId} queries — the checks behind the
 * reject-not-cascade competitor/match deletes — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MatchCompetitorRepositoryIntegrationTest {

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByCompetitorId()
    @Test
    void testExistsByCompetitorId_whenCompetitorHasAResult_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        ScoringFixtures.matchCompetitor(entityManager, competitor, ScoringFixtures.matchWithStage(entityManager, "Match"));

        // Act & Assert
        assertTrue(matchCompetitorRepository.existsByCompetitorId(competitor.getId()));
    }

    @Test
    void testExistsByCompetitorId_whenOnlyAnotherCompetitorHasAResult_thenReturnsFalse() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        Competitor other = ScoringFixtures.competitor(entityManager, "John");
        ScoringFixtures.matchCompetitor(entityManager, other, ScoringFixtures.matchWithStage(entityManager, "Match"));

        // Act & Assert
        assertFalse(matchCompetitorRepository.existsByCompetitorId(competitor.getId()));
    }

    // existsByMatchId()
    @Test
    void testExistsByMatchId_whenMatchHasAResult_thenReturnsTrue() {
        // Arrange
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");
        ScoringFixtures.matchCompetitor(entityManager, ScoringFixtures.competitor(entityManager, "Jane"), match);

        // Act & Assert
        assertTrue(matchCompetitorRepository.existsByMatchId(match.getId()));
    }

    @Test
    void testExistsByMatchId_whenMatchHasNoResults_thenReturnsFalse() {
        // Arrange
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");

        // Act & Assert
        assertFalse(matchCompetitorRepository.existsByMatchId(match.getId()));
    }
}
