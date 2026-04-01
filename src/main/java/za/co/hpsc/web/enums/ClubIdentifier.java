package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ClubIdentifier {
    SOSC("Safari Outdoor Shooting Club", "SOSC", "AAA"),
    HPSC("Hartbeespoortdam Practical Shooting Club", "HPSC", "BBB"),
    PMPSC("Pretoria Military Practical Shooting Club", "PMPSC", "CCC"),
    VISITOR("Visitor", "V", "UUU"),
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
     * Retrieves an optional {@code ClubReference} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a club reference with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the club reference to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubReference} if found,
     * or empty otherwise.
     */
    public static Optional<ClubIdentifier> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubReference -> clubReference.name.equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code ClubReference} instance based on the provided abbreviation.
     *
     * <p>
     * The method performs a case-insensitive match to find a club reference with the given abbreviation.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param abbreviation the abbreviation of the club reference to search for.
     *                     Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubReference} if found,
     * or empty otherwise.
     */
    public static Optional<ClubIdentifier> getByAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubReference -> clubReference.abbreviation.equalsIgnoreCase(abbreviation))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code ClubReference} instance based on the provided code.
     *
     * <p>
     * The method searches for a club reference with a code matching the provided input.
     * If no match is found, an empty {@code Optional} is returned.
     * </p>
     *
     * @param code the code of the club reference to search for.
     *             The code can be {@code null} or negative, in which case
     *             an empty {@code Optional} is returned.
     * @return an {@code Optional} containing the matching {@code ClubReference} if found,
     * or empty otherwise.
     */
    public static Optional<ClubIdentifier> getByCode(String code) {
        if ((code == null) || (code.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubIdentifier.values())
                .filter(clubReference -> clubReference.code.equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
