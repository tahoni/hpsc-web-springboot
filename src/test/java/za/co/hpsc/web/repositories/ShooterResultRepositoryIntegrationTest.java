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
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring-context integration test for {@link ShooterResultRepository}'s {@code findMatchIdsWithoutDateCalculated}
 * query, against the H2 {@code test} profile database.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterResultRepositoryIntegrationTest {

    @Autowired
    private ShooterResultRepository shooterResultRepository;

    @Autowired
    private EntityManager entityManager;

    // findMatchIdsWithoutDateCalculated()
    @Test
    void testFindMatchIdsWithoutDateCalculated_whenOnlySomeMatchesHaveAnUncalculatedResult_thenReturnsThoseOnce() {
        // Arrange
        IpscMatch uncalculated = ScoringFixtures.match(entityManager, "Match A");
        IpscMatch calculated = ScoringFixtures.match(entityManager, "Match B");
        persistResult(uncalculated, "Jane", null);
        persistResult(uncalculated, "John", null);
        persistResult(calculated, "Jill", LocalDateTime.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Long> found = shooterResultRepository.findMatchIdsWithoutDateCalculated();

        // Assert
        assertTrue(found.contains(uncalculated.getId()));
        assertFalse(found.contains(calculated.getId()));
        assertEquals(1, found.stream().filter(uncalculated.getId()::equals).count());
    }

    private void persistResult(IpscMatch match, String firstName, LocalDateTime dateCalculated) {
        Competitor competitor = ScoringFixtures.competitor(entityManager, firstName);
        MatchCompetitor matchCompetitor = ScoringFixtures.matchCompetitor(entityManager, competitor, match);
        ShooterResult result = new ShooterResult();
        result.setMatch(match);
        result.setCompetitor(competitor);
        result.setMatchCompetitor(matchCompetitor);
        result.setCompetitorCategory(CompetitorCategory.NONE);
        result.setDivision(Division.OPEN);
        result.setDateCalculated(dateCalculated);
        entityManager.persist(result);
    }
}
