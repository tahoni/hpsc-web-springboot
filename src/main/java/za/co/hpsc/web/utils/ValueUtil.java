package za.co.hpsc.web.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stateless utility for substituting sensible defaults when encountering null values.
 * <p>
 * This class provides a collection of static methods that transform {@code null} references
 * into appropriate default values (empty strings, zero, empty collections, random UUIDs).
 * It simplifies null-checking logic in client code and reduces defensive programming overhead.
 * </p>
 * <p>
 * <b>Thread-safety:</b> All methods are stateless and thread-safe.
 * </p>
 * <p>
 * <b>Usage:</b>
 * </p>
 * <pre>{@code
 *   String name = ValueUtil.nullAsEmptyString(nullableName);  // "" if null
 *   int count = ValueUtil.nullAsZero(nullableCount);          // 0 if null
 *   List<T> items = ValueUtil.nullAsEmptyList(nullableList);  // [] if null
 * }</pre>
 *
 * @see java.util.UUID
 * @see java.math.BigDecimal
 */
public final class ValueUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValueUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Returns the provided string, or an empty string if the input is {@code null}.
     *
     * @param value the string value to be checked; may be {@code null}
     * @return the original string if non-null; otherwise {@code ""}
     */
    public static String nullAsEmptyString(String value) {
        return nullAsDefaultString(value, "");
    }

    /**
     * Converts an object to its string representation, or returns an empty string if {@code null}.
     * <p>
     * If {@code value} is non-null, {@link Object#toString()} is invoked;
     * if {@code value} is {@code null}, an empty string is returned.
     * </p>
     *
     * @param value the object to be converted; may be {@code null}
     * @return the string representation of the object if non-null; otherwise {@code ""}
     */
    public static String nullAsEmptyString(Object value) {
        return nullAsDefaultString(value, "");
    }

    /**
     * Returns the provided {@code Integer}, or zero if the input is {@code null}.
     *
     * @param value the integer value to be checked; may be {@code null}
     * @return the original integer if non-null; otherwise {@code 0}
     */
    public static int nullAsZero(Integer value) {
        return nullAsDefault(value, 0);
    }

    /**
     * Returns the provided {@code Long}, or zero if the input is {@code null}.
     *
     * @param value the long value to be checked; may be {@code null}
     * @return the original long if non-null; otherwise {@code 0L}
     */
    public static long nullAsZero(Long value) {
        return nullAsDefault(value, 0L);
    }

    /**
     * Parses a string as a {@code long}, or returns zero if the input is {@code null} or unparseable.
     * <p>
     * If the string cannot be parsed as a {@code long} (e.g., contains non-numeric characters),
     * the method catches the {@link NumberFormatException} and returns zero.
     * </p>
     *
     * @param value the string representation of a number; may be {@code null}
     * @return the parsed long value if parsing succeeds; otherwise {@code 0L}
     */
    public static long nullAsZero(String value) {
        try {
            return (value != null ? Long.parseLong(value) : 0L);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Converts a string representation of a number to {@code BigDecimal}, returning
     * {@link BigDecimal#ZERO} if the input is {@code null} or unparseable.
     * <p>
     * If the string cannot be parsed as a number (e.g. contains invalid decimal syntax),
     * the method catches the {@link NumberFormatException} and returns {@code BigDecimal.ZERO}.
     * </p>
     *
     * @param value the string representation of a decimal number; may be {@code null}
     * @return a {@code BigDecimal} representing the parsed value, or {@code BigDecimal.ZERO}
     * if the input is null or cannot be parsed
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
     * Returns the provided {@code UUID}, or generates a random UUID if the input is {@code null}.
     * <p>
     * Random UUID generation uses {@link UUID#randomUUID()}, which generates a
     * universally unique identifier suitable for most non-cryptographic purposes.
     * </p>
     *
     * @param value the UUID to be checked; may be {@code null}
     * @return the original UUID if non-null; otherwise a newly generated random UUID
     * @see UUID#randomUUID()
     */
    public static UUID nullAsRandomUuid(UUID value) {
        return nullAsDefault(value, UUID.randomUUID());
    }

    /**
     * Returns the provided list, or an empty list if the input is {@code null}.
     * <p>
     * The empty list returned for {@code null} input is a new {@link ArrayList} instance.
     * </p>
     *
     * @param <T>   the element type of the list
     * @param value the list to be checked; may be {@code null}
     * @return the original list if non-null; otherwise a new empty {@code ArrayList}
     */
    public static <T> List<T> nullAsEmptyList(List<T> value) {
        return nullAsDefault(value, new ArrayList<>());
    }

    // TODO: Javadoc
    // TODO: tests
    public static <T> T nullAsDefault(T value, T defaultValue) {
        return (value != null ? value : defaultValue);
    }

    // TODO: Javadoc
    // TODO: tests
    public static <T> String nullAsDefaultString(T value, T defaultValue) {
        String defaultString = ((defaultValue != null) ? defaultValue.toString() : null);
        return ((value != null) ? value.toString() : defaultString);
    }
}
