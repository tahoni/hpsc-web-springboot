package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilTest {

    @Test
    void testCalculatePercentage_withValidInputs_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.valueOf(100);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withPartZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.valueOf(100);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withTotalZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.ZERO;

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withNullTotal_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, null);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withNegativeValues_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(-25);
        BigDecimal total = BigDecimal.valueOf(100);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(-25).setScale(2, RoundingMode.HALF_UP),
                result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withRounding_thenReturnsRoundedPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(33);
        BigDecimal total = BigDecimal.valueOf(200);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(16.50).setScale(2, RoundingMode.HALF_UP),
                result.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculatePercentage_withVerySmallValues_thenHandlesCorrectly() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(0.0003);
        BigDecimal total = BigDecimal.valueOf(0.0009);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(33.33).setScale(2, RoundingMode.HALF_UP),
                result.setScale(2, RoundingMode.HALF_UP));
    }
}