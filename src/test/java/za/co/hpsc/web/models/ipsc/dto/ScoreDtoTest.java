package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreDtoTest {

    // Constructor mapping - No Parameter

    // Default Construction
    @Test
    void testDefaultConstructor_whenNoArguments_thenInitializesWithDefaults() {
        // Arrange & Act
        ScoreDto dto = new ScoreDto();

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertNull(dto.getMatchIndex());
        assertNull(dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertNotNull(dto.getLastModified());
    }

    @Test
    void testDefaultConstructor_whenNoArguments_thenUuidIsUnique() {
        // Arrange & Act
        ScoreDto dto1 = new ScoreDto();
        ScoreDto dto2 = new ScoreDto();

        // Assert
        assertNotNull(dto1.getUuid());
        assertNotNull(dto2.getUuid());
        assertNotEquals(dto1.getUuid(), dto2.getUuid());
    }

    @Test
    void testDefaultConstructor_whenNoArguments_thenLastModifiedIsSetToNow() {
        // Arrange
        LocalDateTime beforeConstruction = LocalDateTime.now();

        // Act
        ScoreDto dto = new ScoreDto();

        LocalDateTime afterConstruction = LocalDateTime.now();

        // Assert
        assertNotNull(dto.getLastModified());
        assertTrue(dto.getLastModified().isEqual(beforeConstruction) ||
                dto.getLastModified().isAfter(beforeConstruction));
        assertTrue(dto.getLastModified().isBefore(afterConstruction) ||
                dto.getLastModified().isEqual(afterConstruction));
    }

    // Constructor mapping - Single Parameter (ScoreResponse)

    // Null ScoreResponse
    @Test
    void testConstructor_whenScoreResponseNull_thenKeepsDefaults() {
        // Arrange & Act
        ScoreDto dto = new ScoreDto(null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertNull(dto.getMatchIndex());
        assertNull(dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertNotNull(dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponseNull_thenLastModifiedIsSetToNow() {
        // Arrange
        LocalDateTime beforeConstruction = LocalDateTime.now();

        // Act
        ScoreDto dto = new ScoreDto(null);

        LocalDateTime afterConstruction = LocalDateTime.now();

        // Assert
        assertNotNull(dto.getLastModified());
        assertTrue(dto.getLastModified().isEqual(beforeConstruction) ||
                dto.getLastModified().isAfter(beforeConstruction));
        assertTrue(dto.getLastModified().isBefore(afterConstruction) ||
                dto.getLastModified().isEqual(afterConstruction));
    }

    // Null and Empty Fields
    @Test
    void testConstructor_whenScoreResponseHasNullFields_thenMapsNulls() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(null);
        scoreResponse.setStageId(null);
        scoreResponse.setMemberId(null);
        scoreResponse.setTime(null);
        scoreResponse.setHitFactor(null);
        scoreResponse.setFinalScore(null);
        scoreResponse.setLastModified(null);

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getMatchIndex());
        assertNull(dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertNotNull(dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponseHasEmptyStrings_thenMapsEmptyStrings() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(10);
        scoreResponse.setStageId(1);
        scoreResponse.setMemberId(5);
        scoreResponse.setTime("");
        scoreResponse.setHitFactor("");
        scoreResponse.setFinalScore(0);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 25, 10, 30));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(10, dto.getMatchIndex());
        assertEquals(1, dto.getStageIndex());
        assertEquals(5, dto.getMemberIndex());
        assertEquals("", dto.getTime());
        assertEquals("", dto.getHitFactor());
        assertEquals(0, dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 2, 25, 10, 30), dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponseHasBlankStrings_thenMapsBlankStrings() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(15);
        scoreResponse.setStageId(2);
        scoreResponse.setMemberId(8);
        scoreResponse.setTime("   ");
        scoreResponse.setHitFactor("   ");
        scoreResponse.setFinalScore(100);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 3, 1, 14, 45));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(15, dto.getMatchIndex());
        assertEquals(2, dto.getStageIndex());
        assertEquals(8, dto.getMemberIndex());
        assertEquals("   ", dto.getTime());
        assertEquals("   ", dto.getHitFactor());
        assertEquals(100, dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 3, 1, 14, 45), dto.getLastModified());
    }

    // Partially Populated
    @Test
    void testConstructor_whenScoreResponsePartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(20);
        scoreResponse.setStageId(3);
        scoreResponse.setMemberId(12);
        scoreResponse.setTime("45.32");
        // hitFactor not set (null)
        // finalScore not set (null)
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 20, 9, 15));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(20, dto.getMatchIndex());
        assertEquals(3, dto.getStageIndex());
        assertEquals(12, dto.getMemberIndex());
        assertEquals("45.32", dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 2, 20, 9, 15), dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponsePartiallyPopulatedWithIndices_thenMapsSetFieldsAndNulls() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(25);
        scoreResponse.setStageId(4);
        // memberId not set (null)
        scoreResponse.setTime("38.15");
        scoreResponse.setHitFactor("2.62");
        // finalScore not set (null)
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 18, 11, 30));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(25, dto.getMatchIndex());
        assertEquals(4, dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertEquals("38.15", dto.getTime());
        assertEquals("2.62", dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 2, 18, 11, 30), dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponsePartiallyPopulatedWithScoreValues_thenMapsSetFieldsAndNulls() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        // matchId not set (null)
        scoreResponse.setStageId(5);
        scoreResponse.setMemberId(16);
        // time not set (null)
        scoreResponse.setHitFactor("1.95");
        scoreResponse.setFinalScore(95);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 15, 13, 45));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertNull(dto.getMatchIndex());
        assertEquals(5, dto.getStageIndex());
        assertEquals(16, dto.getMemberIndex());
        assertNull(dto.getTime());
        assertEquals("1.95", dto.getHitFactor());
        assertEquals(95, dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 2, 15, 13, 45), dto.getLastModified());
    }

    // Fully Populated
    @Test
    void testConstructor_whenScoreResponseFullyPopulated_thenMapsAllFields() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(30);
        scoreResponse.setStageId(6);
        scoreResponse.setMemberId(20);
        scoreResponse.setTime("42.87");
        scoreResponse.setHitFactor("2.33");
        scoreResponse.setFinalScore(120);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 10, 15, 20));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(30, dto.getMatchIndex());
        assertEquals(6, dto.getStageIndex());
        assertEquals(20, dto.getMemberIndex());
        assertEquals("42.87", dto.getTime());
        assertEquals("2.33", dto.getHitFactor());
        assertEquals(120, dto.getFinalScore());
        assertEquals(LocalDateTime.of(2026, 2, 10, 15, 20), dto.getLastModified());
    }

    @Test
    void testConstructor_whenScoreResponseFullyPopulated_thenUuidIsNotNull() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(35);
        scoreResponse.setStageId(7);
        scoreResponse.setMemberId(25);
        scoreResponse.setTime("50.00");
        scoreResponse.setHitFactor("2.00");
        scoreResponse.setFinalScore(100);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 5, 10, 0));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertNotNull(dto.getUuid());
    }

    @Test
    void testConstructor_whenScoreResponseFullyPopulated_thenIdAndIndexAreNull() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(40);
        scoreResponse.setStageId(8);
        scoreResponse.setMemberId(30);
        scoreResponse.setTime("55.55");
        scoreResponse.setHitFactor("1.80");
        scoreResponse.setFinalScore(110);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 1, 30, 12, 30));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getIndex());
    }

    // Edge Cases - Zero and Negative Values
    @Test
    void testConstructor_whenScoreResponseHasZeroIndices_thenMapsZeros() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(0);
        scoreResponse.setStageId(0);
        scoreResponse.setMemberId(0);
        scoreResponse.setTime("0.00");
        scoreResponse.setHitFactor("0.00");
        scoreResponse.setFinalScore(0);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 25, 10, 0));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(0, dto.getMatchIndex());
        assertEquals(0, dto.getStageIndex());
        assertEquals(0, dto.getMemberIndex());
        assertEquals("0.00", dto.getTime());
        assertEquals("0.00", dto.getHitFactor());
        assertEquals(0, dto.getFinalScore());
    }

    @Test
    void testConstructor_whenScoreResponseHasNegativeIndices_thenMapsNegatives() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(-1);
        scoreResponse.setStageId(-5);
        scoreResponse.setMemberId(-10);
        scoreResponse.setTime("-100.00");
        scoreResponse.setHitFactor("-1.50");
        scoreResponse.setFinalScore(-50);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 25, 10, 0));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(-1, dto.getMatchIndex());
        assertEquals(-5, dto.getStageIndex());
        assertEquals(-10, dto.getMemberIndex());
        assertEquals("-100.00", dto.getTime());
        assertEquals("-1.50", dto.getHitFactor());
        assertEquals(-50, dto.getFinalScore());
    }

    @Test
    void testConstructor_whenScoreResponseHasMaxValues_thenMapsMaxValues() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(Integer.MAX_VALUE);
        scoreResponse.setStageId(Integer.MAX_VALUE);
        scoreResponse.setMemberId(Integer.MAX_VALUE);
        scoreResponse.setTime("999999.99");
        scoreResponse.setHitFactor("999.99");
        scoreResponse.setFinalScore(Integer.MAX_VALUE);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 25, 23, 59));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(Integer.MAX_VALUE, dto.getMatchIndex());
        assertEquals(Integer.MAX_VALUE, dto.getStageIndex());
        assertEquals(Integer.MAX_VALUE, dto.getMemberIndex());
        assertEquals("999999.99", dto.getTime());
        assertEquals("999.99", dto.getHitFactor());
        assertEquals(Integer.MAX_VALUE, dto.getFinalScore());
    }

    // LastModified Handling
    @Test
    void testConstructor_whenLastModifiedIsNull_thenSetsCurrentDateTime() {
        // Arrange
        LocalDateTime beforeConstruction = LocalDateTime.now();
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(45);
        scoreResponse.setStageId(2);
        scoreResponse.setMemberId(10);
        scoreResponse.setTime("30.00");
        scoreResponse.setHitFactor("3.33");
        scoreResponse.setFinalScore(100);
        scoreResponse.setLastModified(null);

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);
        LocalDateTime afterConstruction = LocalDateTime.now();

        // Assert
        assertNotNull(dto.getLastModified());
        assertTrue(dto.getLastModified().isEqual(beforeConstruction) ||
                dto.getLastModified().isAfter(beforeConstruction));
        assertTrue(dto.getLastModified().isBefore(afterConstruction) ||
                dto.getLastModified().isEqual(afterConstruction));
    }

    @Test
    void testConstructor_whenLastModifiedIsProvided_thenUsesProvidedDateTime() {
        // Arrange
        LocalDateTime providedDateTime = LocalDateTime.of(2026, 1, 15, 8, 30, 45);
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(50);
        scoreResponse.setStageId(3);
        scoreResponse.setMemberId(15);
        scoreResponse.setTime("35.75");
        scoreResponse.setHitFactor("2.80");
        scoreResponse.setFinalScore(105);
        scoreResponse.setLastModified(providedDateTime);

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals(providedDateTime, dto.getLastModified());
    }

    // Special Cases - String Formatting
    @Test
    void testConstructor_whenTimeAndHitFactorHaveSpecialFormats_thenMapsCorrectly() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(55);
        scoreResponse.setStageId(4);
        scoreResponse.setMemberId(18);
        scoreResponse.setTime("12:34:56.789");
        scoreResponse.setHitFactor("12.3456789");
        scoreResponse.setFinalScore(150);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 1, 10, 6, 0));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals("12:34:56.789", dto.getTime());
        assertEquals("12.3456789", dto.getHitFactor());
    }

    @Test
    void testConstructor_whenTimeAndHitFactorHavePaddingSpaces_thenMapsPaddingSpaces() {
        // Arrange
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(60);
        scoreResponse.setStageId(5);
        scoreResponse.setMemberId(22);
        scoreResponse.setTime("  45.32  ");
        scoreResponse.setHitFactor("  2.15  ");
        scoreResponse.setFinalScore(105);
        scoreResponse.setLastModified(LocalDateTime.of(2026, 1, 5, 4, 15));

        // Act
        ScoreDto dto = new ScoreDto(scoreResponse);

        // Assert
        assertEquals("  45.32  ", dto.getTime());
        assertEquals("  2.15  ", dto.getHitFactor());
    }

    // Constructor mapping - All Parameters

    // Fully Populated with All Parameters
    @Test
    void testAllArgsConstructor_whenAllParametersProvided_thenMapsAllFields() {
        // Arrange
        LocalDateTime lastModified = LocalDateTime.of(2026, 2, 20, 12, 0);

        // Act
        ScoreDto dto = new ScoreDto(
                null,  // uuid will be set separately if needed
                100L,  // id
                5,     // index
                10,    // matchIndex
                2,     // stageIndex
                8,     // memberIndex
                "42.50",  // time
                "2.35",   // hitFactor
                100,   // finalScore
                lastModified
        );

        // Assert
        assertNull(dto.getUuid());  // uuid parameter is passed as null
        assertEquals(100L, dto.getId());
        assertEquals(5, dto.getIndex());
        assertEquals(10, dto.getMatchIndex());
        assertEquals(2, dto.getStageIndex());
        assertEquals(8, dto.getMemberIndex());
        assertEquals("42.50", dto.getTime());
        assertEquals("2.35", dto.getHitFactor());
        assertEquals(100, dto.getFinalScore());
        assertEquals(lastModified, dto.getLastModified());
    }

    @Test
    void testAllArgsConstructor_whenAllParametersNull_thenMapsAllNulls() {
        // Arrange & Act
        ScoreDto dto = new ScoreDto(
                null,  // uuid
                null,  // id
                null,  // index
                null,  // matchIndex
                null,  // stageIndex
                null,  // memberIndex
                null,  // time
                null,  // hitFactor
                null,  // finalScore
                null   // lastModified
        );

        // Assert
        assertNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertNull(dto.getMatchIndex());
        assertNull(dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertNull(dto.getLastModified());
    }

    @Test
    void testAllArgsConstructor_whenPartialParametersProvided_thenMapsProvidedFields() {
        // Arrange
        LocalDateTime lastModified = LocalDateTime.of(2026, 2, 15, 14, 30);

        // Act
        ScoreDto dto = new ScoreDto(
                null,  // uuid
                null,  // id
                null,  // index
                15,    // matchIndex
                3,     // stageIndex
                null,  // memberIndex
                "38.22",  // time
                null,  // hitFactor
                null,  // finalScore
                lastModified
        );

        // Assert
        assertNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertEquals(15, dto.getMatchIndex());
        assertEquals(3, dto.getStageIndex());
        assertNull(dto.getMemberIndex());
        assertEquals("38.22", dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getFinalScore());
        assertEquals(lastModified, dto.getLastModified());
    }

    // Constructor Equivalence Tests

    @Test
    void testConstructors_whenUsingNoArgAndSettingProperties_thenEquivalentToAllArgs() {
        // Arrange
        ScoreDto dto1 = new ScoreDto();
        dto1.setId(75L);
        dto1.setIndex(10);
        dto1.setMatchIndex(20);
        dto1.setStageIndex(4);
        dto1.setMemberIndex(15);
        dto1.setTime("48.00");
        dto1.setHitFactor("2.08");
        dto1.setFinalScore(100);
        LocalDateTime lastMod = LocalDateTime.of(2026, 2, 12, 9, 45);
        dto1.setLastModified(lastMod);

        ScoreDto dto2 = new ScoreDto(
                null,
                75L,
                10,
                20,
                4,
                15,
                "48.00",
                "2.08",
                100,
                lastMod
        );

        // Assert
        assertEquals(dto1.getId(), dto2.getId());
        assertEquals(dto1.getIndex(), dto2.getIndex());
        assertEquals(dto1.getMatchIndex(), dto2.getMatchIndex());
        assertEquals(dto1.getStageIndex(), dto2.getStageIndex());
        assertEquals(dto1.getMemberIndex(), dto2.getMemberIndex());
        assertEquals(dto1.getTime(), dto2.getTime());
        assertEquals(dto1.getHitFactor(), dto2.getHitFactor());
        assertEquals(dto1.getFinalScore(), dto2.getFinalScore());
        assertEquals(dto1.getLastModified(), dto2.getLastModified());
    }

    @Test
    void testConstructors_whenScoreResponseAndNoArg_thenScoreResponsePreservesDataBetter() {
        // Arrange - Test that ScoreResponse constructor preserves data better than no-arg constructor
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMatchId(80);
        scoreResponse.setStageId(5);
        scoreResponse.setMemberId(25);
        scoreResponse.setTime("32.10");
        scoreResponse.setHitFactor("3.11");
        scoreResponse.setFinalScore(100);
        LocalDateTime lastMod = LocalDateTime.of(2026, 2, 8, 11, 0);
        scoreResponse.setLastModified(lastMod);

        // Act
        ScoreDto dtoFromResponse = new ScoreDto(scoreResponse);
        ScoreDto dtoEmpty = new ScoreDto();

        // Assert
        assertEquals(80, dtoFromResponse.getMatchIndex());
        assertNull(dtoEmpty.getMatchIndex());

        assertEquals(lastMod, dtoFromResponse.getLastModified());
        assertNotNull(dtoEmpty.getLastModified());
        assertNotEquals(lastMod, dtoEmpty.getLastModified());
    }
}
