package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Discipline {
    // Handgun Disciplines
    OPEN("Open Division"),
    STANDARD("Standard Division"),
    CLASSIC("Classic Division"),
    PRODUCTION("Production Division"),
    PRODUCTION_OPTICS("Production Optics Division"),
    REVOLVER("Revolver Division"),

    // Rifle Disciplines
    RIFLE_SEMI_AUTO_OPEN("Semi Auto Open Division"),
    RIFLE_SEMI_AUTO_STANDARD("Semi Auto Standard Division"),
    RIFLE_MANUAL_ACTION_CONTEMPORARY("Manual Action Contemporary Division"),
    RIFLE_MANUAL_ACTION_BOLT("Manual Action Bolt Division"),

    // Shotgun Disciplines
    SHOTGUN_OPEN("Open Division"),
    SHOTGUN_MODIFIED("Modified Division"),
    SHOTGUN_STANDARD("Standard Division"),
    SHOTGUN_STANDARD_MANUAL("Standard Manual Division"),

    // PCC Disciplines
    PCC_OPTICS("Optics Division"),
    PCC_IRON("Iron Division"),

    // .22 Disciplines
    OPEN_22("Open Division"),
    STANDARD_22("Standard Division"),
    CLASSIC_22("Classic Division"),

    // Mini Rifle Disciplines
    MINI_RIFLE_OPEN("Open Division"),
    MINI_RIFLE_STANDARD("Standard Division");

    private final String name;
}
