package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;

// TODO: Javadoc (not yet ready)
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

    public static ClubReference getByCode(String code) {
        if ((code == null) || (code.isBlank())) {
            return UNKNOWN;
        }

        return Arrays.stream(ClubReference.values())
                .filter(clubReference -> clubReference.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
