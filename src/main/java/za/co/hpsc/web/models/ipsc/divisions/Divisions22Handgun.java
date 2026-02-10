package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents the divisions associated with the .22 Handgun firearm type in the sport
 * of practical shooting.
 *
 * <p>
 * The .22 Handgun firearm type is a specialised category that includes divisions
 * tailored for firearms chambered in .22 calibre.
 * This is a singleton class that provides a thread-safe, single instance for managing
 * the .22 Handgun firearm type and its divisions.
 * The supported divisions for the .22 Handgun firearm type are predefined during the
 * initialisation of the class.
 * This class extends the abstract {@link DivisionsForFirearmType} class, inheriting
 * its functionality for managing firearm types and their respective divisions.
 * The .22 Handgun firearm type and its associated divisions are statically defined,
 * ensuring a consistent set of divisions for this firearm type.
 * Usage of this class provides a central access point for retrieving the .22 Handgun firearm type
 * and its associated divisions.
 * </p>
 */
public final class Divisions22Handgun extends DivisionsForFirearmType {
    private static Divisions22Handgun instance;

    public static Divisions22Handgun getInstance() {
        if (Divisions22Handgun.instance == null) {
            Divisions22Handgun.instance = new Divisions22Handgun();
        }
        return Divisions22Handgun.instance;
    }

    private Divisions22Handgun() {
        super(FirearmType.HANDGUN_22, List.of(
                Division.OPEN_22,
                Division.STANDARD_22,
                Division.CLASSIC_22,
                Division.OPTICS_22
        ));
    }
}
