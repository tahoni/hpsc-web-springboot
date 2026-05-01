package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IpscMatchTest {

    // =====================================================================
    // init(MatchDto)
    // =====================================================================

    @Test
    void testInit_whenDtoIsFullyPopulated_thenMapsAllFields() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 4, 24, 10, 30);
        LocalDateTime dateEdited = LocalDateTime.of(2026, 4, 20, 8, 0);
        dto.setName("Spring Classic");
        dto.setScheduledDate(scheduledDate);
        dto.setMatchFirearmType(FirearmType.HANDGUN);
        dto.setMatchCategory(MatchCategory.CLUB_SHOOT);
        dto.setDateEdited(dateEdited);

        // Act
        match.init(dto);

        // Assert
        assertEquals("Spring Classic", match.getName());
        assertEquals(scheduledDate, match.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, match.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, match.getMatchCategory());
        assertEquals(dateEdited, match.getDateEdited());
    }

    @Test
    void testInit_whenDtoIsNull_thenFieldsAreUnchanged() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Existing Match");
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 3, 1, 9, 0);
        match.setScheduledDate(scheduledDate);
        match.setMatchFirearmType(FirearmType.RIFLE);
        match.setMatchCategory(MatchCategory.LEAGUE);

        // Act
        match.init((MatchDto) null);

        // Assert
        assertEquals("Existing Match", match.getName());
        assertEquals(scheduledDate, match.getScheduledDate());
        assertEquals(FirearmType.RIFLE, match.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, match.getMatchCategory());
    }

    @Test
    void testInit_whenScheduledDateIsNull_thenScheduledDateDefaultsToCurrentTime() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        dto.setName("Date-less Match");
        dto.setScheduledDate(null);

        LocalDateTime before = LocalDateTime.now();

        // Act
        match.init(dto);

        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(match.getScheduledDate());
        assertFalse(match.getScheduledDate().isBefore(before));
        assertFalse(match.getScheduledDate().isAfter(after));
    }

    @Test
    void testInit_whenScheduledDateIsProvided_thenUsesProvidedScheduledDate() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        LocalDateTime scheduledDate = LocalDateTime.of(2027, 1, 15, 14, 0);
        dto.setName("Future Match");
        dto.setScheduledDate(scheduledDate);

        // Act
        match.init(dto);

        // Assert
        assertEquals(scheduledDate, match.getScheduledDate());
    }

    @Test
    void testInit_whenFirearmTypeIsNull_thenFirearmTypeIsNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        dto.setName("No Firearm Type Match");
        dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        dto.setMatchFirearmType(null);

        // Act
        match.init(dto);

        // Assert
        assertNull(match.getMatchFirearmType());
    }

    @Test
    void testInit_whenMatchCategoryIsNull_thenMatchCategoryIsNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        dto.setName("No Category Match");
        dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        dto.setMatchCategory(null);

        // Act
        match.init(dto);

        // Assert
        assertNull(match.getMatchCategory());
    }

    @Test
    void testInit_whenDateEditedIsNull_thenDateEditedIsNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        MatchDto dto = new MatchDto();
        dto.setName("Match");
        dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        dto.setDateEdited(null);

        // Act
        match.init(dto);

        // Assert
        assertNull(match.getDateEdited());
    }

    @Test
    void testInit_whenCalledTwice_thenLastDtoValuesOverwrite() {
        // Arrange
        IpscMatch match = new IpscMatch();

        MatchDto first = new MatchDto();
        first.setName("First Match");
        first.setScheduledDate(LocalDateTime.of(2026, 1, 10, 9, 0));
        first.setMatchFirearmType(FirearmType.HANDGUN);
        first.setMatchCategory(MatchCategory.CLUB_SHOOT);

        MatchDto second = new MatchDto();
        second.setName("Second Match");
        second.setScheduledDate(LocalDateTime.of(2026, 6, 20, 12, 0));
        second.setMatchFirearmType(FirearmType.PCC);
        second.setMatchCategory(MatchCategory.LEAGUE);

        // Act
        match.init(first);
        match.init(second);

        // Assert
        assertEquals("Second Match", match.getName());
        assertEquals(LocalDateTime.of(2026, 6, 20, 12, 0), match.getScheduledDate());
        assertEquals(FirearmType.PCC, match.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, match.getMatchCategory());
    }

    @Test
    void testInit_whenAllFirearmTypesProvided_thenEachFirearmTypeIsSetCorrectly() {
        // Arrange & Act & Assert
        for (FirearmType firearmType : FirearmType.values()) {
            IpscMatch match = new IpscMatch();
            MatchDto dto = new MatchDto();
            dto.setName("Match");
            dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
            dto.setMatchFirearmType(firearmType);

            match.init(dto);

            assertEquals(firearmType, match.getMatchFirearmType());
        }
    }

    @Test
    void testInit_whenAllMatchCategoriesProvided_thenEachMatchCategoryIsSetCorrectly() {
        // Arrange & Act & Assert
        for (MatchCategory category : MatchCategory.values()) {
            IpscMatch match = new IpscMatch();
            MatchDto dto = new MatchDto();
            dto.setName("Match");
            dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
            dto.setMatchCategory(category);

            match.init(dto);

            assertEquals(category, match.getMatchCategory());
        }
    }

    // =====================================================================
    // toString()
    // =====================================================================

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
    void testToString_whenCalledAfterInit_thenReflectsUpdatedNameAndDate() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Old Match");
        match.setScheduledDate(LocalDateTime.of(2025, 1, 1, 8, 0));

        MatchDto dto = new MatchDto();
        dto.setName("Updated Match");
        dto.setScheduledDate(LocalDateTime.of(2026, 6, 15, 9, 0));
        match.init(dto);

        // Act
        String result = match.toString();

        // Assert
        assertEquals("Updated Match (2026-06-15 09:00)", result);
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
    void testInit_whenDtoNameIsNull_thenNameBecomesNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setName("Existing Name");
        MatchDto dto = new MatchDto();
        dto.setName(null);
        dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));

        // Act
        match.init(dto);

        // Assert
        assertNull(match.getName());
        assertEquals(LocalDateTime.of(2026, 5, 1, 9, 0), match.getScheduledDate());
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
