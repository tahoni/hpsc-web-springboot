package za.co.hpsc.web.models.ipsc.shared;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IpscCommonScoreTest {

    // IpscCommonScore(BigDecimal, BigDecimal, BigDecimal, PowerFactor, Integer, Integer, Integer, Integer, Integer,
    // Integer, Integer, Integer)
    @Test
    void testConstructor_whenAllFieldsProvided_thenMapsAllFields() {
        // Arrange
        BigDecimal percentage = new BigDecimal("95.50");
        BigDecimal weightedPoints = new BigDecimal("85.00");
        BigDecimal time = new BigDecimal("12.34");

        // Act
        IpscCommonScore score = new IpscCommonScore(percentage, weightedPoints, time, PowerFactor.MAJOR,
                8, 1, 0, 0, 0, 0, 1, 2);

        // Assert
        assertEquals(percentage, score.getPercentage());
        assertEquals(weightedPoints, score.getWeightedPoints());
        assertEquals(time, score.getTime());
        assertEquals(PowerFactor.MAJOR, score.getPowerFactor());
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
