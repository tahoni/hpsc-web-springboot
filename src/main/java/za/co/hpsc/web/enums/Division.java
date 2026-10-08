package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

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
    OPEN("Open", FirearmType.HANDGUN),
    STANDARD("Standard", FirearmType.HANDGUN),
    MODIFIED("Modified", FirearmType.HANDGUN),
    CLASSIC("Classic", FirearmType.HANDGUN),
    PRODUCTION("Production", FirearmType.HANDGUN),
    PRODUCTION_OPTICS("Production Optics", FirearmType.HANDGUN),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light", FirearmType.HANDGUN),
    OPTICS("Optics", FirearmType.HANDGUN),
    REVOLVER("Revolver", FirearmType.HANDGUN),

    // Rifle Divisions
    RIFLE_SEMI_AUTO_OPEN("Rifle Open", FirearmType.RIFLE),
    RIFLE_SEMI_AUTO_STANDARD("Rifle Standard", FirearmType.RIFLE),
    RIFLE_STANDARD_MANUAL("Rifle Standard Manual", FirearmType.RIFLE),

    // Shotgun Divisions
    SHOTGUN_OPEN("Shotgun Open", FirearmType.SHOTGUN),
    SHOTGUN_MODIFIED("Shotgun Modified", FirearmType.SHOTGUN),
    SHOTGUN_STANDARD("Shotgun Semi", FirearmType.SHOTGUN),
    SHOTGUN_STANDARD_MANUAL("Shotgun Standard Manual", FirearmType.SHOTGUN),

    // PCC Divisions
    PCC_OPTICS("PCC Optic", FirearmType.PCC),
    PCC_IRON("PCC Iron", FirearmType.PCC),

    // .22 Divisions
    OPEN_22(".22 Open", FirearmType.HANDGUN_22),
    STANDARD_22(".22 Standard", FirearmType.HANDGUN_22),
    CLASSIC_22(".22 Classic", FirearmType.HANDGUN_22),
    OPTICS_22(".22 Optics", FirearmType.HANDGUN_22),

    // Mini Rifle Divisions
    MINI_RIFLE_OPEN("Mini Rifle Open", FirearmType.MINI_RIFLE),
    MINI_RIFLE_STANDARD("Mini Rifle Standard", FirearmType.MINI_RIFLE);

    /** The " Division" suffix that division names used to carry, still accepted when looking a division up. */
    private static final Pattern LEGACY_SUFFIX = Pattern.compile("(?i)\s+division$");

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
     * A trailing {@code " Division"} on the name is ignored, so the legacy form (e.g. {@code "Open Division"}) still
     * matches. If no match is found or the input is null/blank, an empty {@code Optional} is returned.
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

        String trimmedName = LEGACY_SUFFIX.matcher(name.trim()).replaceFirst("");
        return Arrays.stream(Division.values())
                .filter(division -> division.isNameMatch(trimmedName))
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
