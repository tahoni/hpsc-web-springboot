package za.co.hpsc.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class providing methods to handle nullable values by substituting default values.
 * <p>
 * The ValueUtil class offers static methods that help in replacing null references with
 * suitable defaults. These methods are designed to simplify null-handling by providing
 * straightforward alternatives when encountering nulls in common scenarios, such as
 * Strings, UUIDs, and collections.
 */
public class ValueUtil {
    /**
     * Returns the provided string if it is not null; otherwise, returns an empty string.
     *
     * @param value the string to be checked; may be null.
     * @return the original string if it is not null, or an empty string if it is null.
     */
    public static String nullAsEmptyString(String value) {
        return (value != null ? value : "");
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
