package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Enum representing different divisions in the sport of shooting.
 *
 * <p>
 * A division categorises a specific type of firearm or shooting discipline,
 * enabling classification of participants based on their equipment.
 * Each division is associated with one or more names that can be used
 * to identify it. The names are normalised during comparison to ensure
 * case- and separator-insensitive matching.
 *
 * @since 1.1.3
 */
@Getter
public enum FirearmType {
    HANDGUN("Handgun"),
    PCC(List.of("PCC", "Pistol Caliber Carbine")),
    SHOTGUN("Shotgun"),
    RIFLE("Rifle"),
    HANDGUN_22(List.of("Handgun .22", "Handgun .22LR", "22", ".22LR")),
    MINI_RIFLE("Mini Rifle");

    private final List<String> names;

    private static final String DEFAULT_SEPARATOR = " ";
    private static final String ALTERNATE_SEPARATOR = "-";

    FirearmType(String name) {
        this.names = List.of(name);
    }

    FirearmType(List<String> names) {
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
     * @param name the name of the division to search for.
     *             Can be null or empty.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or empty otherwise.
     * @since 1.1.3
     */
    public static Optional<FirearmType> fromName(String name) {
        if (!hasText(name)) {
            return Optional.empty();
        }

        return Stream.of(FirearmType.values())
                .filter(firearmType -> firearmType.isNameMatch(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.names.getFirst();
    }

    private boolean isNameMatch(String name) {
        // Checks for a match without separators
        return this.names.stream()
                .anyMatch(firearmTypeName -> firearmTypeName.equalsIgnoreCase(normaliseName(name)));
    }

    private String normaliseName(String name) {
        // Normalises the name by replacing any separator characters with a space
        return name.replace(ALTERNATE_SEPARATOR, DEFAULT_SEPARATOR);
    }
}
