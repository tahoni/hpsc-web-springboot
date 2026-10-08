package za.co.hpsc.web.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import za.co.hpsc.web.constants.IpscConstants;

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
    private static final Pattern MC_PREFIX = Pattern.compile("(?i)mc(?![hu])[a-z]");

    /** A leading position of one or more digits, followed by an optional whitespace character and a hyphen. */
    private static final Pattern POSITION_PREFIX = Pattern.compile("^\\d++\\s?+-");

    /** A range officer marker, as matched by {@link IpscConstants#REPLACE_IN_NAMES_REGEX}. */
    private static final Pattern RANGE_OFFICER_MARKER = Pattern.compile(IpscConstants.REPLACE_IN_NAMES_REGEX);

    /** A run of two or more whitespace characters, matched possessively so it is never backtracked into. */
    private static final Pattern WHITESPACE = Pattern.compile("\\s{2,}+");

    private CompetitorHelpers() {
        // Helper class, not to be instantiated
    }

    /**
     * Cleans a competitor's full name, for matching against a stored competitor, which may be prefixed with a
     * position (e.g. {@code "1 - John Smith"}).
     *
     * <p>
     * A leading number at the very start of the name, followed by a hyphen with at most one whitespace character
     * before it, is treated as a position and removed, so {@code "1 - Smith-Jones"} resolves to
     * {@code "Smith-Jones"}, and a value without such a prefix, such as {@code "Smith-Jones"}, keeps its hyphens. Any
     * {@code RO} or {@code (RO)} range officer marker (see {@link IpscConstants#REPLACE_IN_NAMES_REGEX}) is removed
     * wherever it appears and so are all full stops, then runs of whitespace are replaced with a single space and the
     * result is trimmed.
     * </p>
     *
     * @param competitorName the competitor's full name, optionally prefixed with a position, may be null.
     * @return the cleaned competitor name, or an empty string if {@code competitorName} is null.
     * @since 13.1.0
     */
    public static String cleanCompetitorName(String competitorName) {
        if (competitorName == null) {
            return "";
        }

        String name = POSITION_PREFIX.matcher(competitorName).replaceAll("");
        name = RANGE_OFFICER_MARKER.matcher(name).replaceAll(" ");
        name = name.replace(".", "");
        return WHITESPACE.matcher(name).replaceAll(" ").trim();
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
     * @param value the last name to convert, typically already proper-cased, may be null.
     * @return the last name with its particles in lower case and its "Mc" prefixes corrected, or an empty string if
     * {@code value} is null.
     * @since 8.12.0
     */
    public static String toSentenceCaseLastName(String value) {
        if (value == null) {
            return "";
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

    /**
     * Converts a competitor number to an integer, for matching against a stored competitor number.
     *
     * <p>
     * Leading and trailing whitespace is ignored. A competitor number that is null, blank or not a valid integer
     * converts to {@code 0}, meaning "no competitor number". The ICS alias numbers listed in
     * {@link IpscConstants#EXCLUDE_ICS_ALIAS} are placeholders rather than real competitor numbers, so they also
     * convert to {@code 0}.
     * </p>
     *
     * @param competitorNumber the competitor number to convert; may be null.
     * @return the competitor number as an integer, or {@code 0} if it is null, blank, not numeric or an excluded
     * ICS alias.
     * @since 12.0.0
     */
    public static int getCompetitorNumberAsInteger(String competitorNumber) {
        String normalisedCompetitorNumber = StringUtils.trimToEmpty(competitorNumber);
        int competitorNumberInt = NumberUtils.toInt(normalisedCompetitorNumber, 0);
        if (IpscConstants.EXCLUDE_ICS_ALIAS.contains(competitorNumberInt)) {
            competitorNumberInt = 0;
        }
        return competitorNumberInt;
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
        if (MC_PREFIX.matcher(word).lookingAt()) {
            return "Mc" + Character.toUpperCase(word.charAt(2)) + word.substring(3);
        }
        return word;
    }

    private static boolean isPrefix(String word) {
        return PREFIX_LAST_NAME.contains(word.toLowerCase());
    }
}
