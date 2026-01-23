package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

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
