package za.co.hpsc.web.helpers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods for normalising competitor details.
 *
 * @since 8.12.0
 */
public final class CompetitorHelpers {
    /** Surname particles written in lower case when they precede the surname proper (e.g. "van der Merwe"). */
    private static final List<String> PREFIX_LAST_NAME = List.of(
            "da", "de", "del", "den", "der", "des", "du", "la", "le", "ten", "ter", "van", "von"
    );

    /** Words in a last name, delimited by spaces and hyphens. */
    private static final Pattern WORD = Pattern.compile("[^ \\-]+");

    /** Gaelic "Mc" prefix (e.g. "McDonald"), excluding "Mch" and "Mcu", which start Zulu surnames (e.g. "Mchunu"). */
    private static final Pattern MC_PREFIX = Pattern.compile("(?i)^mc(?![hu])[a-z].*");

    private CompetitorHelpers() {
        // Helper class, not to be instantiated
    }

    /**
     * Applies surname casing conventions to a last name: lower-cases particles such as the "van der" in
     * "Van Der Merwe" or the "du" in "Du Plessis", and capitalises the letter after a Gaelic "Mc" prefix, so
     * "Mcdonald" becomes "McDonald".
     *
     * <p>
     * Words are delimited by spaces and hyphens, so the particle in "Smith-Van Der Merwe" is found too. Only whole
     * words are matched, so a surname that merely starts with a particle (e.g. "Dube", "Vanderbilt") is left alone.
     * The final word is always the surname proper and is never lower-cased, even if it matches a particle (e.g.
     * "Van" on its own). "Mch" and "Mcu" words are left alone, as they start Zulu surnames such as "Mchunu".
     * </p>
     *
     * @param value the last name to convert, typically already proper-cased; may be null.
     * @return the last name with its particles in lower case and its "Mc" prefixes corrected, or null if
     * {@code value} is null.
     * @since 8.12.0
     */
    public static String toSentenceCaseLastName(String value) {
        if (value == null) {
            return null;
        }

        int lastWordEnd = lastWordEnd(value);
        Matcher matcher = WORD.matcher(value);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            String word = matcher.group();
            boolean isLastWord = matcher.end() == lastWordEnd;
            result.append(value, lastEnd, matcher.start())
                    .append(convertWord(word, isLastWord));
            lastEnd = matcher.end();
        }
        return result.append(value.substring(lastEnd)).toString();
    }

    private static int lastWordEnd(String value) {
        Matcher matcher = WORD.matcher(value);
        int end = -1;
        while (matcher.find()) {
            end = matcher.end();
        }
        return end;
    }

    private static String convertWord(String word, boolean isLastWord) {
        if (!isLastWord && isPrefix(word)) {
            return word.toLowerCase();
        }
        if (MC_PREFIX.matcher(word).matches()) {
            return "Mc" + Character.toUpperCase(word.charAt(2)) + word.substring(3);
        }
        return word;
    }

    private static boolean isPrefix(String word) {
        return PREFIX_LAST_NAME.contains(word.toLowerCase());
    }
}
