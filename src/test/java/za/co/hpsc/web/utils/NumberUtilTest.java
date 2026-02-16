package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

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
        assertEquals(BigDecimal.valueOf(25).setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculatePercentage_withPartZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.valueOf(100);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculatePercentage_withTotalZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.ZERO;

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculatePercentage_withNullTotal_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, null);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculatePercentage_withNegativeValues_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(-25);
        BigDecimal total = BigDecimal.valueOf(100);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(-25).setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculatePercentage_withRounding_thenReturnsRoundedPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(33);
        BigDecimal total = BigDecimal.valueOf(200);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total);

        // Assert
        assertEquals(BigDecimal.valueOf(16.50).setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP),
                result);
    }

    @Test
    void testCalculateSum_WithPositiveNumbers_thenReturnsCorrectSum() {
        // Arrange
        List<BigDecimal> values = List.of(new BigDecimal("10.5"), new BigDecimal("20.5"), new BigDecimal("30.0"));
        BigDecimal expected = new BigDecimal("61.00").setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSum_WithEmptyList_thenReturnsZero() {
        // Arrange
        List<BigDecimal> values = Collections.emptyList();
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSum_WithNullList_thenReturnZero() {
        // Arrange
        List<BigDecimal> values = null;
        BigDecimal expected = BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculateSum_WithNumbersNeedingScaling_thenReturnsScaledSum() {
        // Arrange
        List<BigDecimal> values = List.of(new BigDecimal("10.123"), new BigDecimal("20.456"),
                new BigDecimal("30.789"));
        BigDecimal expected = new BigDecimal("61.368").setScale(SystemConstants.DEFAULT_SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculateSum(values);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatBigDecimal_withNullValue_thenReturnsZeroString() {
        // Arrange
        int scale = 2;

        // Act
        String result = NumberUtil.formatBigDecimal(null, scale);

        // Assert
        assertEquals("0.00", result);
    }

    @Test
    void testFormatBigDecimal_withZeroScale_thenReturnsIntegerString() {
        // Arrange
        BigDecimal value = BigDecimal.valueOf(123.75);
        int scale = 0;

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals("124", result);
    }


    @Test
    void testFormatBigDecimal_WithNonNullValue_thenReturnsFormattedString() {
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
    void testFormatBigDecimal_WithNullValue_thenReturnsZeroFormattedString() {
        // Arrange
        BigDecimal value = null;
        int scale = 2;
        String expected = "0.00";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testFormatBigDecimal_WithRoundingUp_thenReturnsFormattedString() {
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
    void testFormatBigDecimal_WithRoundingDown_thenReturnsFormattedString() {
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
    void testFormatBigDecimal_WithZeroScale_thenReturnsFormattedString() {
        // Arrange
        BigDecimal value = new BigDecimal("123.5");
        int scale = 0;
        String expected = "124";

        // Act
        String result = NumberUtil.formatBigDecimal(value, scale);

        // Assert
        assertEquals(expected, result);
    }
}
