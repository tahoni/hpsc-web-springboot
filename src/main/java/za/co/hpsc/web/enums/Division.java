package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Enum representing different divisions in sports shooting.
 *
 * <p>
 * A division defines a specific type of competition or category
 * in which participants can compete.
 * Each division is associated with a name.
 * </p>
 *
 * @since 1.1.3
 */
@Getter
public enum Division {
    // Handgun Divisions
    OPEN("Open Division"),
    STANDARD("Standard Division"),
    MODIFIED("Modified Division"),
    CLASSIC("Classic Division"),
    PRODUCTION("Production Division"),
    PRODUCTION_OPTICS("Production Optics Division"),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light Division"),
    OPTICS("Optics Division"),
    REVOLVER("Revolver Division"),

    // Rifle Divisions
    RIFLE_SEMI_AUTO_OPEN("Semi Auto Open Division"),
    RIFLE_SEMI_AUTO_STANDARD("Semi Auto Standard Division"),
    RIFLE_MANUAL_ACTION_CONTEMPORARY("Manual Action Contemporary Division"),
    RIFLE_MANUAL_ACTION_BOLT("Manual Action Bolt Division"),

    // Shotgun Divisions
    SHOTGUN_OPEN("Open Division"),
    SHOTGUN_MODIFIED("Modified Division"),
    SHOTGUN_STANDARD("Standard Division"),
    SHOTGUN_STANDARD_MANUAL("Standard Manual Division"),

    // PCC Divisions
    PCC_OPTICS("PCC Optic Division"),
    PCC_IRON("PCC Iron Division"),

    // .22 Divisions
    OPEN_22("Open Division"),
    STANDARD_22("Standard Division"),
    CLASSIC_22("Classic Division"),
    OPTICS_22("Optics Division"),

    // Mini Rifle Divisions
    MINI_RIFLE_OPEN("Open Division"),
    MINI_RIFLE_STANDARD("Standard Division");

    private final String name;

    Division(String name) {
        this.name = name;
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
     * @since 1.1.3
     */
    public static Optional<Division> fromName(String name) {
        if (!hasText(name)) {
            return Optional.empty();
        }

        return Arrays.stream(Division.values())
                .filter(division -> division.isNameMatch(name))
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
}
