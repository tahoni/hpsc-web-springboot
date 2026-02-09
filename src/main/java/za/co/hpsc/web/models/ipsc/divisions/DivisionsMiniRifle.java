package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the divisions associated with the Mini Rifle firearm type in the sport
 * of practical shooting.
 *
 * <p>
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the Mini Rifle firearm type and its divisions.
 * The supported divisions for the Mini Rifle firearm type are predefined during the
 * initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The Mini Rifle firearm type and its associated divisions are statically defined,
 * ensuring a consistent set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the Mini Rifle firearm type
 * and its associated divisions.
 * </p>
 */
public final class DivisionsMiniRifle extends DivisionsForFirearmType {
    private static DivisionsMiniRifle instance;

    public static DivisionsMiniRifle getInstance() {
        if (DivisionsMiniRifle.instance == null) {
            DivisionsMiniRifle.instance = new DivisionsMiniRifle();
        }
        return DivisionsMiniRifle.instance;
    }

    private DivisionsMiniRifle() {
        super(FirearmType.MINI_RIFLE, List.of(
                Division.MINI_RIFLE_OPEN,
                Division.MINI_RIFLE_STANDARD
        ));
    }
}
