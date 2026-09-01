package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorRequestForCSVTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenUsesUpperCamelCasePropertyNames() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompetitorRequestForCSV request = new CompetitorRequestForCSV(
                "Jane", "Doe", "Ann", "Janie", LocalDate.of(1990, 1, 1), "Female", "Test Club",
                12345, "C-1", "HPSC-001", "9001015800083", "0821234567", "jane.doe@example.com");

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Jane", node.get("FirstName").asText());
        assertEquals("Doe", node.get("LastName").asText());
        assertEquals("Ann", node.get("MiddleNames").asText());
        assertEquals("Janie", node.get("Nickname").asText());
        assertEquals("1990-01-01", node.get("DateOfBirth").asText());
        assertEquals("Female", node.get("Gender").asText());
        assertEquals("Test Club", node.get("HomeClub").asText());
        assertEquals(12345, node.get("SapsaNumber").asInt());
        assertEquals("C-1", node.get("CompetitorNumber").asText());
        assertEquals("HPSC-001", node.get("ClubNumber").asText());
        assertEquals("9001015800083", node.get("IdNumber").asText());
        assertEquals("0821234567", node.get("CellphoneNumber").asText());
        assertEquals("jane.doe@example.com", node.get("EmailAddress").asText());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompetitorRequestForCSV request = new CompetitorRequestForCSV(
                "Jane", "Doe", null, null, null, null, null, null, null, null, null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Jane", node.get("FirstName").asText());
        assertEquals("Doe", node.get("LastName").asText());
        assertTrue(node.get("MiddleNames").isNull());
        assertTrue(node.get("DateOfBirth").isNull());
        assertTrue(node.get("ClubNumber").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenUpperCamelCaseKeysProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "FirstName": "Jane",
                  "LastName": "Doe",
                  "MiddleNames": "Ann",
                  "Nickname": "Janie",
                  "DateOfBirth": "1990-01-01",
                  "Gender": "Female",
                  "HomeClub": "Test Club",
                  "SapsaNumber": 12345,
                  "CompetitorNumber": "C-1",
                  "ClubNumber": "HPSC-001",
                  "IdNumber": "9001015800083",
                  "CellphoneNumber": "0821234567",
                  "EmailAddress": "jane.doe@example.com"
                }
                """;

        // Act
        CompetitorRequestForCSV request = mapper.readValue(json, CompetitorRequestForCSV.class);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Ann", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), request.getDateOfBirth());
        assertEquals("Female", request.getGender());
        assertEquals("Test Club", request.getHomeClub());
        assertEquals(12345, request.getSapsaNumber());
        assertEquals("C-1", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals("jane.doe@example.com", request.getEmailAddress());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "FirstName": "Jane",
                  "LastName": "Doe"
                }
                """;

        // Act
        CompetitorRequestForCSV request = mapper.readValue(json, CompetitorRequestForCSV.class);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertNull(request.getClubNumber());
        assertNull(request.getGender());
        assertNull(request.getHomeClub());
    }

    @Test
    void testJsonDeserialization_whenFirstNameMissing_thenLeavesFieldNull() throws Exception {
        // Arrange - no @JsonCreator constructor backs this class, so @JsonProperty(required = true)
        // on firstName/lastName isn't enforced by plain property-based deserialization
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "LastName": "Doe"
                }
                """;

        // Act
        CompetitorRequestForCSV request = mapper.readValue(json, CompetitorRequestForCSV.class);

        // Assert
        assertNull(request.getFirstName());
        assertEquals("Doe", request.getLastName());
    }

    @Test
    void testJsonDeserialization_whenLastNameMissing_thenLeavesFieldNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "FirstName": "Jane"
                }
                """;

        // Act
        CompetitorRequestForCSV request = mapper.readValue(json, CompetitorRequestForCSV.class);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertNull(request.getLastName());
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenReturnsInstanceWithAllFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act
        CompetitorRequestForCSV request = mapper.readValue("{}", CompetitorRequestForCSV.class);

        // Assert
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
        assertNull(request.getClubNumber());
    }

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper.schemaFor(CompetitorRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddress
                Jane,Doe,Ann,Janie,1990-01-01,Female,Test Club,12345,C-1,HPSC-001,9001015800083,0821234567,jane.doe@example.com
                """;

        // Act
        List<CompetitorRequestForCSV> rows;
        try (MappingIterator<CompetitorRequestForCSV> it =
                     csvMapper.readerFor(CompetitorRequestForCSV.class).with(csvSchema).readValues(csvData)) {
            rows = it.readAll();
        }

        // Assert
        assertEquals(1, rows.size());
        CompetitorRequestForCSV row = rows.getFirst();
        assertEquals("Jane", row.getFirstName());
        assertEquals("Doe", row.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), row.getDateOfBirth());
        assertEquals("HPSC-001", row.getClubNumber());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesTrailingColumns_thenLeavesMissingFieldsNull() throws Exception {
        // Arrange - CsvSchema.schemaFor(...).withHeader() requires every schema column in the
        // header, but doesn't require every row to supply a value for each of them
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(CompetitorRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddress
                Jane
                """;

        // Act
        List<CompetitorRequestForCSV> rows;
        try (MappingIterator<CompetitorRequestForCSV> it =
                     csvMapper.readerFor(CompetitorRequestForCSV.class).with(csvSchema).readValues(csvData)) {
            rows = it.readAll();
        }

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertNull(rows.getFirst().getLastName());
    }
}
