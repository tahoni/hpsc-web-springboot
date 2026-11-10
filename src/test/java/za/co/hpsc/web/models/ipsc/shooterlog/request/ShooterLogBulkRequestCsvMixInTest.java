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

class ShooterLogBulkRequestCsvMixInTest {

    // CSV deserialization
    @Test
    void testCsvDeserialization_whenValidRow_thenMapsAllFields() throws Exception {
        // Act
        List<ShooterLogBulkRequest> rows =
                readRows("ShooterLogId,StartDate,EndDate,MatchIds\n4,2026-09-01,2026-09-30,10;11\n");

        // Assert
        assertEquals(1, rows.size());
        ShooterLogBulkRequest row = rows.getFirst();
        assertEquals(4L, row.getShooterLogId());
        assertEquals(LocalDate.of(2026, 9, 1), row.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), row.getEndDate());
        assertEquals(List.of(10L, 11L), row.getMatchIds());
    }

    @Test
    void testCsvDeserialization_whenColumnsAreEmptyOrOmitted_thenLeavesThemNullOrEmpty() throws Exception {
        // Act
        ShooterLogBulkRequest empty = readRows("ShooterLogId,StartDate,EndDate,MatchIds\n,,,\n").getFirst();
        ShooterLogBulkRequest omitted = readRows("ShooterLogId\n4\n").getFirst();

        // Assert
        assertNull(empty.getShooterLogId());
        assertNull(empty.getStartDate());
        assertNull(empty.getEndDate());
        assertTrue(empty.getMatchIds().isEmpty());
        assertEquals(4L, omitted.getShooterLogId());
        assertNull(omitted.getStartDate());
        assertTrue(omitted.getMatchIds().isEmpty());
    }

    @Test
    void testCsvDeserialization_whenUnknownColumnsAndAnyColumnOrder_thenIgnoresUnknownAndMapsByName() throws Exception {
        // Act
        ShooterLogBulkRequest row =
                readRows("Notes,MatchIds,EndDate,StartDate,ShooterLogId\nhello,5,2026-09-30,2026-09-01,8\n").getFirst();

        // Assert
        assertEquals(8L, row.getShooterLogId());
        assertEquals(LocalDate.of(2026, 9, 1), row.getStartDate());
        assertEquals(List.of(5L), row.getMatchIds());
    }

    @Test
    void testCsvDeserialization_whenADateIsNotInIsoFormat_thenFails() {
        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> readRows("ShooterLogId,StartDate\n4,01/09/2026\n"));
    }

    // Helpers
    private List<ShooterLogBulkRequest> readRows(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();
        csvMapper.addMixIn(ShooterLogBulkRequest.class, ShooterLogBulkRequestCsvMixIn.class);
        try (MappingIterator<ShooterLogBulkRequest> iterator =
                     csvMapper.readerFor(ShooterLogBulkRequest.class).with(csvSchema).readValues(csvData)) {
            return iterator.readAll();
        }
    }
}
