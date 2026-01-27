package za.co.hpsc.web.utils;

import java.util.Map;

/**
 * Utility class for string operations.
 * <p>
 * The {@code StringUtil} class provides a method to format strings using named
 * parameters. Placeholders in the template string, denoted by ${parameterName},
 * are replaced with corresponding values from the provided map.
 */
public class StringUtil {
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
     *                   and values correspond to their replacements. Can be null.
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
}
