package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchRequestCsvMixInTest {

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        String csvData = """
                MatchDate,MatchName,StartTime,EndTime,Club,MatchFirearmType,MatchCategory,Url
                2026-04-10,Club Championship,08:00,17:00,Test Club,Pistol,Level 1,https://example.com/matches/1
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        MatchRequest row = rows.getFirst();
        assertNull(row.getMatchId());
        assertEquals(LocalDate.of(2026, 4, 10), row.getMatchDate());
        assertEquals("Club Championship", row.getMatchName());
        assertEquals(LocalTime.of(8, 0), row.getStartTime());
        assertEquals(LocalTime.of(17, 0), row.getEndTime());
        assertEquals("Test Club", row.getClub());
        assertEquals("Pistol", row.getMatchFirearmType());
        assertEquals("Level 1", row.getMatchCategory());
        assertEquals("https://example.com/matches/1", row.getUrl());
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsOptionalColumns_thenLeavesThemNull() throws Exception {
        // Arrange
        String csvData = """
                MatchDate,MatchName,MatchFirearmType,MatchCategory
                2026-04-10,Club Championship,Pistol,Level 1
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals(LocalDate.of(2026, 4, 10), rows.getFirst().getMatchDate());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
        assertEquals("Pistol", rows.getFirst().getMatchFirearmType());
        assertEquals("Level 1", rows.getFirst().getMatchCategory());
        assertNull(rows.getFirst().getStartTime());
        assertNull(rows.getFirst().getEndTime());
        assertNull(rows.getFirst().getClub());
        assertNull(rows.getFirst().getUrl());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesOnlyOptionalTrailingColumns_thenLeavesThemNull() throws Exception {
        // Arrange - a row doesn't have to supply a value for every column in the header
        String csvData = """
                MatchDate,MatchName,MatchFirearmType,MatchCategory,Club,StartTime,EndTime,Url
                2026-04-10,Club Championship,Pistol,Level 1
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
        assertNull(rows.getFirst().getClub());
        assertNull(rows.getFirst().getStartTime());
        assertNull(rows.getFirst().getUrl());
    }

    @Test
    void testCsvDeserialization_whenColumnsAreReordered_thenMapsByHeaderName() throws Exception {
        // Arrange
        String csvData = """
                Url,MatchCategory,MatchName,Club,MatchFirearmType,MatchDate
                https://example.com/matches/1,Level 1,Club Championship,Test Club,Pistol,2026-04-10
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals(LocalDate.of(2026, 4, 10), rows.getFirst().getMatchDate());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
        assertEquals("Test Club", rows.getFirst().getClub());
        assertEquals("Pistol", rows.getFirst().getMatchFirearmType());
        assertEquals("Level 1", rows.getFirst().getMatchCategory());
        assertEquals("https://example.com/matches/1", rows.getFirst().getUrl());
    }

    @Test
    void testCsvDeserialization_whenCsvHasUnknownColumn_thenIgnoresIt() throws Exception {
        // Arrange
        String csvData = """
                MatchDate,MatchName,MatchFirearmType,MatchCategory,Colour
                2026-04-10,Club Championship,Pistol,Level 1,Blue
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
    }

    @Test
    void testCsvDeserialization_whenCsvHasMatchIdColumn_thenBindsIt() throws Exception {
        // Arrange
        String csvData = """
                MatchId,MatchDate,MatchName,MatchFirearmType,MatchCategory
                7,2026-04-10,Club Championship,Pistol,Level 1
                """;

        // Act
        List<MatchRequest> rows = readRows(csvData);

        // Assert
        assertEquals(7L, rows.getFirst().getMatchId());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange - a row missing MatchName entirely (not just blank) still trips the required
        // creator property check
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-04-10
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange
        String csvData = """
                MatchDate,MatchFirearmType,MatchCategory
                2026-04-10,Pistol,Level 1
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsMatchFirearmType_thenThrowsMismatchedInputException() {
        // Arrange
        String csvData = """
                MatchDate,MatchName,MatchCategory
                2026-04-10,Club Championship,Level 1
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsMatchCategory_thenThrowsMismatchedInputException() {
        // Arrange
        String csvData = """
                MatchDate,MatchName,MatchFirearmType
                2026-04-10,Club Championship,Pistol
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    // Helpers
    private List<MatchRequest> readRows(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        csvMapper.addMixIn(MatchRequest.class, MatchRequestCsvMixIn.class);
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

        try (MappingIterator<MatchRequest> it =
                     csvMapper.readerFor(MatchRequest.class).with(csvSchema).readValues(csvData)) {
            return it.readAll();
        }
    }
}
