package za.co.hpsc.web.helpers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Helper methods for normalising competitor details.
 *
 * @since 8.12.0
 */
public final class CompetitorHelper {
    /** Surname particles written in lower case when they precede the surname proper (e.g. "van der Merwe"). */
    private static final List<String> PREFIX_LAST_NAME = List.of(
            "da", "de", "del", "den", "der", "des", "du", "la", "le", "ten", "ter", "van", "von"
    );

    private CompetitorHelper() {
        // Helper class, not to be instantiated
    }

    /**
     * Lower-cases the particles in a last name, such as the "van der" in "Van Der Merwe" or the "du" in "Du Plessis".
     *
     * <p>
     * Only whole words are matched, so a surname that merely starts with a particle (e.g. "Dube", "Vanderbilt") is
     * left alone. The final word is always the surname proper and is never lower-cased, even if it matches a particle
     * (e.g. "Van" on its own).
     * </p>
     *
     * @param value the last name to convert, typically already proper-cased; may be null.
     * @return the last name with its particles in lower case, or null if {@code value} is null.
     */
    public static String toSentenceCaseLastName(String value) {
        if (value == null) {
            return null;
        }

        String[] words = value.split(" ", -1);
        return IntStream.range(0, words.length)
                .mapToObj(index -> ((index < words.length - 1) && isPrefix(words[index]))
                        ? words[index].toLowerCase()
                        : words[index])
                .collect(Collectors.joining(" "));
    }

    private static boolean isPrefix(String word) {
        return PREFIX_LAST_NAME.contains(word.toLowerCase());
    }
}
