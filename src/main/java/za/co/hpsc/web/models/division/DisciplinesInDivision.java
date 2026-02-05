package za.co.hpsc.web.models.division;

import lombok.AllArgsConstructor;
import lombok.Getter;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

/**
 * Represents a generic mapping between a shooting division and its associated
 * disciplines in the sport of practical shooting. This abstract class serves as a base
 * for specific division-discipline mappings.
 *
 * <p>
 * The purpose of this class is to encapsulate the relationship between a
 * division and the specific set of disciplines that belong to it. Each subclass
 * defines a concrete division and assigns its corresponding disciplines.
 * This class is designed to be extended by subclasses representing various
 * divisions such as handgun, shotgun, rifle, etc. Each subclass provides
 * a singleton implementation for its associated division.
 * </p>
 */
@Getter
@AllArgsConstructor
public sealed abstract class DisciplinesInDivision
        permits DisciplinesHandgun, DisciplinesShotgun, DisciplinesRifle, DisciplinesPcc,
        Disciplines22Handgun, DisciplinesMiniRifle {
    private final Division division;
    private final List<Discipline> disciplines;
}
