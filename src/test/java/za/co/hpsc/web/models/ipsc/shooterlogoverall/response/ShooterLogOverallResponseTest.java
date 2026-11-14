package za.co.hpsc.web.models.ipsc.shooterlogoverall.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShooterLogOverallResponseTest {

    // ShooterLogOverallResponse(ShooterLogOverall)
    @Test
    void testConstructor_whenOverallHasMatches_thenListsTheirIdsInOrder() {
        // Arrange
        ShooterLogOverall overall = overall();
        overall.setMatches(List.of(match(12L), match(10L), match(11L)));

        // Act
        ShooterLogOverallResponse response = new ShooterLogOverallResponse(overall);

        // Assert
        assertEquals(List.of(12L, 10L, 11L), response.getMatchIds());
    }

    @Test
    void testConstructor_whenOverallHasNoMatches_thenMatchIdsIsEmpty() {
        // Arrange
        ShooterLogOverall overall = overall();
        overall.setMatches(new ArrayList<>());

        // Act
        ShooterLogOverallResponse response = new ShooterLogOverallResponse(overall);

        // Assert
        assertTrue(response.getMatchIds().isEmpty());
    }

    @Test
    void testConstructor_whenOverallsMatchesWereNotSet_thenMatchIdsIsEmpty() {
        // Arrange
        ShooterLogOverall overall = overall();
        overall.setMatches(null);

        // Act
        ShooterLogOverallResponse response = new ShooterLogOverallResponse(overall);

        // Assert
        assertTrue(response.getMatchIds().isEmpty());
    }

    @Test
    void testConstructor_whenOverallHasMatches_thenOtherFieldsAreStillMapped() {
        // Arrange
        ShooterLogOverall overall = overall();
        overall.setMatches(List.of(match(10L)));

        // Act
        ShooterLogOverallResponse response = new ShooterLogOverallResponse(overall);

        // Assert
        assertEquals(50L, response.getShooterLogOverallId());
        assertEquals(1L, response.getShooterLogId());
        assertEquals(5L, response.getCompetitorId());
        assertEquals(Division.OPEN, response.getDivision());
    }

    // Helpers
    private IpscMatch match(Long id) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        return match;
    }

    private ShooterLogOverall overall() {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setId(1L);
        Competitor competitor = new Competitor();
        competitor.setId(5L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");

        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setId(50L);
        overall.setShooterLog(shooterLog);
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.NONE);
        overall.setDivision(Division.OPEN);
        return overall;
    }
}
