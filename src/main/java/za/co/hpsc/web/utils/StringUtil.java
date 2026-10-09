package za.co.hpsc.web.utils;

import org.apache.commons.text.WordUtils;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for string operations.
 *
 * <p>
 * The {@code StringUtil} class offers static methods for common string-related tasks. These methods
 * are designed to handle various use cases where string manipulation is required.
 * </p>
 *
 * @since 1.1.3
 */
public final class StringUtil {
    private StringUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Converts an object to its string representation.
     *
     * @param object the object to be converted to a string.
     * @return the string representation of the object, or null if the input object is null.
     * @since 4.1.0
     */
    public static @Nullable String toString(Object object) {
        if (object == null) {
            return null;
        }

        return object.toString();
    }

    /**
     * Converts a string to proper case: the first letter of each word upper case, the rest lower
     * case. Words are delimited by whitespace, hyphens and apostrophes (straight or curly, as spreadsheets often
     * produce), so {@code "o'NEIL-smith"} becomes {@code "O'Neil-Smith"}.
     *
     * @param value the string to convert; may be null.
     * @return the proper-cased string, or null if {@code value} is null.
     * @since 8.12.0
     */
    public static @Nullable String toProperCase(String value) {
        if (value == null) {
            return null;
        }

        return WordUtils.capitalizeFully(value, ' ', '\t', '-', '\'', '’');
    }

    /**
     * Checks whether a string contains at least one non-whitespace character.
     *
     * <p>A string made up only of whitespace characters, as defined by {@link String#isBlank()},
     * is treated as having no text.</p>
     *
     * @param value the string to check; may be null.
     * @return {@code true} if {@code value} is not null and not blank, {@code false} otherwise.
     * @since 12.0.0
     */
    public static boolean hasText(String value) {
        return (value != null) && !value.isBlank();
    }
}
