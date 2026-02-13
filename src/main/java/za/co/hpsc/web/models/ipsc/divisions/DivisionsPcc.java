package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the specific mapping between the PCC (Pistol Calibre Carbine) firearm type
 * and its associated divisions in the sport of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the PCC firearm type and its divisions.
 * The supported divisions for the PCC firearm type are predefined during the
 * initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The PCC firearm type and its associated divisions are statically defined,
 * ensuring a consistent set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the PCC firearm type
 * and its associated divisions.
 * </p>
 */
public final class DivisionsPcc extends DivisionsForFirearmType {
    private static DivisionsPcc instance;

    public static DivisionsPcc getInstance() {
        if (DivisionsPcc.instance == null) {
            DivisionsPcc.instance = new DivisionsPcc();
        }
        return DivisionsPcc.instance;
    }

    private DivisionsPcc() {
        super(FirearmType.PCC, List.of(
                Division.PCC_OPTICS,
                Division.PCC_IRON
        ));
    }
}
