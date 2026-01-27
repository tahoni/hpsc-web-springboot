package za.co.hpsc.web.models.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class DisciplinesHandgun extends DisciplinesInDivision {
    private static DisciplinesHandgun instance;

    public static DisciplinesHandgun getInstance() {
        if (DisciplinesHandgun.instance == null) {
            DisciplinesHandgun.instance = new DisciplinesHandgun();
        }
        return DisciplinesHandgun.instance;
    }

    private DisciplinesHandgun() {
        super(Division.HANDGUN, List.of(
                Discipline.OPEN,
                Discipline.STANDARD,
                Discipline.CLASSIC,
                Discipline.PRODUCTION,
                Discipline.PRODUCTION_OPTICS,
                Discipline.REVOLVER
        ));
    }
}
