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
     * Retrieves the corresponding {@code CompetitorCategory} for the given name.
     * If the provided name is null, blank, or does not match any category,
     * the method returns {@code NONE}.
     *
     * @param name the name of the category to retrieve, case-insensitive.
     * @return the {@code CompetitorCategory} matching the given name, or {@code NONE}
     * if no match is found.
     */
    // TODO: Javadoc review
    // TODO: add tests
    public static Optional<CompetitorCategory> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.isNameMatch(name))
                .findFirst();
    }

    /**
     * Returns category matching code, if any
     */
    // TODO: Javadoc
    // TODO: add tests
    public static Optional<CompetitorCategory> getByCode(int code) {
        if (code == 0) {
            return Optional.empty();
        }

        return Arrays.stream(CompetitorCategory.values())
                .filter(category -> category.getCode() == code)
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
