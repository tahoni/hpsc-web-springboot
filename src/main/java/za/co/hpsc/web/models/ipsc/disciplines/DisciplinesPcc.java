package za.co.hpsc.web.models.ipsc.disciplines;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the specific mapping between the PCC (Pistol Caliber Carbine) division
 * and its associated disciplines in the sport of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the PCC division and its disciplines. The supported disciplines for the PCC division
 * are predefined during the initialisation of the class.
 * This class extends the abstract {@code DisciplinesInDivision} class, inheriting
 * its functionality for managing divisions and their respective disciplines. The PCC
 * division and its associated disciplines are statically defined, ensuring a consistent
 * set of disciplines for this division.
 * Usage of this class provides a central access point for retrieving the PCC division
 * and its associated disciplines.
 * </p>
 */
public final class DisciplinesPcc extends DisciplinesForFirearmType {
    private static DisciplinesPcc instance;

    public static DisciplinesPcc getInstance() {
        if (DisciplinesPcc.instance == null) {
            DisciplinesPcc.instance = new DisciplinesPcc();
        }
        return DisciplinesPcc.instance;
    }

    private DisciplinesPcc() {
        super(FirearmType.PCC, List.of(
                Discipline.PCC_OPTICS,
                Discipline.PCC_IRON
        ));
    }
}
