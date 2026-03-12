package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {

    // =====================================================================
    // Tests for formatStringWithNamedParameters - Valid Data Processing
    // =====================================================================

    @Test
    void testFormatStringWithNamedParameters_whenValidTemplateAndParameters_thenReplacesPlaceholders() {
        // Arrange
        String template = standardTemplate();
        Map<String, String> parameters = mapOf("name", "Alice", "place", "Wonderland");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Welcome to Wonderland.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenDuplicatePlaceholders_thenReplacesAllOccurrences() {
        // Arrange
        String template = "Hello, ${name}! ${name}, you are amazing!";
        Map<String, String> parameters = mapOf("name", "Alice");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Alice, you are amazing!", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenAdjacentPlaceholders_thenReplacesCorrectly() {
        // Arrange
        String template = "Welcome, ${firstName}${lastName}!";
        Map<String, String> parameters = mapOf("firstName", "John", "lastName", "Doe");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Welcome, JohnDoe!", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenMissingKeysInParameters_thenLeavesPlaceholdersUnchanged() {
        // Arrange
        String template = standardTemplate();
        Map<String, String> parameters = mapOf("name", "Alice");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Welcome to ${place}.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNonExistentPlaceholder_thenLeavesItUnchanged() {
        // Arrange
        String template = "Hello, ${missingKey}.";
        Map<String, String> parameters = mapOf("key", "value");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, ${missingKey}.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNonPlaceholderTextOnly_thenReturnsTemplate() {
        // Arrange
        String template = "Simple text without placeholders.";
        Map<String, String> parameters = mapOf("key", "value");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Simple text without placeholders.", result);
    }

    // =====================================================================
    // Tests for formatStringWithNamedParameters - Edge Cases and Boundary Conditions
    // =====================================================================

    @Test
    void testFormatStringWithNamedParameters_whenEmptyTemplate_thenReturnsEmptyString() {
        // Arrange
        String template = "";
        Map<String, String> parameters = mapOf("key", "value");

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenEmptyParametersMap_thenReturnsOriginalTemplate() {
        // Arrange
        String template = standardTemplate();
        Map<String, String> parameters = new HashMap<>();

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals(template, result);
    }

    // =====================================================================
    // Tests for formatStringWithNamedParameters - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testFormatStringWithNamedParameters_whenNullParameters_thenReturnsOriginalTemplate() {
        // Arrange
        String template = standardTemplate();

        // Act
        String result = StringUtil.formatStringWithNamedParameters(template, null);

        // Assert
        assertEquals(template, result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNullTemplate_thenThrowsNullPointerException() {
        // Arrange
        Map<String, String> parameters = mapOf("key", "value");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                StringUtil.formatStringWithNamedParameters(null, parameters));
    }

    // =====================================================================
    // Tests for toString - Valid Data Processing
    // =====================================================================

    @Test
    void testToString_whenValidObject_thenReturnsObjectStringRepresentation() {
        // Arrange
        Object obj = 123;

        // Act
        String result = StringUtil.toString(obj);

        // Assert
        assertEquals("123", result);
    }

    @Test
    void testToString_whenCustomObject_thenReturnsCustomStringRepresentation() {
        // Arrange
        Object obj = new Object() {
            @Override
            public String toString() {
                return "CustomToString";
            }
        };

        // Act
        String result = StringUtil.toString(obj);

        // Assert
        assertEquals("CustomToString", result);
    }

    // =====================================================================
    // Tests for toString - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testToString_whenNullObject_thenReturnsNull() {
        // Act
        String result = StringUtil.toString(null);

        // Assert
        assertNull(result);
    }

    private static String standardTemplate() {
        return "Hello, ${name}! Welcome to ${place}.";
    }

    private static Map<String, String> mapOf(String... entries) {
        Map<String, String> map = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            map.put(entries[index], entries[index + 1]);
        }
        return map;
    }
}

