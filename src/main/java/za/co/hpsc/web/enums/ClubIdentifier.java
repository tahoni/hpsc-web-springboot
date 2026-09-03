package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing the practical shooting clubs recognised by the HPSC platform.
 *
 * <p>
 * Each club is associated with a full name, an abbreviation and a code for cross-referencing
 * with external systems. {@code VISITOR} represents a competitor visiting from outside the
 * recognised clubs, and {@code UNKNOWN} is the default constant used when a club could not
 * be matched.
 * </p>
 *
 * @since 5.0.0
 */
@Getter
public enum ClubIdentifier {
    SOSC("Safari Outdoor Shooting Club", "SOSC", "AAA"),
    HPSC("Hartbeespoortdam Practical Shooting Club", "HPSC", "BBB"),
    PMPSC("Pretoria Military Practical Shooting Club", "PMPSC", "CCC"),
    VISITOR("Visitor", "V", "UUU"),
    ALL("Eufees Clubs", "All", "ALL"),
    UNKNOWN;

    private final String name;
    private final String abbreviation;
    private final String code;

    ClubIdentifier() {
        this.name = "";
        this.abbreviation = "";
        this.code = "";
    }

    ClubIdentifier(String name, String abbreviation, String code) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.code = code;
    }

    /**
     * Retrieves an optional {@code ClubIdentifier} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a club with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the club to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubIdentifier} if found,
     * or empty otherwise.
     */
    public static Optional<ClubIdentifier> fromName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubReference -> clubReference.name.equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code ClubIdentifier} instance based on the provided abbreviation.
     *
     * <p>
     * The method performs a case-insensitive match to find a club with the given abbreviation.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param abbreviation the abbreviation of the club to search for.
     *                     Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubIdentifier} if found,
     * or empty otherwise.
     * @since 5.4.0
     */
    public static Optional<ClubIdentifier> fromAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubReference -> clubReference.abbreviation.equalsIgnoreCase(abbreviation))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code ClubIdentifier} instance based on the provided code.
     *
     * <p>
     * The method searches for a club with a code matching the provided input.
     * If no match is found, an empty {@code Optional} is returned.
     * </p>
     *
     * @param code the code of the club to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubIdentifier} if found,
     * or empty otherwise.
     */
    public static Optional<ClubIdentifier> fromCode(String code) {
        if ((code == null) || (code.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubIdentifier -> clubIdentifier.code.equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name + " (" + this.abbreviation + ")";
    }
}
