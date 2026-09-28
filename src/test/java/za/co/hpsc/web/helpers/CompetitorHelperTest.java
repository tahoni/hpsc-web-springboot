package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitorHelperTest {

    // toSentenceCaseLastName(String)
    @Test
    void testToSentenceCaseLastName_whenNull_thenReturnsNull() {
        // Act & Assert
        assertNull(CompetitorHelper.toSentenceCaseLastName(null));
    }

    @Test
    void testToSentenceCaseLastName_whenNoParticles_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("O'Neil-Smith", CompetitorHelper.toSentenceCaseLastName("O'Neil-Smith"));
    }

    @Test
    void testToSentenceCaseLastName_whenSingleParticle_thenLowerCasesIt() {
        // Act & Assert
        assertEquals("du Plessis", CompetitorHelper.toSentenceCaseLastName("Du Plessis"));
    }

    @Test
    void testToSentenceCaseLastName_whenMultipleParticles_thenLowerCasesAllOfThem() {
        // Act & Assert
        assertEquals("van der Merwe", CompetitorHelper.toSentenceCaseLastName("Van Der Merwe"));
    }

    @Test
    void testToSentenceCaseLastName_whenSurnameStartsWithParticleLetters_thenReturnsUnchanged() {
        // Act & Assert
        assertEquals("Dube", CompetitorHelper.toSentenceCaseLastName("Dube"));
        assertEquals("Vanderbilt", CompetitorHelper.toSentenceCaseLastName("Vanderbilt"));
    }

    @Test
    void testToSentenceCaseLastName_whenLastWordIsParticle_thenKeepsItCapitalised() {
        // Act & Assert
        assertEquals("Van", CompetitorHelper.toSentenceCaseLastName("Van"));
        assertEquals("de Van", CompetitorHelper.toSentenceCaseLastName("De Van"));
    }

    @Test
    void testToSentenceCaseLastName_whenEmpty_thenReturnsEmpty() {
        // Act & Assert
        assertEquals("", CompetitorHelper.toSentenceCaseLastName(""));
    }
}
