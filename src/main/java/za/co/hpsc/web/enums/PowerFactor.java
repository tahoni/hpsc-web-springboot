package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing different power factors in the context of shooting sports.
 *
 * <p>
 * A power factor defines the level of energy a bullet carries, which is used
 * to categorise participants based on the caliber and velocity of ammunition used.
 * Each power factor is associated with a name and an abbreviation for easy reference and
 * presentation.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum PowerFactor {
    MINOR("Minor", "Min"),
    MAJOR("Major", "Maj");

    private final String name;
    private final String abbreviation;

    /**
     * Retrieves an optional {@code PowerFactor} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive search to find a matching power factor
     * by its name. If the input is null, empty, or no match is found, an empty
     * {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the power factor to search for. Can be null or empty.
     * @return an {@code Optional} containing the matching {@code PowerFactor} if found,
     * or an empty {@code Optional} otherwise.
     */
    public static Optional<PowerFactor> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(PowerFactor.values())
                .filter(powerFactor -> powerFactor.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code PowerFactor} instance based on the provided abbreviation.
     *
     * <p>
     * The method performs a case-insensitive search to find a matching power factor
     * by its abbreviation. If the input is null, empty, or no match is found, an
     * empty {@code Optional} is returned.
     * </p>
     *
     * @param abbreviation the abbreviation of the power factor to search for. Can be null or empty.
     * @return an {@code Optional} containing the matching {@code PowerFactor} if found,
     * or an empty {@code Optional} otherwise.
     */
    public static Optional<PowerFactor> getByAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(PowerFactor.values())
                .filter(powerFactor -> powerFactor.getAbbreviation().equalsIgnoreCase(abbreviation))
                .findFirst();
    }

    /**
     * Retrieves the display name associated with this power factor.
     *
     * @return the name of the power factor.
     */
    public String getDisplayName() {
        return this.name;
    }
}
