package za.co.hpsc.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ValueUtil {
    public static String nullAsEmptyString(String value) {
        return (value != null ? value : "");
    }

    public static UUID nullAsRandomUUID(UUID value) {
        return (value != null ? value : UUID.randomUUID());
    }

    public static <T> List<T> nullAsEmptyList(List<T> value) {
        return (value != null ? value : new ArrayList<>());
    }
}
