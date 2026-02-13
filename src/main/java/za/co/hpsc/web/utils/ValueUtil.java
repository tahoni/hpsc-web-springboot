package za.co.hpsc.web.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class providing methods to handle nullable values by substituting default values.
 *
 * <p>
 * The {@code ValueUtil} class offers static methods that help in replacing null references with
 * suitable defaults. These methods are designed to simplify null-handling by providing
 * straightforward alternatives when encountering nulls in common scenarios, such as
 * Strings, UUIDs, and collections.
 * </p>
 */
public final class ValueUtil {
    private ValueUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Returns the provided string if it is not null; otherwise, returns an empty string.
     *
     * @param value the string value to be checked; may be null.
     * @return the original string if it is not null, or an empty string if it is null.
     */
    public static String nullAsEmptyString(String value) {
        return (value != null ? value : "");
    }

    /**
     *
     * @param value
     * @return
     */
    public static String nullAsEmptyString(Object value) {
        return (value != null ? value.toString() : "");
    }

    /**
     * Returns the given integer value if it is not null; otherwise, returns zero.
     *
     * @param value the integer value to be checked; may be null.
     * @return the original integer value if it is not null, or zero if it is null.
     */
    public static int nullAsZero(Integer value) {
        return (value != null ? value : 0);
    }

    /**
     * Returns the given long value if it is not null; otherwise, returns zero.
     *
     * @param value the long value to be checked; may be null.
     * @return the original long value if it is not null, or zero if it is null.
     */
    public static long nullAsZero(Long value) {
        return (value != null ? value : 0L);
    }

    /**
     * Converts a nullable string representation of a number to a {@code BigDecimal},
     * returning {@code BigDecimal.ZERO} if the input string is {@code null}.or not a number
     *
     * @param value the string representation of the number to be converted; may be null.
     * @return a {@code BigDecimal} representing the input string,
     * or {@code BigDecimal.ZERO} if the input is null or not a number.
     */
    public static BigDecimal nullAsZeroBigDecimal(String value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Returns the provided UUID value if it is not null; otherwise, generates
     * and returns a random UUID.
     *
     * @param value the UUID to be checked; may be null.
     * @return the original UUID if it is not null, or a randomly generated
     * UUID if it is null.
     */
    public static UUID nullAsRandomUuid(UUID value) {
        return (value != null ? value : UUID.randomUUID());
    }

    /**
     * Returns the provided list if it is not null; otherwise, returns an empty list.
     *
     * @param value the list to be checked; may be null.
     * @return the original list if it is not null, or an empty list if it is null.
     */
    public static <T> List<T> nullAsEmptyList(List<T> value) {
        return (value != null ? value : new ArrayList<>());
    }
}
