package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompetitorCategory {
    JUNIOR("Junior"),
    SUPER_JUNIOR("Super Junior"),
    LADY("Lady"),
    SUPER_LADY("Lady,Senior"),
    JUNIOR_LADY("Lady,Junior"),
    SENIOR("Senior"),
    SUPER_SENIOR("Super Senior"),
    NONE("");

    private final String name;
}
