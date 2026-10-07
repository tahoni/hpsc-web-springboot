package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Enum representing different power factors in the context of shooting sports.
 *
 * <p>
 * A power factor defines the level of energy a bullet carries, which is used
 * to categorise participants based on the calibre and velocity of ammunition used.
 * Each power factor is associated with a name for easy reference and presentation.
 * </p>
 *
 * @since 1.1.3
 */
@Getter
@AllArgsConstructor
public enum PowerFactor {
    MINOR("Minor"),
    MAJOR("Major");

    private final String name;

    /**
     * Retrieves an optional {@code PowerFactor} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive search to find a matching power factor
     * by its name. If the input is null, empty or no match is found, an empty
     * {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the power factor to search for.
     *             Can be null or empty.
     * @return an {@code Optional} containing the matching {@code PowerFactor} if found,
     * or an empty {@code Optional} otherwise.
     * @since 1.1.3
     */
    public static Optional<PowerFactor> fromName(String name) {
        if (!hasText(name)) {
            return Optional.empty();
        }

        return Arrays.stream(PowerFactor.values())
                .filter(powerFactor -> powerFactor.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
