package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Gender {
    Male("Male", "M"),
    Female("Female", "F");

    private final String name;
    private final String abbreviation;

    Gender(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public static Optional<Gender> fromName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(Gender.values())
                .filter(gender -> gender.name.equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
