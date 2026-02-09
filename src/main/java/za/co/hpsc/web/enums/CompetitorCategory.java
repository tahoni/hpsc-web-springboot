package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing various categories of competitors.
 *
 * <p>
 * Each category is associated with a display name that represents its descriptive value.
 * The enum provides utility methods for retrieving a specific category by its name.
 * If no match is found, the default category is {@code NONE}.
 * </p>
 */
@Getter
public enum CompetitorCategory {
    JUNIOR("Junior", 2),
    SUPER_JUNIOR("Super Junior", 5),
    LADY("Lady", 1),
    SUPER_LADY("Lady, Senior", 7),
    SENIOR("Senior", 3),
    SUPER_SENIOR("Super Senior", 4),
    GRAND_SENIOR("Grand Senior", 6),
    NONE;

    private final String name;
    private final int code;

    CompetitorCategory() {
        this.name = "";
        this.code = 0;
    }

    CompetitorCategory(String name, int code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Retrieves an optional {@code CompetitorCategory} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a division with the given name.
     * If no match is found or the input is null/blank, a {@link CompetitorCategory#NONE} is returned.
     * </p>
     *
     * @param name the name of the division to search for.
     *             Can be null or empty.
     * @return an {@code Optional} containing the matching {@code CompetitorCategory} if found,
     * or {@link CompetitorCategory#NONE} otherwise.
     */
    public static Optional<CompetitorCategory> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.of(NONE);
        }

        Optional<CompetitorCategory> optionalCompetitorCategory = Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.isNameMatch(name))
                .findFirst();
        return optionalCompetitorCategory.isPresent() ? optionalCompetitorCategory : Optional.of(NONE);
    }

    /**
     * Retrieves an optional {@code CompetitorCategory} instance based on the provided code.
     *
     * <p>
     * The method searches for a category with a code matching the provided input.
     * If no match is found, an {@link CompetitorCategory#NONE} is returned.
     * </p>
     *
     * @param code the code of the division to search for.
     *             The code can be {@code null} or negative, in which case
     *             a {@link CompetitorCategory#NONE}} is returned.
     * @return an {@code Optional} containing the matching {@code Division} if found,
     * or {@link CompetitorCategory#NONE} otherwise.
     */
    // TODO: test
    public static Optional<CompetitorCategory> getByCode(Integer code) {
        if ((code == null) || (code == 0)) {
            return Optional.of(NONE);
        }

        Optional<CompetitorCategory> optionalCompetitorCategory = Arrays.stream(CompetitorCategory.values())
                .filter(category -> code.equals(category.getCode()))
                .findFirst();
        return optionalCompetitorCategory.isPresent() ? optionalCompetitorCategory : Optional.of(NONE);
    }

    @Override
    public String toString() {
        return this.name;
    }

    private boolean isNameMatch(String name) {
        // Checks for an exact match without any separators
        return trimName(this.name).equalsIgnoreCase(trimName(name));
    }

    private String trimName(String name) {
        // Removes all whitespace characters
        return name.replaceAll("\\s", "");
    }
}
