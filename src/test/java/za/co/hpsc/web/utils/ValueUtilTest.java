package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueUtilTest {

    // =====================================================================
    // Tests for nullAsEmptyString (String) - Valid Data Processing
    // =====================================================================

    @Test
    void testNullAsEmptyString_whenNonNullString_thenReturnsSameString() {
        // Arrange
        String input = "Hello, World!";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_whenEmptyString_thenReturnsSameString() {
        // Arrange
        String input = "";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_whenWhitespace_thenReturnsSameString() {
        // Arrange
        String input = "   ";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_whenSpecialCharacterString_thenReturnsSameString() {
        // Arrange
        String input = "@#$%^&*()";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    // =====================================================================
    // Tests for nullAsEmptyString (String) - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testNullAsEmptyString_whenNullString_thenReturnsEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString(null);

        // Assert
        assertEquals("", result);
    }

    // =====================================================================
    // Tests for nullAsEmptyString (Object) - Valid Data Processing
    // =====================================================================

    @Test
    void testNullAsEmptyString_whenNonNullObject_thenReturnsToStringValue() {
        // Arrange
        Object value = 123;
        String expected = "123";

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testNullAsEmptyString_whenCustomObject_thenReturnsToStringValue() {
        // Arrange
        Object value = new Object() {
            @Override
            public String toString() {
                return "CustomObject";
            }
        };
        String expected = "CustomObject";

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for nullAsEmptyString (Object) - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testNullAsEmptyString_whenNullObject_thenReturnsEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString((Object) null);

        // Assert
        assertEquals("", result);
    }

    // =====================================================================
    // Tests for nullAsRandomUuid - Valid Data Processing
    // =====================================================================

    @Test
    void testNullAsRandomUuid_whenNonNullUuid_thenReturnsSameUuid() {
        // Arrange
        UUID input = UUID.randomUUID();

        // Act
        UUID result = ValueUtil.nullAsRandomUuid(input);

        // Assert
        assertEquals(input, result);
    }

    // =====================================================================
    // Tests for nullAsRandomUuid - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testNullAsRandomUuid_whenNullValue_thenReturnsRandomUuid() {
        // Act
        UUID result = ValueUtil.nullAsRandomUuid(null);

        // Assert
        assertNotNull(result);
    }

    // =====================================================================
    // Tests for nullAsEmptyList - Valid Data Processing
    // =====================================================================

    @Test
    void testNullAsEmptyList_whenNonNullList_thenReturnsSameList() {
        // Arrange
        List<String> input = List.of("a", "b", "c");

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_whenEmptyList_thenReturnsSameList() {
        // Arrange
        List<String> input = new ArrayList<>();

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_whenModifiableList_thenReturnsSameList() {
        // Arrange
        List<String> input = new ArrayList<>(List.of("x", "y", "z"));

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
        assertEquals(List.of("x", "y", "z"), input);
    }

    // =====================================================================
    // Tests for nullAsEmptyList - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testNullAsEmptyList_whenNullList_thenReturnsEmptyList() {
        // Act
        List<String> result = ValueUtil.nullAsEmptyList(null);

        // Assert
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testNullAsZero_whenIntegerIsNonNull_thenReturnsSameValue() {
        // Arrange
        Integer input = 42;

        // Act
        int result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(42, result);
    }

    @Test
    void testNullAsZero_whenIntegerIsNull_thenReturnsZero() {
        // Act
        int result = ValueUtil.nullAsZero((Integer) null);

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testNullAsZero_whenLongIsNonNull_thenReturnsSameValue() {
        // Arrange
        Long input = 987654321L;

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(987654321L, result);
    }

    @Test
    void testNullAsZero_whenLongIsNull_thenReturnsZero() {
        // Act
        long result = ValueUtil.nullAsZero((Long) null);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenValidDecimalString_thenReturnsBigDecimalValue() {
        // Arrange
        String input = "123.45";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(new BigDecimal("123.45"), result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenScientificNotationString_thenReturnsBigDecimalValue() {
        // Arrange
        String input = "1E+3";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(new BigDecimal("1E+3"), result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenNullString_thenReturnsZero() {
        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(null);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenInvalidString_thenReturnsZero() {
        // Arrange
        String input = "abc123";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenBlankString_thenReturnsZero() {
        // Arrange
        String input = "   ";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }
}
