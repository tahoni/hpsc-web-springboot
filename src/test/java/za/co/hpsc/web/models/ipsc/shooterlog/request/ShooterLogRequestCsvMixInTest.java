package za.co.hpsc.web.models.ipsc.shooterlog.request;

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

class ShooterLogRequestCsvMixInTest {

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Arrange
        String csvData = """
                StartDate,EndDate,MatchIds
                2026-09-01,2026-09-30,10;11;12
                """;

        // Act
        List<ShooterLogRequest> rows = readRows(csvData);

        // Assert
        assertEquals(1, rows.size());
        ShooterLogRequest row = rows.getFirst();
        assertEquals(LocalDate.of(2026, 9, 1), row.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), row.getEndDate());
        assertEquals(List.of(10L, 11L, 12L), row.getMatchIds());
    }

    @Test
    void testCsvDeserialization_whenRowHasASingleMatchId_thenMapsIt() throws Exception {
        // Act
        List<ShooterLogRequest> rows = readRows("StartDate,EndDate,MatchIds\n2026-09-01,2026-09-30,7\n");

        // Assert
        assertEquals(List.of(7L), rows.getFirst().getMatchIds());
    }

    @Test
    void testCsvDeserialization_whenMatchIdsAreEmptyOrOmitted_thenLeavesThemEmpty() throws Exception {
        // Act
        List<ShooterLogRequest> empty = readRows("StartDate,EndDate,MatchIds\n2026-09-01,2026-09-30,\n");
        List<ShooterLogRequest> omitted = readRows("StartDate,EndDate\n2026-09-01,2026-09-30\n");

        // Assert
        assertTrue(empty.getFirst().getMatchIds().isEmpty());
        assertTrue(omitted.getFirst().getMatchIds().isEmpty());
    }

    @Test
    void testCsvDeserialization_whenADateIsEmptyOrOmitted_thenLeavesItNull() throws Exception {
        // Act
        List<ShooterLogRequest> emptyStart = readRows("StartDate,EndDate,MatchIds\n,2026-09-30,1\n");
        List<ShooterLogRequest> omittedEnd = readRows("StartDate,MatchIds\n2026-09-01,1\n");

        // Assert
        assertNull(emptyStart.getFirst().getStartDate());
        assertNull(omittedEnd.getFirst().getEndDate());
    }

    @Test
    void testCsvDeserialization_whenUnknownColumnsAndColumnOrderDiffers_thenIgnoresUnknownAndMapsByName()
            throws Exception {
        // Act
        List<ShooterLogRequest> rows = readRows("Notes,MatchIds,EndDate,StartDate\nhello,5,2026-09-30,2026-09-01\n");

        // Assert
        ShooterLogRequest row = rows.getFirst();
        assertEquals(LocalDate.of(2026, 9, 1), row.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), row.getEndDate());
        assertEquals(List.of(5L), row.getMatchIds());
    }

    @Test
    void testCsvDeserialization_whenADateIsNotInIsoFormat_thenFails() {
        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> readRows("StartDate,EndDate,MatchIds\n01/09/2026,2026-09-30,1\n"));
    }

    // Helpers
    private List<ShooterLogRequest> readRows(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();
        csvMapper.addMixIn(ShooterLogRequest.class, ShooterLogRequestCsvMixIn.class);
        try (MappingIterator<ShooterLogRequest> iterator =
                     csvMapper.readerFor(ShooterLogRequest.class).with(csvSchema).readValues(csvData)) {
            return iterator.readAll();
        }
    }
}
