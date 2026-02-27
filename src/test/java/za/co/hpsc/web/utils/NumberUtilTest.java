package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilTest {

    // =====================================================================
    // Tests for calculatePercentage - Valid Data Processing
    // =====================================================================

    @Test
    void testCalculatePercentage_whenValidInputs_thenReturnsCorrectPercentage() {
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
    void testCalculatePercentage_whenPartZero_thenReturnsZero() {
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
    void testCalculatePercentage_whenNegativeValues_thenReturnsCorrectPercentage() {
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
    void testCalculatePercentage_whenRoundingNeeded_thenReturnsRoundedPercentage() {
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

    // =====================================================================
    // Tests for calculatePercentage - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testCalculatePercentage_whenTotalZero_thenReturnsZero() {
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
    void testCalculatePercentage_whenNullTotal_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, null);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for calculateSum - Valid Data Processing
    // =====================================================================

    @Test
    void testCalculateSum_whenPositiveNumbers_thenReturnsCorrectSum() {
        // Arrange
        List<BigDecimal> values = List.of(
                new BigDecimal("10.5"),
                new BigDecimal("20.5"),
                new BigDecimal("30.0")
        );
        BigDecimal expected = new BigDecimal("61.00").setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSum_whenNumbersNeedingScaling_thenReturnsScaledSum() {
        // Arrange
        List<BigDecimal> values = List.of(
                new BigDecimal("10.123"),
                new BigDecimal("20.456"),
                new BigDecimal("30.789")
        );
        BigDecimal expected = new BigDecimal("61.368").setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for calculateSum - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testCalculateSum_whenEmptyList_thenReturnsZero() {
        // Arrange
        List<BigDecimal> values = Collections.emptyList();
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSum_whenNullList_thenReturnsZero() {
        // Arrange
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(null);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for formatBigDecimal - Valid Data Processing
    // =====================================================================

    @Test
    void testFormatBigDecimal_whenNonNullValue_thenReturnsFormattedString() {
        // Arrange
        BigDecimal value = new BigDecimal("123.456");
        int scale = 2;
        String expected = "123.46";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatBigDecimal_whenRoundingUp_thenReturnsFormattedString() {
        // Arrange
        BigDecimal value = new BigDecimal("99.995");
        int scale = 2;
        String expected = "100.00";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatBigDecimal_whenRoundingDown_thenReturnsFormattedString() {
        // Arrange
        BigDecimal value = new BigDecimal("123.454");
        int scale = 2;
        String expected = "123.45";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatBigDecimal_whenZeroScale_thenReturnsIntegerString() {
        // Arrange
        BigDecimal value = new BigDecimal("123.5");
        int scale = 0;
        String expected = "124";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }

    // =====================================================================
    // Tests for formatBigDecimal - Input Validation and Error Handling
    // =====================================================================

    @Test
    void testFormatBigDecimal_whenNullValue_thenReturnsZeroString() {
        // Arrange
        int scale = 2;
        String expected = "0.00";

        // Act
        String result = NumberUtil.formatBigDecimal(null, scale);

        // Assert
        assertEquals(expected, result);
    }
}
