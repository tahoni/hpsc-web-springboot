package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitorHelpersTest {

    // toSentenceCaseLastName(String)
    @Test
    void testToSentenceCaseLastName_whenNull_thenReturnsNull() {
        // Act & Assert
        assertNull(CompetitorHelpers.toSentenceCaseLastName(null));
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
