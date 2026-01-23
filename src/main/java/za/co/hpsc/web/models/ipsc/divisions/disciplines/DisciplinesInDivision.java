package za.co.hpsc.web.models.ipsc.divisions.disciplines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

@Getter
@AllArgsConstructor
public sealed abstract class DisciplinesInDivision
        permits DisciplinesHandgun, DisciplinesShotgun, DisciplinesRifle, DisciplinesPcc,
        Disciplines22Handgun, DisciplinesMiniRifle {
    private final Division division;
    private final List<Discipline> disciplines;
}
