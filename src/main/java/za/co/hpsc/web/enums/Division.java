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
 * Each division is associated with a name, which is unique across all divisions, and the
 * {@link FirearmType} it is shot with.
 * </p>
 *
 * @since 1.1.3
 */
@Getter
public enum Division {
    // Handgun Divisions
    OPEN("Open Division", FirearmType.HANDGUN),
    STANDARD("Standard Division", FirearmType.HANDGUN),
    MODIFIED("Modified Division", FirearmType.HANDGUN),
    CLASSIC("Classic Division", FirearmType.HANDGUN),
    PRODUCTION("Production Division", FirearmType.HANDGUN),
    PRODUCTION_OPTICS("Production Optics Division", FirearmType.HANDGUN),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light Division", FirearmType.HANDGUN),
    OPTICS("Optics Division", FirearmType.HANDGUN),
    REVOLVER("Revolver Division", FirearmType.HANDGUN),

    // Rifle Divisions
    RIFLE_SEMI_AUTO_OPEN("Rifle Open Division", FirearmType.RIFLE),
    RIFLE_SEMI_AUTO_STANDARD("Rifle Standard Division", FirearmType.RIFLE),
    RIFLE_STANDARD_MANUAL("Rifle Standard Manual Division", FirearmType.RIFLE),

    // Shotgun Divisions
    SHOTGUN_OPEN("Shotgun Open Division", FirearmType.SHOTGUN),
    SHOTGUN_MODIFIED("Shotgun Modified Division", FirearmType.SHOTGUN),
    SHOTGUN_STANDARD("Shotgun Semi Division", FirearmType.SHOTGUN),
    SHOTGUN_STANDARD_MANUAL("Standard Manual Division", FirearmType.SHOTGUN),

    // PCC Divisions
    PCC_OPTICS("PCC Optic Division", FirearmType.PCC),
    PCC_IRON("PCC Iron Division", FirearmType.PCC),

    // .22 Divisions
    OPEN_22(".22 Open Division", FirearmType.HANDGUN_22),
    STANDARD_22(".22 Standard Division", FirearmType.HANDGUN_22),
    CLASSIC_22(".22 Classic Division", FirearmType.HANDGUN_22),
    OPTICS_22(".22 Optics Division", FirearmType.HANDGUN_22),

    // Mini Rifle Divisions
    MINI_RIFLE_OPEN("Mini Rifle Open Division", FirearmType.MINI_RIFLE),
    MINI_RIFLE_STANDARD("Mini Rifle Standard Division", FirearmType.MINI_RIFLE);

    private final String name;
    private final FirearmType firearmType;

    Division(String name, FirearmType firearmType) {
        this.name = name;
        this.firearmType = firearmType;
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
