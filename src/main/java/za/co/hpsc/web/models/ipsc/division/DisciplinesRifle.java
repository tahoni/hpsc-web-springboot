package za.co.hpsc.web.models.ipsc.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents the disciplines associated with the Rifle division in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Rifle division and its disciplines. The supported disciplines for the Rifle division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The Rifle
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the Rifle division
 * and its associated disciplines.
 * </p>
 */
public final class DisciplinesRifle extends DisciplinesInDivision {
    private static DisciplinesRifle instance;

    public static DisciplinesRifle getInstance() {
        if (DisciplinesRifle.instance == null) {
            DisciplinesRifle.instance = new DisciplinesRifle();
        }
        return DisciplinesRifle.instance;
    }

    private DisciplinesRifle() {
        super(Division.RIFLE, List.of(
                Discipline.RIFLE_SEMI_AUTO_OPEN,
                Discipline.RIFLE_SEMI_AUTO_STANDARD,
                Discipline.RIFLE_MANUAL_ACTION_CONTEMPORARY,
                Discipline.RIFLE_MANUAL_ACTION_BOLT
        ));
    }
}
