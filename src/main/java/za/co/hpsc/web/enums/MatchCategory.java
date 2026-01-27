package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing categories of shooting matches.
 * A match category defines the type or level of the shooting event,
 * helping to distinguish between different tiers of competition.
 * <p>
 * Each category is associated with a display name, providing a readable
 * identifier for presentation purposes.
 */
@Getter
@AllArgsConstructor
public enum MatchCategory {
    CLUB_SHOOT("Club Shoot"),
    LEAGUE("League");

    private final String name;

    public static Optional<MatchCategory> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Stream.of(MatchCategory.values())
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public String getDisplayName() {
        return this.name;
    }
}
