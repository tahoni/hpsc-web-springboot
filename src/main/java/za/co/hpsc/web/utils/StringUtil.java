package za.co.hpsc.web.utils;

import java.util.Map;

public class StringUtil {
    public static String formatStringWithNamedParameters(String template, Map<String, String> parameters) {
        String result = template;
        if (parameters != null) {
            // Replace each placeholder in the template with the corresponding value from the map
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                result = result.replace(placeholder, entry.getValue());
            }
        }
        return template;
    }
}
