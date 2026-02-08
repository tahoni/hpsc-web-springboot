package za.co.hpsc.web.models.ipsc.disciplines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;

import java.util.List;

/**
 * Represents a generic mapping between a firearm type and its associated
 * disciplines in the sport of practical shooting. This abstract class serves as a base
 * for specific firearm type-discipline mappings.
 *
 * <p>
 * The purpose of this class is to encapsulate the relationship between a
 * firearm type and the specific set of disciplines that belong to it. Each subclass
 * defines a concrete firearm type and assigns its corresponding disciplines.
 * This class is designed to be extended by subclasses representing various
 * firearm types such as handgun, shotgun, rifle, etc. Each subclass provides
 * a singleton implementation for its associated firearm type.
 * </p>
 */
@Getter
@AllArgsConstructor
public sealed abstract class DisciplinesForFirearmType
        permits DisciplinesHandgun, DisciplinesShotgun, DisciplinesRifle, DisciplinesPcc,
        Disciplines22Handgun, DisciplinesMiniRifle {
    private final FirearmType firearmType;
    private final List<Discipline> disciplines;
}
