package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {

    // formatStringWithNamedParameters(String, Map)
    @Test
    void testFormatStringWithNamedParameters_whenValidTemplateAndParameters_thenReplacesPlaceholders() {
        // Arrange
        String template = "Hello, ${name}! Welcome to ${place}.";
        String[] entries = new String[]{"name", "Alice", "place", "Wonderland"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Welcome to Wonderland.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenDuplicatePlaceholders_thenReplacesAllOccurrences() {
        // Arrange
        String template = "Hello, ${name}! ${name}, you are amazing!";
        String[] entries = new String[]{"name", "Alice"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Alice, you are amazing!", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenAdjacentPlaceholders_thenReplacesCorrectly() {
        // Arrange
        String template = "Welcome, ${firstName}${lastName}!";
        String[] entries = new String[]{"firstName", "John", "lastName", "Doe"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Welcome, JohnDoe!", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenMissingKeysInParameters_thenLeavesPlaceholdersUnchanged() {
        // Arrange
        String template = "Hello, ${name}! Welcome to ${place}.";
        String[] entries = new String[]{"name", "Alice"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, Alice! Welcome to ${place}.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNonExistentPlaceholder_thenLeavesItUnchanged() {
        // Arrange
        String template = "Hello, ${missingKey}.";
        String[] entries = new String[]{"key", "value"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Hello, ${missingKey}.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNonPlaceholderTextOnly_thenReturnsTemplate() {
        // Arrange
        String template = "Simple text without placeholders.";
        String[] entries = new String[]{"key", "value"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("Simple text without placeholders.", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenEmptyTemplate_thenReturnsEmptyString() {
        // Arrange
        String template = "";
        String[] entries = new String[]{"key", "value"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenEmptyParametersMap_thenReturnsOriginalTemplate() {
        // Arrange
        String template = "Hello, ${name}! Welcome to ${place}.";
        Map<String, String> parameters = new HashMap<>();

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, parameters);

        // Assert
        assertEquals(template, result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNullParameters_thenReturnsOriginalTemplate() {
        // Arrange
        String template = "Hello, ${name}! Welcome to ${place}.";

        // Act
        String result = StringUtils.formatStringWithNamedParameters(template, null);

        // Assert
        assertEquals(template, result);
    }

    @Test
    void testFormatStringWithNamedParameters_whenNullTemplate_thenThrowsNullPointerException() {
        // Arrange
        String[] entries = new String[]{"key", "value"};
        Map<String, String> parameters = new HashMap<>();
        for (int index = 0; index + 1 < entries.length; index += 2) {
            parameters.put(entries[index], entries[index + 1]);
        }

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                StringUtils.formatStringWithNamedParameters(null, parameters));
    }

    // toString(Object)
    @Test
    void testToString_whenValidObject_thenReturnsObjectStringRepresentation() {
        // Arrange
        Object obj = 123;

        // Act
        String result = StringUtils.toString(obj);

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
        String result = StringUtils.toString(obj);

        // Assert
        assertEquals("CustomToString", result);
    }

    @Test
    void testToString_whenNullObject_thenReturnsNull() {
        // Act & Assert
        assertNull(StringUtils.toString(null));
    }

    // toProperCase(String)
    @Test
    void testToProperCase_whenNull_thenReturnsNull() {
        // Act & Assert
        assertNull(StringUtils.toProperCase(null));
    }

    @Test
    void testToProperCase_whenMixedCaseWords_thenCapitalisesEachWord() {
        // Act & Assert
        assertEquals("Jane Ann Doe", StringUtils.toProperCase("jANE ann DOE"));
    }

    @Test
    void testToProperCase_whenHyphenatedOrApostrophised_thenCapitalisesEachPart() {
        // Act & Assert
        assertEquals("O'Neil-Smith", StringUtils.toProperCase("o'NEIL-SMITH"));
    }

    @Test
    void testToProperCase_whenCurlyApostrophe_thenCapitalisesTheNextLetter() {
        // Act & Assert
        assertEquals("O’Neil", StringUtils.toProperCase("o’NEIL"));
    }

    @Test
    void testToProperCase_whenDigitsOnly_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("0821234567", StringUtils.toProperCase("0821234567"));
    }

    @Test
    void testToProperCase_whenEmpty_thenReturnsEmpty() {
        // Act & Assert
        assertEquals("", StringUtils.toProperCase(""));
    }
}

