package za.co.hpsc.web.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Discipline {

    // Handgun Disciplines
    OPEN("Open Division", "O"),
    STANDARD("Standard Division", "S"),
    CLASSIC("Classic Division", "C"),
    PRODUCTION("Production Division", "P"),
    PRODUCTION_OPTICS("Production Optics Division", "PO"),
    PRODUCTION_OPTICS_LIGHT("Production Optics Light Division", "POL"),
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
    PCC_OPTICS("Optics Division", "PCC"),
    PCC_IRON("Iron Division", "PCC"),

    // .22 Disciplines
    OPEN_22("Open Division"),
    STANDARD_22("Standard Division"),
    CLASSIC_22("Classic Division"),

    // Mini Rifle Disciplines
    MINI_RIFLE_OPEN("Open Division"),
    MINI_RIFLE_STANDARD("Standard Division");

    private final String name;
    private final String abbreviation;

    Discipline(String name) {
        this.name = name;
        this.abbreviation = "";
    }

    Discipline(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public static Optional<Discipline> findByName(String name) {
        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isNameMatch(name))
                .findFirst();
    }

    public static Optional<Discipline> findByAbbreviation(String abbreviation) {
        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isAbbreviationMatch(abbreviation))
                .findFirst();
    }

    public static Optional<Discipline> findByAbbreviationOrName(String name) {
        return Arrays.stream(Discipline.values())
                .filter(discipline -> discipline.isNameMatch(name) ||
                        discipline.isAbbreviationMatch(name))
                .findFirst();
    }

    public String getDisplayName() {
        return this.name;
    }

    private boolean isNameMatch(String name) {
        // Checks for null or blank input
        if (name == null || name.isBlank()) {
            return false;
        }
        // Checks for exact match
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }
        // Checks for starts with match
        return this.name.startsWith(name);
    }

    private boolean isAbbreviationMatch(String abbreviation) {
        // Checks for null or blank input
        if (abbreviation == null || abbreviation.isBlank()) {
            return false;
        }
        // Checks for exact match
        return this.abbreviation.equalsIgnoreCase(abbreviation);
    }
}
