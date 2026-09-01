package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchRequestForCSVTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenUsesUpperCamelCasePropertyNames() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchRequestForCSV request = new MatchRequestForCSV(
                LocalDate.of(2026, 4, 10), "Club Championship", "Test Club", "Pistol", "Level 1", 2);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("2026-04-10", node.get("MatchDate").asText());
        assertEquals("Club Championship", node.get("MatchName").asText());
        assertEquals("Test Club", node.get("Club").asText());
        assertEquals("Pistol", node.get("MatchFirearmType").asText());
        assertEquals("Level 1", node.get("MatchCategory").asText());
        assertEquals(2, node.get("NumberOfStages").asInt());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionalsAndZeroCount() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchRequestForCSV request = new MatchRequestForCSV(
                LocalDate.of(2026, 4, 10), "Club Championship", null, null, null, 0);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Club Championship", node.get("MatchName").asText());
        assertTrue(node.get("Club").isNull());
        assertTrue(node.get("MatchFirearmType").isNull());
        assertTrue(node.get("MatchCategory").isNull());
        assertEquals(0, node.get("NumberOfStages").asInt());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenUpperCamelCaseKeysProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "MatchDate": "2026-04-10",
                  "MatchName": "Club Championship",
                  "Club": "Test Club",
                  "MatchFirearmType": "Pistol",
                  "MatchCategory": "Level 1",
                  "NumberOfStages": 2
                }
                """;

        // Act
        MatchRequestForCSV request = mapper.readValue(json, MatchRequestForCSV.class);

        // Assert
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals("Test Club", request.getClub());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertEquals(2, request.getNumberOfStages());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "MatchDate": "2026-04-10",
                  "MatchName": "Club Championship"
                }
                """;

        // Act
        MatchRequestForCSV request = mapper.readValue(json, MatchRequestForCSV.class);

        // Assert
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertNull(request.getClub());
        assertNull(request.getMatchFirearmType());
        assertNull(request.getMatchCategory());
        assertEquals(0, request.getNumberOfStages());
    }

    @Test
    void testJsonDeserialization_whenMatchDateMissing_thenThrowsMismatchedInputException() {
        // Arrange - the @JsonCreator constructor's matchDate/matchName params are marked
        // @JsonProperty(required = true), so Jackson enforces them as required creator properties
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "MatchName": "Club Championship"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenMatchNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "MatchDate": "2026-04-10"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchRequestForCSV.class));
    }

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper.schemaFor(MatchRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,NumberOfStages
                2026-04-10,Club Championship,Test Club,Pistol,Level 1,2
                """;

        // Act
        List<MatchRequestForCSV> rows;
        try (MappingIterator<MatchRequestForCSV> it =
                     csvMapper.readerFor(MatchRequestForCSV.class).with(csvSchema).readValues(csvData)) {
            rows = it.readAll();
        }

        // Assert
        assertEquals(1, rows.size());
        MatchRequestForCSV row = rows.getFirst();
        assertEquals(LocalDate.of(2026, 4, 10), row.getMatchDate());
        assertEquals("Club Championship", row.getMatchName());
        assertEquals("Test Club", row.getClub());
        assertEquals("Pistol", row.getMatchFirearmType());
        assertEquals("Level 1", row.getMatchCategory());
        assertEquals(2, row.getNumberOfStages());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesOnlyOptionalTrailingColumns_thenLeavesThemNullOrZero() throws Exception {
        // Arrange - CsvSchema.schemaFor(...).withHeader() requires every schema column in the
        // header, but doesn't require every row to supply a value for each of them
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper.schemaFor(MatchRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,NumberOfStages
                2026-04-10,Club Championship
                """;

        // Act
        List<MatchRequestForCSV> rows;
        try (MappingIterator<MatchRequestForCSV> it =
                     csvMapper.readerFor(MatchRequestForCSV.class).with(csvSchema).readValues(csvData)) {
            rows = it.readAll();
        }

        // Assert
        assertEquals(1, rows.size());
        assertEquals(LocalDate.of(2026, 4, 10), rows.getFirst().getMatchDate());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
        assertNull(rows.getFirst().getClub());
        assertNull(rows.getFirst().getMatchFirearmType());
        assertNull(rows.getFirst().getMatchCategory());
        assertEquals(0, rows.getFirst().getNumberOfStages());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange - a row missing MatchName entirely (not just blank) still trips the required
        // creator property check, same as a missing JSON key
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper.schemaFor(MatchRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,NumberOfStages
                2026-04-10
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> {
            try (MappingIterator<MatchRequestForCSV> it =
                         csvMapper.readerFor(MatchRequestForCSV.class).with(csvSchema).readValues(csvData)) {
                it.readAll();
            }
        });
    }
}
