package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    @Test
    void testFormatDate_WithValidDateAndFormat_thenReturnsFormattedString() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 5);
        String format = "yyyy-MM-dd";
        String expected = "2023-10-05";

        // Act
        String result = DateUtil.formatDate(date, format);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatDate_WithNullDate_thenReturnsEmptyString() {
        // Arrange
        LocalDate date = null;
        String format = "yyyy-MM-dd";
        String expected = "";

        // Act
        String result = DateUtil.formatDate(date, format);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatDate_WithNullFormat_thenReturnsEmptyString() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 10, 5);
        String format = null;
        String expected = "";

        // Act
        String result = DateUtil.formatDate(date, format);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatDateTime_WithValidDateAndFormat_thenReturnsFormattedString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0);
        String format = "yyyy-MM-dd HH:mm:ss";
        String expected = "2023-10-05 14:30:00";

        // Act
        String result = DateUtil.formatDateTime(dateTime, format);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatDateTime_WithNullDate_thenReturnsEmptyString() {
        // Arrange
        LocalDateTime dateTime = null;
        String format = "yyyy-MM-dd HH:mm:ss";
        String expected = "";

        // Act
        String result = DateUtil.formatDateTime(dateTime, format);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatDateTime_WithNullFormat_thenReturnsEmptyString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 5, 14, 30, 0);
        String format = null;
        String expected = "";

        // Act
        String result = DateUtil.formatDateTime(dateTime, format);

        // Assert
        assertEquals(expected, result);
    }
}
