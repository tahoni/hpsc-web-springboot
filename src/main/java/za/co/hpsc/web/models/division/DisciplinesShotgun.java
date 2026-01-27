package za.co.hpsc.web.models.division;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class DisciplinesShotgun extends DisciplinesInDivision {
    private static DisciplinesShotgun instance;

    public static DisciplinesShotgun instance() {
        if (DisciplinesShotgun.instance == null) {
            DisciplinesShotgun.instance = new DisciplinesShotgun();
        }
        return DisciplinesShotgun.instance;
    }

    private DisciplinesShotgun() {
        super(Division.SHOTGUN, List.of(
                Discipline.SHOTGUN_OPEN,
                Discipline.SHOTGUN_MODIFIED,
                Discipline.SHOTGUN_STANDARD
        ));
    }
}
