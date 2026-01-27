package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing different divisions in the sport of shooting.
 * A division categorizes a specific type of firearm or shooting discipline,
 * enabling classification of participants based on their equipment.
 * <p>
 * Each division is associated with one or more names that can be used
 * to identify it. The names are normalized during comparison to ensure
 * case- and separator-insensitive matching.
 */
@Getter
public enum Division {
    HANDGUN("Handgun"),
    PCC(List.of("PCC", "Pistol Caliber Carbine")),
    SHOTGUN("Shotgun"),
    RIFLE("Rifle"),
    HANDGUN_22(List.of("Handgun .22", "Handgun .22LR", "22", ".22LR")),
    MINI_RIFLE("Mini Rifle");

    private final List<String> names;

    private static final String DEFAULT_SEPARATOR = " ";
    private static final String ALTERNATE_SEPARATOR = "-";

    Division(String name) {
        this.names = List.of(name);
    }

    Division(List<String> names) {
        this.names = names;
    }

    public static Optional<Division> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Stream.of(Division.values())
                .filter(division -> division.isNameMatch(name))
                .findFirst();
    }

    public String getDisplayName() {
        return this.names.getFirst();
    }

    private boolean isNameMatch(String name) {
        // Checks for match without separators
        return this.names.stream()
                .anyMatch(divisionName -> divisionName.equalsIgnoreCase(normaliseName(name)));
    }

    private String normaliseName(String name) {
        return name.replace(ALTERNATE_SEPARATOR, DEFAULT_SEPARATOR);
    }
}
