package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueUtilTest {

    @Test
    void testNullAsEmptyString_withNullValue_thenReturnsEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString(null);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testNullAsEmptyString_withNonNullValue_thenReturnsSameString() {
        // Arrange
        String input = "Hello, World!";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_withEmptyString_thenReturnsSameString() {
        // Arrange
        String input = "";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_withWhitespace_thenReturnsSameString() {
        // Arrange
        String input = "   ";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyString_withSpecialCharacterString_thenReturnsSameString() {
        // Arrange
        String input = "@#$%^&*()";

        // Act
        String result = ValueUtil.nullAsEmptyString(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsRandomUuid_withNullValue_thenReturnsRandomUuid() {
        // Act
        UUID result = ValueUtil.nullAsRandomUuid(null);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testNullAsRandomUuid_withNonNullUuid_thenReturnsSameUuid() {
        // Arrange
        UUID input = UUID.randomUUID();

        // Act
        UUID result = ValueUtil.nullAsRandomUuid(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_withNullList_thenReturnsEmptyList() {
        // Act
        List<String> result = ValueUtil.nullAsEmptyList(null);

        // Assert
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testNullAsEmptyList_withNonNullList_thenReturnsSameList() {
        // Arrange
        List<String> input = List.of("a", "b", "c");

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_withEmptyList_thenReturnsSameList() {
        // Arrange
        List<String> input = new ArrayList<>();

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testNullAsEmptyList_withModifiableList_thenReturnsSameList() {
        // Arrange
        List<String> input = new ArrayList<>(List.of("x", "y", "z"));

        // Act
        List<String> result = ValueUtil.nullAsEmptyList(input);

        // Assert
        assertEquals(input, result);
        assertEquals(List.of("x", "y", "z"), input);
    }

    @Test
    void testNullAsEmptyString_withNonNullObject_ShouldReturnToStringValue() {
        // Arrange
        Object value = 123;
        String expected = "123";

        // Act
        String result = ValueUtil.nullAsEmptyString(value);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testNullAsEmptyString_withNullObject_ShouldReturnEmptyString() {
        // Act
        String result = ValueUtil.nullAsEmptyString(null);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testNullAsEmptyString_withCustomObject_ShouldReturnToStringValue() {
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
}
