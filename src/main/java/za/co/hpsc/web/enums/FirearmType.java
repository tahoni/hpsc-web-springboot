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
public enum FirearmType {
    HANDGUN("Handgun", 1),
    PCC(List.of("PCC", "Pistol Caliber Carbine"), 7),
    SHOTGUN("Shotgun", 3),
    RIFLE("Rifle", 2),
    HANDGUN_22(List.of("Handgun .22", "Handgun .22LR", "22", ".22LR"), 10),
    MINI_RIFLE("Mini Rifle", 6);

    private final List<String> names;
    private final int code;

    private static final String DEFAULT_SEPARATOR = " ";
    private static final String ALTERNATE_SEPARATOR = "-";

    FirearmType() {
        this.names = List.of();
        this.code = 0;
    }

    FirearmType(String name, int code) {
        this.names = List.of(name);
        this.code = code;
    }

    FirearmType(List<String> names, int code) {
        this.names = names;
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
     * @param name the name of the division to search for. Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     */
    public static Optional<FirearmType> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Stream.of(FirearmType.values())
                .filter(division -> division.isNameMatch(name))
                .findFirst();
    }

    public static Optional<FirearmType> getByCode(int code) {
        if (code == 0) {
            return Optional.empty();
        }

        return Stream.of(FirearmType.values())
                .filter(division -> division.getCode() == code)
                .findFirst();
    }

    @Override
    public String toString() {
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
