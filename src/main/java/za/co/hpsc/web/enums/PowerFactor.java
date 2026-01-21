package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PowerFactor {
    MINOR("Minor"),
    MAJOR("Major");

    private final String name;
}
