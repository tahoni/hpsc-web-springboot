package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing different disciplines in sports shooting.
 *
 * <p>
 * A discipline defines a specific type of competition or category
 * in which participants can compete.
 * Each discipline is associated with a name and may optionally include
 * an abbreviation for easier reference.
 * </p>
 */
@Getter
public enum Discipline {

    // Handgun Disciplines
    OPEN("Open Division", "O"),
    STANDARD("Standard Division", "S"),
    CLASSIC("Classic Division", "C"),
    PRODUCTION("Production Division", "P"),
    PRODUCTION_OPTICS("Production Optics Division", "PO"),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light Division", "POL"),
    REVOLVER("Revolver Division"),

    // Rifle Disciplines
    RIFLE_SEMI_AUTO_OPEN("Semi Auto Open Division"),
    RIFLE_SEMI_AUTO_STANDARD("Semi Auto Standard Division"),
    RIFLE_MANUAL_ACTION_CONTEMPORARY("Manual Action Contemporary Division"),
    RIFLE_MANUAL_ACTION_BOLT("Manual Action Bolt Division"),

    // Shotgun Disciplines
    SHOTGUN_OPEN("Open Division"),
    SHOTGUN_MODIFIED("Modified Division"),
    SHOTGUN_STANDARD("Standard Division"),
    SHOTGUN_STANDARD_MANUAL("Standard Manual Division"),

    // PCC Disciplines
    PCC_OPTICS("Optics Division", "PCC"),
    PCC_IRON("Iron Division", "PCC"),

    // .22 Disciplines
    OPEN_22("Open Division"),
    STANDARD_22("Standard Division"),
    CLASSIC_22("Classic Division"),

    // Mini Rifle Disciplines
    MINI_RIFLE_OPEN("Open Division"),
    MINI_RIFLE_STANDARD("Standard Division");

    private final String name;
    private final String abbreviation;

    Discipline(String name) {
        this.name = name;
        this.abbreviation = "";
    }

    Discipline(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Retrieves an optional {@code Discipline} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a discipline with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the discipline to search for. Can be null or empty
     * @return an {@code Optional} containing the matching {@code Discipline} if found,
     * or empty otherwise
     */
    public static Optional<Discipline> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isNameMatch(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code Discipline} instance based on the provided abbreviation.
     *
     * <p>
     * The method performs a case-insensitive match to find a discipline that matches
     * the given abbreviation.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param abbreviation the abbreviation of the discipline to search for. Can be null or empty
     * @return an {@code Optional} containing the matching {@code Discipline} if found,
     * or empty otherwise
     */
    public static Optional<Discipline> getByAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isAbbreviationMatch(abbreviation))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code Discipline} instance based on the provided abbreviation or name.
     *
     * <p>
     * The method attempts to find a discipline where the provided value matches either the name
     * or the abbreviation of the discipline. The matching is case-insensitive. If no match is
     * found or if the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param value the value to search for, which can either be a discipline's name or abbreviation.
     *              Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Discipline} if found,
     * or empty if no match is found.
     */
    public static Optional<Discipline> getByAbbreviationOrName(String value) {
        if ((value == null) || (value.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isNameMatch(value) ||
                        discipline.isAbbreviationMatch(value))
                .findFirst();
    }

    /**
     * Retrieves the display name of the discipline.
     *
     * @return the display name associated with this discipline
     */
    public String getDisplayName() {
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
