package za.co.hpsc.web.mappers;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorRow;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchCompetitorRowMapperTest {
    private final MatchCompetitorRowMapper mapper = new MatchCompetitorRowMapper();

    // toRow()
    @Test
    void testToRow_whenNoMatchCompetitor_thenDescribesTheRequestAlone() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        request.setDivision("Open");
        request.setPoints(new BigDecimal("10"));

        // Act
        MatchCompetitorRow row = mapper.toRow(request, null);

        // Assert
        assertEquals("7001", row.getCompetitorNumber());
        assertEquals("Open", row.getDivision());
        assertEquals("10", row.getPoints());
        assertEquals("", row.getMatchId());
    }

    @Test
    void testToRow_whenNoRequestAndNoMatchCompetitor_thenEveryValueIsEmpty() {
        // Act
        MatchCompetitorRow row = mapper.toRow(null, null);

        // Assert
        assertEquals("", row.getCompetitorId());
        assertEquals("", row.getPoints());
    }

    @Test
    void testToRow_whenMatchCompetitorHasResolvedValues_thenTheyReplaceTheRequestsText() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        request.setCompetitorName("Jane Doe");
        request.setDivision("open division");
        request.setPoints(new BigDecimal("10"));
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber(7001);
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setMatchClub(ClubIdentifier.HPSC);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setFirearmType(Division.OPEN.getFirearmType());
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);

        // Act
        MatchCompetitorRow row = mapper.toRow(request, matchCompetitor);

        // Assert
        assertEquals("1", row.getCompetitorId());
        assertEquals("Jane Doe", row.getCompetitorName());
        assertEquals("7001", row.getCompetitorNumber());
        assertEquals("2", row.getMatchId());
        assertEquals(ClubIdentifier.HPSC.getName(), row.getMatchClub());
        assertEquals(Division.OPEN.getName(), row.getDivision());
        assertEquals(PowerFactor.MAJOR.getName(), row.getPowerFactor());
        assertEquals("10", row.getPoints());
    }

    @Test
    void testToRow_whenCompetitorResolved_thenCompetitorNameIsTheNormalisedName() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("1 - John   Smith (RO)");
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(new Competitor());

        // Act
        MatchCompetitorRow row = mapper.toRow(request, matchCompetitor);

        // Assert
        assertEquals("John Smith", row.getCompetitorName());
    }

    @Test
    void testToRow_whenCompetitorResolvedAndNameKeepsHyphens_thenOnlyThePositionPrefixIsRemoved() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("2 - Jane Smith-Jones");
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(new Competitor());

        // Act
        MatchCompetitorRow row = mapper.toRow(request, matchCompetitor);

        // Assert
        assertEquals("Jane Smith-Jones", row.getCompetitorName());
    }

    @Test
    void testToRow_whenCompetitorResolvedAndRequestHasNoName_thenCompetitorNameIsEmpty() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(new Competitor());

        // Act
        MatchCompetitorRow row = mapper.toRow(new MatchCompetitorRequest(), matchCompetitor);

        // Assert
        assertEquals("", row.getCompetitorName());
    }

    @Test
    void testToRow_whenCompetitorNotResolved_thenCompetitorNameIsTheNormalisedName() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("1 - John   Smith (RO)");

        // Act
        MatchCompetitorRow row = mapper.toRow(request, new MatchCompetitor());

        // Assert
        assertEquals("John Smith", row.getCompetitorName());
    }

    @Test
    void testToRow_whenNoMatchCompetitor_thenCompetitorNameIsTheNormalisedName() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("1 - John   Smith (RO)");

        // Act
        MatchCompetitorRow row = mapper.toRow(request, null);

        // Assert
        assertEquals("John Smith", row.getCompetitorName());
    }

    @Test
    void testToRow_whenMatchCompetitorLacksAValue_thenKeepsTheRequestsText() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setDivision("Not a division");
        request.setMatchId(2L);

        // Act
        MatchCompetitorRow row = mapper.toRow(request, new MatchCompetitor());

        // Assert
        assertEquals("Not a division", row.getDivision());
        assertEquals("2", row.getMatchId());
    }
}
