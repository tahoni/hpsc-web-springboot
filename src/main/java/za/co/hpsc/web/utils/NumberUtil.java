package za.co.hpsc.web.utils;

import za.co.hpsc.web.constants.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class providing methods for numeric calculations and operations.
 *
 * <p>
 * The {@code NumberUtil} class offers static methods for common numerical tasks. These methods
 * are designed to handle various use cases where numerical computations are required.
 * </p>
 */
public final class NumberUtil {
    private NumberUtil() {
        // Utility class
    }

    /**
     * Calculates the percentage of a part relative to a total.
     *
     * <p>
     * If the total is null or zero, the method returns a value of zero,
     * rounded to the default scale defined in {@code SystemConstants.DEFAULT_SCALE}.
     * </p>
     *
     * @param part  the portion or subset of the total to calculate percentage for. Must not be null.
     * @param total the whole or total value. Can be null or zero.
     * @return the percentage of {@code part} relative to {@code total}, scaled to the default scale.
     * If {@code total} is null or zero, returns zero.
     */
    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        }
        return part.multiply(BigDecimal.valueOf(100))
                .setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP)
                .divide(total, RoundingMode.HALF_UP);
    }
}
