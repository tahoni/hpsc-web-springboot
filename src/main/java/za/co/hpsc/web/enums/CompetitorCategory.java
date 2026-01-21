package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CompetitorCategory {
    JUNIOR("Junior"),
    SUPER_JUNIOR("Super Junior"),
    LADY("Lady"),
    SUPER_LADY("Lady, Senior"),
    JUNIOR_LADY("Lady, Junior"),
    SENIOR("Senior"),
    SUPER_SENIOR("Super Senior"),
    NONE;

    private final String name;

    CompetitorCategory() {
        this.name = "";
    }

    CompetitorCategory(String name) {
        this.name = name;
    }

    public static CompetitorCategory findByName(String name) {
        return Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(NONE);
    }


    public String getDisplayName() {
        return this.name;
    }

    private boolean isNameMatch(String name) {
        // Checks for null or blank input
        if (name == null || name.isBlank()) {
            return false;
        }
        // Checks for exact match without any separators
        return trimName(this.name).equalsIgnoreCase(trimName(name));
    }

    private String trimName(String name) {
        return name.replace("\\s", "");
    }
}
