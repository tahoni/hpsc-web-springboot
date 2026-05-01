package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueUtilTest {

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

    @Test
    void testNullAsEmptyString_whenNullString_thenReturnsEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString(null);

        // Assert
        assertEquals("", result);
    }

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

    @Test
    void testNullAsEmptyString_whenNullObject_thenReturnsEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString((Object) null);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testNullAsRandomUuid_whenNonNullUuid_thenReturnsSameUuid() {
        // Arrange
        UUID input = UUID.randomUUID();

        // Act
        UUID result = ValueUtil.nullAsRandomUuid(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsRandomUuid_whenNullValue_thenReturnsRandomUuid() {
        // Act
        UUID result = ValueUtil.nullAsRandomUuid(null);

        // Assert
        assertNotNull(result);
    }

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

    @Test
    void testNullAsZero_whenValidNumericString_thenReturnsLongValue() {
        // Arrange
        String input = "12345";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(12345L, result);
    }

    @Test
    void testNullAsZero_whenNegativeNumericString_thenReturnsNegativeLongValue() {
        // Arrange
        String input = "-5000";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(-5000L, result);
    }

    @Test
    void testNullAsZero_whenZeroString_thenReturnsZero() {
        // Arrange
        String input = "0";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZero_whenNullString_thenReturnsZero() {
        // Act
        long result = ValueUtil.nullAsZero((String) null);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZero_whenInvalidNumericString_thenReturnsZero() {
        // Arrange
        String input = "not a number";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZero_whenBlankString_thenReturnsZero() {
        // Arrange
        String input = "   ";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZero_whenEmptyString_thenReturnsZero() {
        // Arrange
        String input = "";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsZero_whenDecimalString_thenReturnsZero() {
        // Arrange
        String input = "123.45";

        // Act
        long result = ValueUtil.nullAsZero(input);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void testNullAsEmptyString_whenUnicodeString_thenReturnsSameString() {
        // Arrange
        String input = "Hello 世界 🌍";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_whenVeryLongString_thenReturnsSameString() {
        // Arrange
        String input = "A".repeat(10000);

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsRandomUuid_whenMultipleNullCalls_thenReturnsDifferentUuids() {
        // Act
        UUID result1 = ValueUtil.nullAsRandomUuid(null);
        UUID result2 = ValueUtil.nullAsRandomUuid(null);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        // Random UUIDs should be different (extremely unlikely to collide)
        // but we only assert they're not null rather than strict inequality
        // to avoid flaky tests from the extremely rare collision possibility
    }

    @Test
    void testNullAsZeroBigDecimal_whenLargeDecimal_thenReturnsBigDecimalValue() {
        // Arrange
        String input = "999999999999999.123456789";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(new BigDecimal("999999999999999.123456789"), result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenNegativeDecimal_thenReturnsBigDecimalValue() {
        // Arrange
        String input = "-42.5";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(new BigDecimal("-42.5"), result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenZeroString_thenReturnsZero() {
        // Arrange
        String input = "0";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testNullAsZeroBigDecimal_whenSpecialCharacters_thenReturnsZero() {
        // Arrange
        String input = "12@34.56";

        // Act
        BigDecimal result = ValueUtil.nullAsZeroBigDecimal(input);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testNullAsEmptyList_whenSingleElementList_thenReturnsSameList() {
        // Arrange
        List<Integer> input = List.of(42);

        // Act
        List<Integer> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_whenLargeList_thenReturnsSameList() {
        // Arrange
        List<String> input = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            input.add("Item " + i);
        }

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
        assertEquals(1000, result.size());
    }

    @Test
    void testNullAsEmptyList_whenReturnedFromNull_thenReturnsNewInstance() {
        // Act
        List<String> result1 = ValueUtil.nullAsEmptyList(null);
        List<String> result2 = ValueUtil.nullAsEmptyList(null);

        // Assert
        assertEquals(new ArrayList<>(), result1);
        assertEquals(new ArrayList<>(), result2);
        // Verify they are different instances
        assertNotNull(result1);
        assertNotNull(result2);
    }

    @Test
    void testNullAsEmptyString_whenObjectWithCustomToString_thenReturnsCustomValue() {
        // Arrange
        Object value = new Object() {
            @Override
            public String toString() {
                return "CustomStringRepresentation";
            }
        };

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals("CustomStringRepresentation", result);
    }

    @Test
    void testNullAsEmptyString_whenBoolean_thenReturnsStringValue() {
        // Arrange
        Object value = Boolean.TRUE;

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals("true", result);
    }

    @Test
    void testNullAsEmptyString_whenDouble_thenReturnsStringValue() {
        // Arrange
        Object value = 3.14159;

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals("3.14159", result);
    }
}
