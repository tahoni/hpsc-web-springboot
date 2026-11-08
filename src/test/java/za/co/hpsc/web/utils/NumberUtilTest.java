package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilTest {
    private static final int SCALE = 5;

    // calculatePercentage(BigDecimal, BigDecimal, int)
    @Test
    void testCalculatePercentage_whenValidInputs_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.valueOf(25).setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenPartZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.ZERO.setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenNegativeValues_thenReturnsCorrectPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(-25);
        BigDecimal total = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.valueOf(-25).setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenRoundingNeeded_thenReturnsRoundedPercentage() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(33);
        BigDecimal total = BigDecimal.valueOf(200);
        BigDecimal expected = BigDecimal.valueOf(16.50).setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenTotalZero_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal expected = BigDecimal.ZERO.setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenNullTotal_thenReturnsZero() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(25);
        BigDecimal expected = BigDecimal.ZERO.setScale(SCALE,
                RoundingMode.HALF_UP);

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, null, SCALE);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenCustomScale_thenReturnsPercentageWithGivenScale() {
        // Arrange
        BigDecimal part = BigDecimal.ONE;
        BigDecimal total = BigDecimal.valueOf(3);
        BigDecimal expected = new BigDecimal("33.3333");

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, 4);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenZeroScale_thenReturnsRoundedWholeNumber() {
        // Arrange
        BigDecimal part = BigDecimal.valueOf(2);
        BigDecimal total = BigDecimal.valueOf(3);
        BigDecimal expected = new BigDecimal("67");

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(part, total, 0);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void testCalculatePercentage_whenNullTotalAndCustomScale_thenReturnsZeroWithGivenScale() {
        // Arrange
        BigDecimal expected = new BigDecimal("0.000");

        // Act
        BigDecimal result = NumberUtil.calculatePercentage(BigDecimal.TEN, null, 3);

        // Assert
        assertEquals(expected, result);
    }

    // formatBigDecimal(BigDecimal, int)
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
