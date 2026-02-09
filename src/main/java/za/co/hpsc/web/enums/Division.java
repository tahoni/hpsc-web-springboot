package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing different divisions in sports shooting.
 *
 * <p>
 * A division defines a specific type of competition or category
 * in which participants can compete.
 * Each division is associated with a name and may optionally include
 * an abbreviation for easier reference.
 * </p>
 */
@Getter
public enum Division {
    // Handgun Divisions
    OPEN("Open Division", "O", 1),
    STANDARD("Standard Division", "S", 2),
    MODIFIED("Modified Division", "", 3),
    CLASSIC("Classic Division", "C", 18),
    PRODUCTION("Production Division", "P", 4),
    PRODUCTION_OPTICS("Production Optics Division", "PO", 24),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light Division", "POL", 28),
    OPTICS("Optics Division", "", 50),
    REVOLVER("Revolver Division", "", 5),

    // Rifle Divisions
    RIFLE_SEMI_AUTO_OPEN("Semi Auto Open Division", 6),
    RIFLE_SEMI_AUTO_STANDARD("Semi Auto Standard Division", 8),
    RIFLE_MANUAL_ACTION_CONTEMPORARY("Manual Action Contemporary Division", 33),
    RIFLE_MANUAL_ACTION_BOLT("Manual Action Bolt Division", 34),

    // Shotgun Divisions
    SHOTGUN_OPEN("Open Division", 10),
    SHOTGUN_MODIFIED("Modified Division", 13),
    SHOTGUN_STANDARD("Standard Division", 11),
    SHOTGUN_STANDARD_MANUAL("Standard Manual Division", 12),

    // PCC Divisions
    PCC_OPTICS("PCC Optic Division", "PCC", 29),
    PCC_IRON("PCC Iron Division", "PCC", 31),

    // .22 Divisions
    OPEN_22("Open Division", 45),
    STANDARD_22("Standard Division", 46),
    CLASSIC_22("Classic Division", 48),
    OPTICS_22("Optics Division", 47),

    // Mini Rifle Divisions
    MINI_RIFLE_OPEN("Open Division", 25),
    MINI_RIFLE_STANDARD("Standard Division", 26);

    private final String name;
    private final String abbreviation;
    private final int code;

    Division(String name, String abbreviation, int code) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.code = code;
    }

    Division(String name, int code) {
        this.name = name;
        this.abbreviation = "";
        this.code = code;
    }

    /**
     * Retrieves an optional {@code Division} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a division with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the division to search for.
     *             Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     */
    public static Optional<Division> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Division.values())
                .filter(division -> division.isNameMatch(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code Division} instance based on the provided abbreviation.
     *
     * <p>
     * The method performs a case-insensitive match to find a division that matches
     * the given abbreviation.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param abbreviation the abbreviation of the division to search for.
     *                     Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     */
    public static Optional<Division> getByAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Division.values())
                .filter(division -> division.isAbbreviationMatch(abbreviation))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code Division} instance based on the provided abbreviation or name.
     *
     * <p>
     * The method attempts to find a division where the provided value matches either the name
     * or the abbreviation of the division. The matching is case-insensitive. If no match is
     * found or if the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param value the value to search for, which can either be a division's name or abbreviation.
     *              Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty if no match is found.
     */
    public static Optional<Division> getByAbbreviationOrName(String value) {
        if ((value == null) || (value.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Division.values())
                .filter(division -> division.isNameMatch(value) ||
                        division.isAbbreviationMatch(value))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code Division} instance based on the provided code.
     *
     * <p>
     * The method searches for a division with a code matching the provided input.
     * If no match is found, an empty {@code Optional} is returned.
     * </p>
     *
     * @param code the code of the division to search for.
     *             The code can be {@code null} or negative, in which case
     *             an empty {@code Optional} is returned.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     */
    public static Optional<Division> getByCode(Integer code) {
        if ((code == null) || (code <= 0)) {
            return Optional.empty();
        }

        return Arrays.stream(Division.values())
                .filter(division -> code.equals(division.getCode()))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }

    private boolean isNameMatch(String name) {
        // Checks for an exact match
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }
        // Checks for a match starting with
        return this.name.startsWith(name);
    }

    private boolean isAbbreviationMatch(String abbreviation) {
        // Checks for an exact match
        return this.abbreviation.equalsIgnoreCase(abbreviation);
    }
}
