package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.SystemConstants;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorRequestCsvMixInTest {

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        String csvData = """
                FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                Jane,Doe,Ann,Janie,1990-01-01,Female,Test Club,12345,C-1,HPSC-001,9001015800083,0821234567,jane.doe@example.com;jane2.doe@example.com,true,false
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        CompetitorRequest row = rows.getFirst();
        assertNull(row.getCompetitorId());
        assertEquals("Jane", row.getFirstName());
        assertEquals("Doe", row.getLastName());
        assertEquals("Ann", row.getMiddleNames());
        assertEquals("Janie", row.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), row.getDateOfBirth());
        assertEquals("Female", row.getGender());
        assertEquals("Test Club", row.getHomeClub());
        assertEquals(12345, row.getSapsaNumber());
        assertEquals("C-1", row.getCompetitorNumber());
        assertEquals("HPSC-001", row.getClubNumber());
        assertEquals("9001015800083", row.getIdNumber());
        assertEquals("0821234567", row.getCellphoneNumber());
        assertEquals(Boolean.TRUE, row.getPaidUpSapsa());
        assertEquals(Boolean.FALSE, row.getPaidUpClub());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), row.getEmailAddresses());
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsOptionalColumns_thenLeavesThemNullAndEmailsEmpty() throws Exception {
        // Arrange
        String csvData = """
                FirstName,LastName,NickName
                Jane,Doe,Janie
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertEquals("Janie", rows.getFirst().getNickName());
        assertNull(rows.getFirst().getMiddleNames());
        assertNull(rows.getFirst().getDateOfBirth());
        assertTrue(rows.getFirst().getEmailAddresses().isEmpty());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesOnlyOptionalTrailingColumns_thenLeavesThemNull() throws Exception {
        // Arrange - a row doesn't have to supply a value for every column in the header
        String csvData = """
                FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                Jane,Doe
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertEquals("Doe", rows.getFirst().getLastName());
        assertNull(rows.getFirst().getMiddleNames());
        assertTrue(rows.getFirst().getEmailAddresses().isEmpty());
    }

    @Test
    void testCsvDeserialization_whenColumnsAreReordered_thenMapsByHeaderName() throws Exception {
        // Arrange
        String csvData = """
                ClubNumber,LastName,FirstName
                HPSC-001,Doe,Jane
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertEquals("Doe", rows.getFirst().getLastName());
        assertEquals("HPSC-001", rows.getFirst().getClubNumber());
    }

    @Test
    void testCsvDeserialization_whenCsvHasUnknownColumn_thenIgnoresIt() throws Exception {
        // Arrange
        String csvData = """
                FirstName,LastName,Colour
                Jane,Doe,Blue
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
    }

    @Test
    void testCsvDeserialization_whenCsvHasCompetitorIdColumn_thenBindsIt() throws Exception {
        // Arrange
        String csvData = """
                CompetitorId,FirstName,LastName
                7,Jane,Doe
                """;

        // Act
        List<CompetitorRequest> rows = readRows(csvData);

        // Assert
        assertEquals(7L, rows.getFirst().getCompetitorId());
    }

    @Test
    void testCsvDeserialization_whenRowIsRaggedAndMissesRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange - a row missing LastName entirely (not just blank) still trips the required
        // creator property check
        String csvData = """
                FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                Jane
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    @Test
    void testCsvDeserialization_whenHeaderOmitsRequiredColumn_thenThrowsMismatchedInputException() {
        // Arrange
        String csvData = """
                FirstName,NickName
                Jane,Janie
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> readRows(csvData));
    }

    // Helpers
    private List<CompetitorRequest> readRows(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        csvMapper.addMixIn(CompetitorRequest.class, CompetitorRequestCsvMixIn.class);
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();

        try (MappingIterator<CompetitorRequest> it =
                     csvMapper.readerFor(CompetitorRequest.class).with(csvSchema).readValues(csvData)) {
            return it.readAll();
        }
    }
}
