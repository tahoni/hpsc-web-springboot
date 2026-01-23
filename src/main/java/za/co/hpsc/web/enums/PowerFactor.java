package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PowerFactor {
    MINOR("Minor", "Min"),
    MAJOR("Major", "Maj");

    private final String name;
    private final String abbreviation;

    public static Optional<PowerFactor> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(PowerFactor.values())
                .filter(powerFactor -> powerFactor.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<PowerFactor> getByAbbreviation(String abbreviation) {
        if ((abbreviation == null) || (abbreviation.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(PowerFactor.values())
                .filter(powerFactor -> powerFactor.getAbbreviation().equalsIgnoreCase(abbreviation))
                .findFirst();
    }

    public String getDisplayName() {
        return this.name;
    }
}
