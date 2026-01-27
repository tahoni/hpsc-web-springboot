package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enum representing various categories of competitors.
 * Each category is associated with a display name that represents its descriptive value.
 * The enum provides utility methods for retrieving a specific category by its name.
 * If no match is found, the default category is {@code NONE}.
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

    public static CompetitorCategory getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return NONE;
        }

        return Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(NONE);
    }


    public String getDisplayName() {
        return this.name;
    }

    private boolean isNameMatch(String name) {
        // Checks for exact match without any separators
        return trimName(this.name).equalsIgnoreCase(trimName(name));
    }

    private String trimName(String name) {
        return name.replace("\\s", "");
    }
}
