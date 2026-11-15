package za.co.hpsc.web.models.ipsc.matchresult.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatchResultResponseTest {

    // MatchResultResponse(MatchResult)
    @Test
    void testConstructor_whenPercentageInLogIsSet_thenFormatsItForDisplay() {
        // Arrange
        MatchResult matchResult = matchResult();
        matchResult.setPercentageInLog(new BigDecimal("93.5"));

        // Act
        MatchResultResponse response = new MatchResultResponse(matchResult);

        // Assert
        assertEquals("93.50", response.getPercentageInLog());
    }

    @Test
    void testConstructor_whenPercentageInLogIsNotCalculatedYet_thenLeavesItNull() {
        // Arrange
        MatchResult matchResult = matchResult();
        matchResult.setPercentageInLog(null);

        // Act
        MatchResultResponse response = new MatchResultResponse(matchResult);

        // Assert
        assertNull(response.getPercentageInLog());
    }

    // Helpers
    private MatchResult matchResult() {
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        Competitor competitor = new Competitor();
        competitor.setId(2L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(3L);

        MatchResult matchResult = new MatchResult();
        matchResult.setId(4L);
        matchResult.setMatch(match);
        matchResult.setCompetitor(competitor);
        matchResult.setMatchCompetitor(matchCompetitor);
        return matchResult;
    }
}
