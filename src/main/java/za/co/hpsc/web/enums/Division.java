package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing different divisions in the sport of shooting.
 *
 * <p>
 * A division categorises a specific type of firearm or shooting discipline,
 * enabling classification of participants based on their equipment.
 * Each division is associated with one or more names that can be used
 * to identify it. The names are normalised during comparison to ensure
 * case- and separator-insensitive matching.
 */
@Getter
public enum Division {
    HANDGUN("Handgun"),
    PCC(List.of("PCC", "Pistol Caliber Carbine")),
    SHOTGUN("Shotgun"),
    RIFLE("Rifle"),
    HANDGUN_22(List.of("Handgun .22", "Handgun .22LR", "22", ".22LR")),
    MINI_RIFLE("Mini Rifle"),
    NONE;

    private final List<String> names;

    private static final String DEFAULT_SEPARATOR = " ";
    private static final String ALTERNATE_SEPARATOR = "-";

    Division() {
        this.names = List.of();
    }

    Division(String name) {
        this.names = List.of(name);
    }

    Division(List<String> names) {
        this.names = names;
    }

    /**
     * Retrieves an optional {@code Division} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a division with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the division to search for. Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     */
    public static Optional<Division> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Stream.of(Division.values())
                .filter(division -> division.isNameMatch(name))
                .findFirst();
    }

    /**
     * Retrieves the primary display name associated with this division.
     *
     * @return the first name in the list of names representing the division
     */
    public String getDisplayName() {
        return this.names.getFirst();
    }

    private boolean isNameMatch(String name) {
        // Checks for a match without separators
        return this.names.stream()
                .anyMatch(divisionName -> divisionName.equalsIgnoreCase(normaliseName(name)));
    }

    private String normaliseName(String name) {
        // Normalises the name by replacing any separator characters with a space
        return name.replace(ALTERNATE_SEPARATOR, DEFAULT_SEPARATOR);
    }
}
