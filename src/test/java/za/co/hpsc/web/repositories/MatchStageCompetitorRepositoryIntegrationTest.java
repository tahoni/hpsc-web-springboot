package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link MatchStageCompetitorRepository}'s {@code existsByMatchStageMatchId} query — the checks behind the
 * reject-not-cascade competitor/match deletes — against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MatchStageCompetitorRepositoryIntegrationTest {

    @Autowired
    private MatchStageCompetitorRepository matchStageCompetitorRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByMatchStageMatchId()
    @Test
    void testExistsByMatchStageMatchId_whenAStageOfTheMatchHasAResult_thenReturnsTrue() {
        // Arrange
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager,
                ScoringFixtures.competitor(entityManager, "Jane"), match);
        ScoringFixtures.matchStageCompetitor(entityManager, matchCompetitor);

        // Act & Assert
        assertTrue(matchStageCompetitorRepository.existsByMatchStageMatchId(match.getId()));
    }

    @Test
    void testExistsByMatchStageMatchId_whenOnlyAnotherMatchHasStageResults_thenReturnsFalse() {
        // Arrange
        IpscMatch match = ScoringFixtures.matchWithStage(entityManager, "Match");
        IpscMatch other = ScoringFixtures.matchWithStage(entityManager, "Other Match");
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager,
                ScoringFixtures.competitor(entityManager, "Jane"), other);
        ScoringFixtures.matchStageCompetitor(entityManager, matchCompetitor);

        // Act & Assert
        assertFalse(matchStageCompetitorRepository.existsByMatchStageMatchId(match.getId()));
    }
}
