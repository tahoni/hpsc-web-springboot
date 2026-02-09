package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the divisions associated with the Shotgun firearm type in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Shotgun firearm type and its divisions.
 * The supported divisions for the Shotgun firearm type are predefined during the
 * initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The Shotgun firearm type and its associated divisions are statically defined, ensuring a consistent
 * set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the Shotgun firearm type
 * and its associated divisions.
 * </p>
 */
public final class DivisionsShotgun extends DivisionsForFirearmType {
    private static DivisionsShotgun instance;

    public static DivisionsShotgun getInstance() {
        if (DivisionsShotgun.instance == null) {
            DivisionsShotgun.instance = new DivisionsShotgun();
        }
        return DivisionsShotgun.instance;
    }

    private DivisionsShotgun() {
        super(FirearmType.SHOTGUN, List.of(
                Division.SHOTGUN_OPEN,
                Division.SHOTGUN_MODIFIED,
                Division.SHOTGUN_STANDARD
        ));
    }
}
