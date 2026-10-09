package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {

    // toString(Object)
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

    @Test
    void testToString_whenNullObject_thenReturnsNull() {
        // Act & Assert
        assertNull(StringUtil.toString(null));
    }

    // toProperCase(String)
    @Test
    void testToProperCase_whenNull_thenReturnsNull() {
        // Act & Assert
        assertNull(StringUtil.toProperCase(null));
    }

    @Test
    void testToProperCase_whenMixedCaseWords_thenCapitalisesEachWord() {
        // Act & Assert
        assertEquals("Jane Ann Doe", StringUtil.toProperCase("jANE ann DOE"));
    }

    @Test
    void testToProperCase_whenHyphenatedOrApostrophised_thenCapitalisesEachPart() {
        // Act & Assert
        assertEquals("O'Neil-Smith", StringUtil.toProperCase("o'NEIL-SMITH"));
    }

    @Test
    void testToProperCase_whenCurlyApostrophe_thenCapitalisesTheNextLetter() {
        // Act & Assert
        assertEquals("O’Neil", StringUtil.toProperCase("o’NEIL"));
    }

    @Test
    void testToProperCase_whenDigitsOnly_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("0821234567", StringUtil.toProperCase("0821234567"));
    }

    @Test
    void testToProperCase_whenEmpty_thenReturnsEmpty() {
        // Act & Assert
        assertEquals("", StringUtil.toProperCase(""));
    }

    // hasText(String)
    @Test
    void testHasText_whenNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(StringUtil.hasText(null));
    }

    @Test
    void testHasText_whenEmpty_thenReturnsFalse() {
        // Act & Assert
        assertFalse(StringUtil.hasText(""));
    }

    @Test
    void testHasText_whenOnlyWhitespace_thenReturnsFalse() {
        // Act & Assert
        assertFalse(StringUtil.hasText("   "));
        assertFalse(StringUtil.hasText(" \t\r\n "));
    }

    @Test
    void testHasText_whenNonBlank_thenReturnsTrue() {
        // Act & Assert
        assertTrue(StringUtil.hasText("text"));
        assertTrue(StringUtil.hasText("0"));
    }

    @Test
    void testHasText_whenTextSurroundedByWhitespace_thenReturnsTrue() {
        // Act & Assert
        assertTrue(StringUtil.hasText("  text \t"));
    }
}
