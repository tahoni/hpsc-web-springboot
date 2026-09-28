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
    void testToSentenceCaseLastName_whenEmpty_thenReturnsEmpty() {
        // Act & Assert
        assertEquals("", CompetitorHelpers.toSentenceCaseLastName(""));
    }
}
