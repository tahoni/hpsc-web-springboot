package za.co.hpsc.web.utils;

import java.time.LocalDateTime;

public final class DateUtil {
    private DateUtil() {
        // Utility class, not to be instantiated
    }

    // TODO: Javadoc
    public static LocalDateTime calculateDateCreated(LocalDateTime dateCreatedValue) {
        return ((dateCreatedValue != null) ? dateCreatedValue : LocalDateTime.now());
    }
}
