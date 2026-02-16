package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilTest {

    @Test
    void testCalculatePercentage_withValidInputs_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.valueOf(25).setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_withPartZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_withTotalZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_withNullTotal_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, null);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_withNegativeValues_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(-25);
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.valueOf(-25).setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_withRounding_thenReturnsRoundedPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(33);
        BigDecimal total = BigDecimal.valueOf(200);
        BigDecimal expected = BigDecimal.valueOf(16.50).setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(expected, result);
    }
}