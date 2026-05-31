package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IpscMatchStageTest {

    // toString()
    @Test
    void testToString_whenStageNameAndStageNumberAreSet_thenFormatsNameAndNumber() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("El Presidente");
        stage.setStageNumber(3);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("El Presidente (3)", result);
    }

    @Test
    void testToString_whenStageNameIsNull_thenIncludesOnlyNumber() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName(null);
        stage.setStageNumber(2);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("(2)", result);
    }

    @Test
    void testToString_whenStageNumberIsFirstStage_thenFormatsCorrectly() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("Opening Stage");
        stage.setStageNumber(1);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("Opening Stage (1)", result);
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("Consistent Stage");
        stage.setStageNumber(4);

        // Act
        String result1 = stage.toString();
        String result2 = stage.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_whenCalled_thenContainsStageNumberInOutput() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("Speed Stage");
        stage.setStageNumber(9);

        // Act
        String result = stage.toString();

        // Assert
        assertTrue(result.contains("9"));
    }

    @Test
    void testToString_whenCalled_thenContainsStageNameInOutput() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("Speed Stage");
        stage.setStageNumber(9);

        // Act
        String result = stage.toString();

        // Assert
        assertTrue(result.contains("Speed Stage"));
    }

    @Test
    void testToString_whenStageNumberIsNull_thenDontIncludesNullLiteral() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("No Number");
        stage.setStageNumber(null);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("No Number", result);
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameValue() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();

        // Act
        stage.onInsert();

        // Assert
        assertNotNull(stage.getDateCreated());
        assertNotNull(stage.getDateUpdated());
        assertEquals(stage.getDateCreated(), stage.getDateUpdated());
    }

    // =====================================================================
    // onUpdate()
    // =====================================================================

    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedOnly() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.onInsert();
        LocalDateTime createdBeforeUpdate = stage.getDateCreated();
        LocalDateTime updatedBeforeUpdate = stage.getDateUpdated();

        // Act
        stage.onUpdate();

        // Assert
        assertEquals(createdBeforeUpdate, stage.getDateCreated());
        assertNotNull(stage.getDateUpdated());
        assertFalse(stage.getDateUpdated().isBefore(updatedBeforeUpdate));
    }
}

