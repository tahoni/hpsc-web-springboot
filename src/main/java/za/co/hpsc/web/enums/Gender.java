package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing the gender of a competitor.
 *
 * <p>
 * Each gender is associated with a display name and an abbreviation for
 * easy reference and presentation.
 * </p>
 */
@Getter
public enum Gender {
    Male("Male", "M"),
    Female("Female", "F");

    private final String name;
    private final String abbreviation;

    Gender(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Retrieves an optional {@code Gender} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a gender with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the gender to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code Gender} if found,
     * or empty otherwise.
     */
    public static Optional<Gender> fromName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Gender.values())
                .filter(gender -> gender.name.equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
