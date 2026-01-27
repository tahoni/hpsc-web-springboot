package za.co.hpsc.web.models.ipsc.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents the disciplines associated with the Mini Rifle division in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Mini Rifle division and its disciplines. The supported disciplines for the Mini Rifle division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The Mini Rifle
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the Mini Rifle division
 * and its associated disciplines.
 * </p>
 */
public final class DisciplinesMiniRifle extends DisciplinesInDivision {
    private static DisciplinesMiniRifle instance;

    public static DisciplinesMiniRifle getInstance() {
        if (DisciplinesMiniRifle.instance == null) {
            DisciplinesMiniRifle.instance = new DisciplinesMiniRifle();
        }
        return DisciplinesMiniRifle.instance;
    }

    private DisciplinesMiniRifle() {
        super(Division.MINI_RIFLE, List.of(
                Discipline.MINI_RIFLE_OPEN,
                Discipline.MINI_RIFLE_STANDARD
        ));
    }
}
