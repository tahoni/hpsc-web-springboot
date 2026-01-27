package za.co.hpsc.web.models.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents the disciplines associated with the Shotgun division in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Shotgun division and its disciplines. The supported disciplines for the Shotgun division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The Shotgun
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the Shotgun division
 * and its associated disciplines.
 * </p>
 */
public final class DisciplinesShotgun extends DisciplinesInDivision {
    private static DisciplinesShotgun instance;

    public static DisciplinesShotgun getInstance() {
        if (DisciplinesShotgun.instance == null) {
            DisciplinesShotgun.instance = new DisciplinesShotgun();
        }
        return DisciplinesShotgun.instance;
    }

    private DisciplinesShotgun() {
        super(Division.SHOTGUN, List.of(
                Discipline.SHOTGUN_OPEN,
                Discipline.SHOTGUN_MODIFIED,
                Discipline.SHOTGUN_STANDARD
        ));
    }
}
