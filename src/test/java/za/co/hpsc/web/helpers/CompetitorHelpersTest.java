package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitorHelpersTest {

    // getCompetitorNumberAsInteger(String)
    @Test
    void testGetCompetitorNumberAsInteger_whenNumeric_thenReturnsInteger() {
        // Act & Assert
        assertEquals(123, CompetitorHelpers.getCompetitorNumberAsInteger("123"));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenSurroundedByWhitespace_thenTrimsAndReturnsInteger() {
        // Act & Assert
        assertEquals(123, CompetitorHelpers.getCompetitorNumberAsInteger("  123 "));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenLeadingZeros_thenReturnsInteger() {
        // Act & Assert
        assertEquals(42, CompetitorHelpers.getCompetitorNumberAsInteger("0042"));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenNull_thenReturnsZero() {
        // Act & Assert
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger(null));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenEmptyOrBlank_thenReturnsZero() {
        // Act & Assert
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger(""));
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger("   "));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenNotNumeric_thenReturnsZero() {
        // Act & Assert
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger("abc"));
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger("12a"));
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger("1.5"));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenExcludedIcsAlias_thenReturnsZero() {
        // Act & Assert
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger("15000"));
        assertEquals(0, CompetitorHelpers.getCompetitorNumberAsInteger(" 16000 "));
    }

    @Test
    void testGetCompetitorNumberAsInteger_whenNextToExcludedIcsAlias_thenReturnsInteger() {
        // Act & Assert
        assertEquals(14999, CompetitorHelpers.getCompetitorNumberAsInteger("14999"));
        assertEquals(15001, CompetitorHelpers.getCompetitorNumberAsInteger("15001"));
    }

    // toSentenceCaseLastName(String)
    @Test
    void testToSentenceCaseLastName_whenNull_thenReturnsEmptyString() {
        // Act & Assert
        assertEquals("", CompetitorHelpers.toSentenceCaseLastName(null));
    }

    @Test
    void testToSentenceCaseLastName_whenNoParticles_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("O'Neil-Smith", CompetitorHelpers.toSentenceCaseLastName("O'Neil-Smith"));
    }

    @Test
    void testToSentenceCaseLastName_whenSingleParticle_thenLowerCasesIt() {
        // Act & Assert
        assertEquals("du Plessis", CompetitorHelpers.toSentenceCaseLastName("Du Plessis"));
    }

    @Test
    void testToSentenceCaseLastName_whenMultipleParticles_thenLowerCasesAllOfThem() {
        // Act & Assert
        assertEquals("van der Merwe", CompetitorHelpers.toSentenceCaseLastName("Van Der Merwe"));
    }

    @Test
    void testToSentenceCaseLastName_whenSurnameStartsWithParticleLetters_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("Dube", CompetitorHelpers.toSentenceCaseLastName("Dube"));
        assertEquals("Vanderbilt", CompetitorHelpers.toSentenceCaseLastName("Vanderbilt"));
    }

    @Test
    void testToSentenceCaseLastName_whenLastWordIsParticle_thenKeepsItCapitalised() {
        // Act & Assert
        assertEquals("Van", CompetitorHelpers.toSentenceCaseLastName("Van"));
        assertEquals("de Van", CompetitorHelpers.toSentenceCaseLastName("De Van"));
    }

    @Test
    void testToSentenceCaseLastName_whenHyphenatedWithParticles_thenLowerCasesThem() {
        // Act & Assert
        assertEquals("Smith-van der Merwe", CompetitorHelpers.toSentenceCaseLastName("Smith-Van Der Merwe"));
        assertEquals("du Plessis-de Villiers", CompetitorHelpers.toSentenceCaseLastName("Du Plessis-De Villiers"));
        assertEquals("Botha-Smith", CompetitorHelpers.toSentenceCaseLastName("Botha-Smith"));
    }

    @Test
    void testToSentenceCaseLastName_whenHyphenatedEndsWithParticle_thenKeepsItCapitalised() {
        // Act & Assert
        assertEquals("Smith-Van", CompetitorHelpers.toSentenceCaseLastName("Smith-Van"));
    }

    @Test
    void testToSentenceCaseLastName_whenMcPrefix_thenCapitalisesTheNextLetter() {
        // Act & Assert
        assertEquals("McDonald", CompetitorHelpers.toSentenceCaseLastName("Mcdonald"));
        assertEquals("McKenzie", CompetitorHelpers.toSentenceCaseLastName("mckenzie"));
        assertEquals("McIntyre", CompetitorHelpers.toSentenceCaseLastName("Mcintyre"));
        assertEquals("Smith-McDonald", CompetitorHelpers.toSentenceCaseLastName("Smith-Mcdonald"));
        assertEquals("van der McDonald", CompetitorHelpers.toSentenceCaseLastName("Van Der Mcdonald"));
    }

    @Test
    void testToSentenceCaseLastName_whenAlreadyMcCased_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("McDonald", CompetitorHelpers.toSentenceCaseLastName("McDonald"));
    }

    @Test
    void testToSentenceCaseLastName_whenZuluMchOrMcuSurname_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("Mchunu", CompetitorHelpers.toSentenceCaseLastName("Mchunu"));
        assertEquals("Mcunu", CompetitorHelpers.toSentenceCaseLastName("Mcunu"));
    }

    @Test
    void testToSentenceCaseLastName_whenMcOnly_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("Mc", CompetitorHelpers.toSentenceCaseLastName("Mc"));
    }

    @Test
    void testToSentenceCaseLastName_whenEmpty_thenReturnsEmpty() {
        // Act & Assert
        assertEquals("", CompetitorHelpers.toSentenceCaseLastName(""));
    }
}
