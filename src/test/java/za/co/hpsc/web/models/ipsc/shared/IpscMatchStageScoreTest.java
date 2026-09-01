package za.co.hpsc.web.models.ipsc.shared;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IpscMatchStageScoreTest {

    // IpscMatchStageScore(BigDecimal, BigDecimal, BigDecimal, PowerFactor, Integer, Integer, Integer, Integer,
    // Integer, Integer, Integer, Integer, Integer, BigDecimal)
    @Test
    void testConstructor_whenAllFieldsProvided_thenMapsInheritedAndOwnFields() {
        // Arrange
        BigDecimal percentage = new BigDecimal("95.50");
        BigDecimal weightedPoints = new BigDecimal("85.00");
        BigDecimal time = new BigDecimal("12.34");
        BigDecimal hitFactor = new BigDecimal("6.89");

        // Act
        IpscMatchStageScore score = new IpscMatchStageScore(percentage, weightedPoints, time, PowerFactor.MAJOR,
                8, 1, 0, 0, 0, 0, 1, 2, 85, hitFactor);

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
        assertEquals(85, score.getRawPoints());
        assertEquals(hitFactor, score.getHitFactor());
    }
}
