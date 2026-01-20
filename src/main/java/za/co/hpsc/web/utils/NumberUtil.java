package za.co.hpsc.web.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {
    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(total, RoundingMode.HALF_UP);
    }
}
