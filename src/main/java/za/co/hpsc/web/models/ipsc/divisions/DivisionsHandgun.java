package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the divisions associated with the Handgun firearm type in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Handgun firearm type and its divisions.
 * The supported divisions for the Handgun firearm type are predefined during the
 * initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The Handgun firearm type and its associated divisions are statically defined,
 * ensuring a consistent set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the Handgun firearm type
 * and its associated divisions.
 */
public final class DivisionsHandgun extends DivisionsForFirearmType {
    private static DivisionsHandgun instance;

    public static DivisionsHandgun getInstance() {
        if (DivisionsHandgun.instance == null) {
            DivisionsHandgun.instance = new DivisionsHandgun();
        }
        return DivisionsHandgun.instance;
    }

    private DivisionsHandgun() {
        super(FirearmType.HANDGUN, List.of(
                Division.OPEN,
                Division.STANDARD,
                Division.MODIFIED,
                Division.CLASSIC,
                Division.PRODUCTION,
                Division.PRODUCTION_OPTICS,
                Division.PRODUCTION_OPTICS_LIGHT,
                Division.OPTICS,
                Division.REVOLVER
        ));
    }
}
