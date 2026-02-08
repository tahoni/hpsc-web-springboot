package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing categories of shooting matches.
 *
 * <p>
 * A match category defines the type or level of the shooting event,
 * helping to distinguish between different tiers of competition.
 * Each category is associated with a display name, providing a readable
 * identifier for presentation purposes.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum MatchCategory {
    // TODO: get codes
    CLUB_SHOOT("Club Shoot"),
    LEAGUE("League");

    private final String name;

    /**
     * Retrieves an optional {@code MatchCategory} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a category with the given display name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the match category to search for.
     *             Can be null or empty.
     * @return an {@code Optional} containing the matching {@code MatchCategory} if found,
     * or empty otherwise.
     */
    public static Optional<MatchCategory> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Stream.of(MatchCategory.values())
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
