package za.co.hpsc.web.models.ipsc.shared;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IpscCommonScoreTest {

    // IpscCommonScore(BigDecimal, BigDecimal, BigDecimal, Integer, Integer, Integer, Integer, Integer,
    // Integer, Integer, Integer)
    @Test
    void testConstructor_whenAllFieldsProvided_thenMapsAllFields() {
        // Arrange
        BigDecimal percentage = new BigDecimal("95.50");
        BigDecimal points = new BigDecimal("85.00");
        BigDecimal time = new BigDecimal("12.34");

        // Act
        IpscCommonScore score = new IpscCommonScore(percentage, points, time,
                8, 1, 0, 0, 0, 0, 1, 2);

        // Assert
        assertEquals(percentage, score.getPercentage());
        assertEquals(points, score.getPoints());
        assertEquals(time, score.getTime());
        assertEquals(8, score.getAlpha());
        assertEquals(1, score.getCharlie());
        assertEquals(0, score.getDelta());
        assertEquals(0, score.getNoShoots());
        assertEquals(0, score.getMisses());
        assertEquals(0, score.getNoPenaltyMisses());
        assertEquals(1, score.getProceduralErrors());
        assertEquals(2, score.getAdditionalPenalties());
    }
}
