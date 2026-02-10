package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ClubReference {
    SOSC("SOSC", "AAA"),
    HPSC("HPSC", "BBB"),
    PMPSC("PMPSC", "CCC"),
    VISITOR("Visitor", "UUU"),
    UNKNOWN;

    private final String name;
    private final String code;

    ClubReference() {
        this.name = "";
        this.code = "";
    }

    ClubReference(String name, String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Retrieves an optional {@code ClubReference} instance based on the provided name.
     *
     * <p>
     * The method performs a case-insensitive match to find a club reference with the given name.
     * If no match is found or the input is null/blank, an empty {@code Optional} is returned.
     * </p>
     *
     * @param name the name of the club reference to search for.
     *             Can be null or blank.
     * @return an {@code Optional} containing the matching {@code ClubReference} if found,
     * or empty otherwise.
     */
    public static Optional<ClubReference> getByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubReference.values())
                .filter(clubReference -> clubReference.name.equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Retrieves an optional {@code ClubReference} instance based on the provided code.
     *
     * <p>
     * The method searches for a club reference with a code matching the provided input.
     * If no match is found, an empty {@code Optional} is returned.
     * </p>
     *
     * @param code the code of the club reference to search for.
     *             The code can be {@code null} or negative, in which case
     *             an empty {@code Optional} is returned.
     * @return an {@code Optional} containing the matching {@code ClubReference} if found,
     * or empty otherwise.
     */
    public static Optional<ClubReference> getByCode(String code) {
        if ((code == null) || (code.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(ClubReference.values())
                .filter(clubReference -> clubReference.code.equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
