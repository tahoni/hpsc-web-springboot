package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IpscMatchTest {

    // toString()
    @Test
    void testToString_whenNameAndScheduledDateAreSet_thenFormatsNameAndDate() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Spring Classic");
        match.setScheduledDate(LocalDateTime.of(2026, 4, 24, 10, 30));

        // Act
        String result = match.toString();

        // Assert
        assertEquals("Spring Classic (2026-04-24 10:30)", result);
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Consistent Match");
        match.setScheduledDate(LocalDateTime.of(2026, 4, 24, 10, 30));

        // Act
        String result1 = match.toString();
        String result2 = match.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_whenNameIsSet_thenContainsMatchNameInOutput() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("HPSC Club Match");
        match.setScheduledDate(LocalDateTime.of(2026, 4, 24, 10, 30));

        // Act
        String result = match.toString();

        // Assert
        assertTrue(result.contains("HPSC Club Match"));
    }

    @Test
    void testToString_whenScheduledDateIsSet_thenContainsFormattedDateInOutput() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Match");
        match.setScheduledDate(LocalDateTime.of(2026, 12, 31, 23, 59));

        // Act
        String result = match.toString();

        // Assert
        assertTrue(result.contains("2026-12-31 23:59"));
    }

    @Test
    void testToString_whenNameIsNull_thenReturnsScheduledDateOnly() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName(null);
        match.setScheduledDate(LocalDateTime.of(2026, 4, 24, 10, 30));

        // Act
        String result = match.toString();

        // Assert
        assertEquals("(2026-04-24 10:30)", result);
    }

    @Test
    void testToString_whenScheduledDateIsNull_thenScheduledDateIsCurrentValue() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Null Date Match");
        match.setScheduledDate(null);

        // Act & Assert
        assertNotNull(match.toString());
        assertFalse(match.toString().contains("null"));
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenInitializesCreatedAndUpdatedDatesToSameCurrentValue() {
        // Arrange
        IpscMatch match = new IpscMatch();
        LocalDateTime before = LocalDateTime.now();

        // Act
        match.onInsert();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(match.getDateCreated());
        assertNotNull(match.getDateUpdated());
        assertEquals(match.getDateCreated(), match.getDateUpdated());
        assertFalse(match.getDateCreated().isBefore(before));
        assertFalse(match.getDateCreated().isAfter(after));
    }

    // onUpdate()
    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedAndKeepsDateCreatedUnchanged() {
        // Arrange
        IpscMatch match = new IpscMatch();
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 8, 0);
        match.setDateCreated(created);
        match.setDateUpdated(LocalDateTime.of(2026, 1, 1, 8, 0));
        LocalDateTime before = LocalDateTime.now();

        // Act
        match.onUpdate();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertEquals(created, match.getDateCreated());
        assertNotNull(match.getDateUpdated());
        assertFalse(match.getDateUpdated().isBefore(before));
        assertFalse(match.getDateUpdated().isAfter(after));
    }
}
