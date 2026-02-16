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
        // Utility class, not to be instantiated
    }

    /**
     * Calculates the percentage of a part relative to a total.
     *
     * <p>
     * If the total is null or zero, the method returns a value of zero,
     * rounded to the default scale defined in {@code SystemConstants.DEFAULT_SCALE}.
     * </p>
     *
     * @param part  the portion or subset of the total to calculate percentage for.
     *              Must not be null.
     * @param whole the whole or total value.
     *              Can be null or zero.
     * @return the percentage of {@code part} relative to {@code total}, scaled to the default scale.
     * If {@code total} is null or zero, returns zero.
     */
    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal whole) {
        BigDecimal result = BigDecimal.ZERO;
        // Calculates percentage to double the default scale, then multiplies by 100
        if ((whole != null) && (whole.compareTo(BigDecimal.ZERO) != 0)) {
            result = part.divide(whole, SystemConstants.DEFAULT_SCALE * 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        // Scales the result to the default scale
        return result.setScale(SystemConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    // TODO: Javadoc
    public static String formatBigDecimal(BigDecimal value, int scale) {
        BigDecimal result = ((value != null) ? value : BigDecimal.ZERO);
        // Scales the result to the default scale
        return result.setScale(scale, RoundingMode.HALF_UP).toString();
    }
}
