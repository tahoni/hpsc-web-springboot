package za.co.hpsc.web.models.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents the disciplines associated with the .22 Handgun division in the sport
 * of practical shooting.
 *
 * <p>
 * The .22 Handgun division is a specialised category that includes disciplines
 * tailored for firearms chambered in .22 calibre.
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the .22 Handgun division and its disciplines. The supported disciplines for the .22 Handgun division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The .22 Handgun
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the .22 Handgun division
 * and its associated disciplines.
 * </p>
 */
public final class Disciplines22Handgun extends DisciplinesInDivision {
    private static Disciplines22Handgun instance;

    public static Disciplines22Handgun getInstance() {
        if (Disciplines22Handgun.instance == null) {
            Disciplines22Handgun.instance = new Disciplines22Handgun();
        }
        return Disciplines22Handgun.instance;
    }

    private Disciplines22Handgun() {
        super(Division.HANDGUN_22, List.of(
                Discipline.OPEN_22,
                Discipline.STANDARD_22,
                Discipline.CLASSIC_22
        ));
    }
}
