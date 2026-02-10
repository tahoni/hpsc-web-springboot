package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the divisions associated with the Rifle firearm type in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Rifle firearm type and its divisions.
 * The supported divisions for the Rifle firearm type are predefined during the initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The Rifle firearm type and its associated divisions are statically defined, ensuring a consistent
 * set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the Rifle firearm type
 * and its associated divisions.
 * </p>
 */
public final class DivisionsRifle extends DivisionsForFirearmType {
    private static DivisionsRifle instance;

    public static DivisionsRifle getInstance() {
        if (DivisionsRifle.instance == null) {
            DivisionsRifle.instance = new DivisionsRifle();
        }
        return DivisionsRifle.instance;
    }

    private DivisionsRifle() {
        super(FirearmType.RIFLE, List.of(
                Division.RIFLE_SEMI_AUTO_OPEN,
                Division.RIFLE_SEMI_AUTO_STANDARD,
                Division.RIFLE_MANUAL_ACTION_CONTEMPORARY,
                Division.RIFLE_MANUAL_ACTION_BOLT
        ));
    }
}
