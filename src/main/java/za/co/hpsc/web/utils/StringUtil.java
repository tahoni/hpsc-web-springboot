package za.co.hpsc.web.utils;

import java.util.Map;

/**
 * Utility class for string operations.
 *
 * <p>
 * The {@code StringUtil} class offers static methods for common string-related tasks. These methods
 * are designed to handle various use cases where string manipulation is required.
 * </p>
 */
public final class StringUtil {
    private StringUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Formats a template string by replacing placeholders with corresponding values from a map.
     *
     * <p>
     * The template string can contain placeholders denoted by ${parameterName}, where "parameterName"
     * corresponds to a key in the provided map. Each placeholder is replaced by the value associated
     * with the key in the map. If the map is null or a placeholder's key does not exist in the map,
     * the placeholder remains unchanged in the resulting string.
     * </p>
     *
     * @param template   the template string containing placeholders in the format ${parameterName}.
     *                   Must not be null.
     * @param parameters a map containing key-value pairs where keys correspond to placeholder names
     *                   and values correspond to their replacements.
     *                   Can be null.
     * @return a formatted string with placeholders replaced by corresponding values from the map, or
     * the original template if no replacements are made.
     */
    public static String formatStringWithNamedParameters(String template, Map<String, String> parameters) {
        String result = template;
        if (parameters != null) {
            // Replace each placeholder in the template with the corresponding value from the map
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                result = result.replace(placeholder, entry.getValue());
            }
        }
        return result;
    }

    /**
     * Converts an object to its string representation.
     *
     * @param object the object to be converted to a string.
     * @return the string representation of the object, or null if the input object is null.
     */
    public static String toString(Object object) {
        if (object == null) {
            return null;
        }

        return object.toString();
    }
}
