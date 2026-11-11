package za.co.hpsc.web.models.ipsc.shooterresult.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.ShooterResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShooterResultResponseTest {

    // ShooterResultResponse(ShooterResult)
    @Test
    void testConstructor_whenPercentageInLogIsSet_thenFormatsItForDisplay() {
        // Arrange
        ShooterResult shooterResult = shooterResult();
        shooterResult.setPercentageInLog(new BigDecimal("93.5"));

        // Act
        ShooterResultResponse response = new ShooterResultResponse(shooterResult);

        // Assert
        assertEquals("93.50", response.getPercentageInLog());
    }

    @Test
    void testConstructor_whenPercentageInLogIsNotCalculatedYet_thenLeavesItNull() {
        // Arrange
        ShooterResult shooterResult = shooterResult();
        shooterResult.setPercentageInLog(null);

        // Act
        ShooterResultResponse response = new ShooterResultResponse(shooterResult);

        // Assert
        assertNull(response.getPercentageInLog());
    }

    // Helpers
    private ShooterResult shooterResult() {
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        Competitor competitor = new Competitor();
        competitor.setId(2L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(3L);

        ShooterResult shooterResult = new ShooterResult();
        shooterResult.setId(4L);
        shooterResult.setMatch(match);
        shooterResult.setCompetitor(competitor);
        shooterResult.setMatchCompetitor(matchCompetitor);
        return shooterResult;
    }
}
