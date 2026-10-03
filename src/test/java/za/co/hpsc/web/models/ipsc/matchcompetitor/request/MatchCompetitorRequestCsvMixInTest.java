package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.SystemConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorRequestCsvMixInTest {

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        String csvData = """
                MatchCompetitorId,CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF,Pts,OverallRanking,ClubRanking,IsVisitor
                7,1,2,HPSC,Junior,Handgun,Open Division,Major,95.5,2,1,false
                """;

        // Act
        List<MatchCompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        MatchCompetitorRequest row = rows.getFirst();
        assertEquals(7L, row.getMatchCompetitorId());
        assertEquals(1L, row.getCompetitorId());
        assertEquals(2L, row.getMatchId());
        assertEquals("HPSC", row.getMatchClub());
        assertEquals("Junior", row.getCompetitorCategory());
        assertEquals("Handgun", row.getFirearmType());
        assertEquals("Open Division", row.getDivision());
        assertEquals("Major", row.getPowerFactor());
        assertEquals(0, new BigDecimal("95.5").compareTo(row.getPoints()));
        assertEquals(0, new BigDecimal("2").compareTo(row.getOverallRanking()));
        assertEquals(0, new BigDecimal("1").compareTo(row.getClubRanking()));
        assertEquals(Boolean.FALSE, row.getIsVisitor());
    }

    @Test
    void testCsvDeserialization_whenNameAndMemberNumberColumnsGiven_thenMapsThem() throws Exception {
        // Arrange
        String csvData = """
                Name,Mem #,MatchId,Cats,Div
                Jane Doe,A123,2,Junior,Open Division
                """;

        // Act
        MatchCompetitorRequest row = readRows(csvData).getFirst();

        // Assert
        assertNull(row.getCompetitorId());
        assertEquals("Jane Doe", row.getCompetitorName());
        assertEquals("A123", row.getCompetitorNumber());
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsOptionalColumns_thenLeavesThemNull() throws Exception {
        // Arrange
        String csvData = """
                CompetitorId,MatchId,Cats,Div
                1,2,Junior,Open Division
                """;

        // Act
        List<MatchCompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        MatchCompetitorRequest row = rows.getFirst();
        assertEquals(1L, row.getCompetitorId());
        assertEquals(2L, row.getMatchId());
        assertEquals("Junior", row.getCompetitorCategory());
        assertEquals("Open Division", row.getDivision());
        assertNull(row.getMatchCompetitorId());
        assertNull(row.getMatchClub());
        assertNull(row.getFirearmType());
        assertNull(row.getPowerFactor());
        assertNull(row.getPoints());
        assertNull(row.getOverallRanking());
        assertNull(row.getClubRanking());
        assertNull(row.getIsVisitor());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesOnlyOptionalTrailingColumns_thenLeavesThemNull() throws Exception {
        // Arrange - a row doesn't have to supply a value for every column in the header
        String csvData = """
                CompetitorId,MatchId,Cats,Div,Class,FirearmType,PF,Pts
                1,2,Junior,Open Division
                """;

        // Act
        List<MatchCompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Open Division", rows.getFirst().getDivision());
        assertNull(rows.getFirst().getMatchClub());
        assertNull(rows.getFirst().getFirearmType());
        assertNull(rows.getFirst().getPoints());
    }

    @Test
    void testCsvDeserialization_whenColumnsAreReordered_thenMapsByHeaderName() throws Exception {
        // Arrange
        String csvData = """
                Div,Cats,MatchId,CompetitorId,FirearmType
                Open Division,Junior,2,1,Handgun
                """;

        // Act
        List<MatchCompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals(1L, rows.getFirst().getCompetitorId());
        assertEquals(2L, rows.getFirst().getMatchId());
        assertEquals("Junior", rows.getFirst().getCompetitorCategory());
        assertEquals("Open Division", rows.getFirst().getDivision());
        assertEquals("Handgun", rows.getFirst().getFirearmType());
    }

    @Test
    void testCsvDeserialization_whenCsvHasUnknownColumn_thenIgnoresIt() throws Exception {
        // Arrange
        String csvData = """
                CompetitorId,MatchId,Cats,Div,Colour
                1,2,Junior,Open Division,Blue
                """;

        // Act
        List<MatchCompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals(1L, rows.getFirst().getCompetitorId());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange - a row missing Division entirely (not just blank) still trips the required
        // creator property check
        String csvData = """
                CompetitorId,MatchId,Cats,Div
                1,2,Junior
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange
        String csvData = """
                CompetitorId,Cats,Div
                1,Junior,Open Division
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    // Helpers
    private List<MatchCompetitorRequest> readRows(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.addMixIn(MatchCompetitorRequest.class, MatchCompetitorRequestCsvMixIn.class);
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();

        try (MappingIterator<MatchCompetitorRequest> it =
                     csvMapper.readerFor(MatchCompetitorRequest.class).with(csvSchema).readValues(csvData)) {
            return it.readAll();
        }
    }
}
