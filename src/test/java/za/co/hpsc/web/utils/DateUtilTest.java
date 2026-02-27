package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    // =====================================================================
    // Tests for formatDate - Valid Data Processing
    // =====================================================================

    @Test
    void testFormatDate_whenValidDateAndFormat_thenReturnsFormattedString() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 5);
        String format = "yyyy-MM-dd";
        String expected = "2023-10-05";

        // Act
        String result = DateUtil.formatDate(date, format);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for formatDate - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testFormatDate_whenNullDate_thenReturnsEmptyString() {
        // Arrange
        String format = "yyyy-MM-dd";

        // Act
        String result = DateUtil.formatDate(null, format);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testFormatDate_whenNullFormat_thenReturnsEmptyString() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 5);

        // Act
        String result = DateUtil.formatDate(date, null);

        // Assert
        assertEquals("", result);
    }

    // =====================================================================
    // Tests for formatDateTime - Valid Data Processing
    // =====================================================================

    @Test
    void testFormatDateTime_whenValidDateTimeAndFormat_thenReturnsFormattedString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0);
        String format = "yyyy-MM-dd HH:mm:ss";
        String expected = "2023-10-05 14:30:00";

        // Act
        String result = DateUtil.formatDateTime(dateTime, format);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for formatDateTime - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testFormatDateTime_whenNullDateTime_thenReturnsEmptyString() {
        // Arrange
        String format = "yyyy-MM-dd HH:mm:ss";

        // Act
        String result = DateUtil.formatDateTime(null, format);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testFormatDateTime_whenNullFormat_thenReturnsEmptyString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0);

        // Act
        String result = DateUtil.formatDateTime(dateTime, null);

        // Assert
        assertEquals("", result);
    }
}
