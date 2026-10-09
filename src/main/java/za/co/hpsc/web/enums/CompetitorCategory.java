package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Enum representing various categories of competitors.
 *
 * <p>
 * Each category is associated with a display name that represents its descriptive value and
 * an abbreviation.
 * The enum provides utility methods for retrieving a specific category by its name.
 * If no match is found, the default category is {@code NONE}.
 * </p>
 *
 * @since 1.1.3
 */
@Getter
public enum CompetitorCategory {
    JUNIOR("Junior", "Jun"),
    SUPER_JUNIOR("Super Junior", "S/Jun"),
    LADY("Lady", "Lady"),
    SENIOR_LADY("Lady Senior", "S/Lady"),
    SENIOR("Senior", "Sen"),
    SUPER_SENIOR("Super Senior", "S/Sen"),
    GRAND_SENIOR("Grand Senior", "G/Sen"),
    NONE;

    private final String name;
    private final String abbreviation;

    CompetitorCategory() {
        this.name = "";
        this.abbreviation = "";
    }

    CompetitorCategory(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Retrieves an optional {@code CompetitorCategory} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a category with the given name.
     * A {@code null} name yields an empty {@code Optional}, and a blank name yields
     * {@link CompetitorCategory#NONE}. A non-blank name that matches no category also yields
     * an empty {@code Optional}.
     * </p>
     *
     * @param name the name of the category to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code CompetitorCategory} if found,
     * an {@code Optional} containing {@link CompetitorCategory#NONE} if the name is blank,
     * or an empty {@code Optional} if the name is {@code null} or matches no category.
     * @since 1.1.3
     */
    public static Optional<CompetitorCategory> fromName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        if (!hasText(name)) {
            return Optional.of(NONE);
        }

        return Arrays.stream(CompetitorCategory.values())
                .filter(competitorCategory -> competitorCategory.isNameMatch(name))
                .findFirst();
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
