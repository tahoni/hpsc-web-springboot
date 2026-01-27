package za.co.hpsc.web.models.ipsc.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents the disciplines associated with the Handgun division in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Handgun division and its disciplines. The supported disciplines for the Handgun division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The Handgun
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the Handgun division
 * and its associated disciplines.
 */
public final class DisciplinesHandgun extends DisciplinesInDivision {
    private static DisciplinesHandgun instance;

    public static DisciplinesHandgun getInstance() {
        if (DisciplinesHandgun.instance == null) {
            DisciplinesHandgun.instance = new DisciplinesHandgun();
        }
        return DisciplinesHandgun.instance;
    }

    private DisciplinesHandgun() {
        super(Division.HANDGUN, List.of(
                Discipline.OPEN,
                Discipline.STANDARD,
                Discipline.CLASSIC,
                Discipline.PRODUCTION,
                Discipline.PRODUCTION_OPTICS,
                Discipline.REVOLVER
        ));
    }
}
