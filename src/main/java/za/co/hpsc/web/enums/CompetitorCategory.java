package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;

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
    JUNIOR("Junior"),
    SUPER_JUNIOR("Super Junior"),
    LADY("Lady"),
    SUPER_LADY("Lady, Senior"),
    JUNIOR_LADY("Lady, Junior"),
    SENIOR("Senior"),
    SUPER_SENIOR("Super Senior"),
    GRAND_SENIOR("Grand Senior"),
    NONE;

    private final String name;

    CompetitorCategory() {
        this.name = "";
    }

    CompetitorCategory(String name) {
        this.name = name;
    }

    /**
     * Retrieves the corresponding {@code CompetitorCategory} for the given name.
     * If the provided name is null, blank, or does not match any category,
     * the method returns {@code NONE}.
     *
     * @param name the name of the category to retrieve, case-insensitive
     * @return the {@code CompetitorCategory} matching the given name, or {@code NONE}
     * if no match is found
     */
    public static CompetitorCategory getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return NONE;
        }

        return Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.isNameMatch(name))
                .findFirst()
                .orElse(NONE);
    }


    /**
     * Retrieves the display name associated with the current enum constant.
     * If the display name is not explicitly defined, an empty string will be returned.
     *
     * @return the display name of the enum constant, or an empty string if not defined
     */
    public String getDisplayName() {
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
