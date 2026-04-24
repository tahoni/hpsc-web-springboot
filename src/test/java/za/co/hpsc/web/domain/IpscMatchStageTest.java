package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IpscMatchStageTest {

    // =====================================================================
    // init(MatchStageDto)
    // =====================================================================

    @Test
    void testInit_whenDtoIsFullyPopulated_thenMapsAllFields() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(3);
        dto.setStageName("El Presidente");
        dto.setRangeNumber(2);
        dto.setTargetPaper(6);
        dto.setTargetPopper(2);
        dto.setTargetPlates(4);
        dto.setTargetDisappear(1);
        dto.setTargetPenalty(0);
        dto.setMinRounds(12);
        dto.setMaxPoints(60);

        // Act
        stage.init(dto);

        // Assert
        assertEquals(3, stage.getStageNumber());
        assertEquals("El Presidente", stage.getStageName());
        assertEquals(2, stage.getRangeNumber());
        assertEquals(6, stage.getTargetPaper());
        assertEquals(2, stage.getTargetPopper());
        assertEquals(4, stage.getTargetPlates());
        assertEquals(1, stage.getTargetDisappear());
        assertEquals(0, stage.getTargetPenalty());
        assertEquals(12, stage.getMinRounds());
        assertEquals(60, stage.getMaxPoints());
    }

    @Test
    void testInit_whenStageNameIsNull_thenStageNameIsNull() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(1);
        dto.setStageName(null);
        dto.setRangeNumber(1);

        // Act
        stage.init(dto);

        // Assert
        assertNull(stage.getStageName());
        assertEquals(1, stage.getStageNumber());
    }

    @Test
    void testInit_whenRangeNumberIsNull_thenRangeNumberIsNull() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(2);
        dto.setStageName("Stage 2");
        dto.setRangeNumber(null);

        // Act
        stage.init(dto);

        // Assert
        assertNull(stage.getRangeNumber());
    }

    @Test
    void testInit_whenAllTargetsAreNull_thenTargetsAreNull() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(1);
        dto.setStageName("Null Targets Stage");
        dto.setTargetPaper(null);
        dto.setTargetPopper(null);
        dto.setTargetPlates(null);
        dto.setTargetDisappear(null);
        dto.setTargetPenalty(null);
        dto.setMinRounds(null);
        dto.setMaxPoints(null);

        // Act
        stage.init(dto);

        // Assert
        assertNull(stage.getTargetPaper());
        assertNull(stage.getTargetPopper());
        assertNull(stage.getTargetPlates());
        assertNull(stage.getTargetDisappear());
        assertNull(stage.getTargetPenalty());
        assertNull(stage.getMinRounds());
        assertNull(stage.getMaxPoints());
    }

    @Test
    void testInit_whenCalledTwice_thenLastDtoValuesOverwrite() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();

        MatchStageDto first = new MatchStageDto();
        first.setStageNumber(1);
        first.setStageName("First Stage");
        first.setRangeNumber(1);
        first.setTargetPaper(4);
        first.setMaxPoints(40);

        MatchStageDto second = new MatchStageDto();
        second.setStageNumber(5);
        second.setStageName("Second Stage");
        second.setRangeNumber(3);
        second.setTargetPaper(8);
        second.setMaxPoints(80);

        // Act
        stage.init(first);
        stage.init(second);

        // Assert
        assertEquals(5, stage.getStageNumber());
        assertEquals("Second Stage", stage.getStageName());
        assertEquals(3, stage.getRangeNumber());
        assertEquals(8, stage.getTargetPaper());
        assertEquals(80, stage.getMaxPoints());
    }

    @Test
    void testInit_whenTargetPaperIsZero_thenTargetPaperIsZero() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(1);
        dto.setStageName("Stage");
        dto.setTargetPaper(0);

        // Act
        stage.init(dto);

        // Assert
        assertEquals(0, stage.getTargetPaper());
    }

    // =====================================================================
    // toString()
    // =====================================================================

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
    void testToString_whenStageNameIsNull_thenIncludesNullLiteralAndNumber() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName(null);
        stage.setStageNumber(2);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("null (2)", result);
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
    void testToString_whenCalledAfterInit_thenReflectsInitialisedValues() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(7);
        dto.setStageName("Final Stage");
        stage.init(dto);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("Final Stage (7)", result);
    }

    @Test
    void testToString_containsStageNumberInOutput() {
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
    void testToString_containsStageNameInOutput() {
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
    void init_whenDtoIsNull_thenThrowsNullPointerException() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> stage.init(null));
    }

    @Test
    void init_whenDtoHasNullStageNumber_thenStageNumberBecomesNull() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(null);
        dto.setStageName("Null Number Stage");
        dto.setRangeNumber(5);

        // Act
        stage.init(dto);

        // Assert
        assertNull(stage.getStageNumber());
        assertEquals("Null Number Stage", stage.getStageName());
        assertEquals(5, stage.getRangeNumber());
    }

    @Test
    void toString_whenStageNumberIsNull_thenIncludesNullLiteral() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageName("No Number");
        stage.setStageNumber(null);

        // Act
        String result = stage.toString();

        // Assert
        assertEquals("No Number (null)", result);
    }

    @Test
    void onInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameValue() {
        // Arrange
        IpscMatchStage stage = new IpscMatchStage();

        // Act
        stage.onInsert();

        // Assert
        assertNotNull(stage.getDateCreated());
        assertNotNull(stage.getDateUpdated());
        assertEquals(stage.getDateCreated(), stage.getDateUpdated());
    }

    @Test
    void onUpdate_whenInvoked_thenUpdatesDateUpdatedOnly() {
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

