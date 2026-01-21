package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class DisciplinesRifle extends DisciplinesInDivision {
    private static DisciplinesRifle instance;

    public static DisciplinesRifle getInstance() {
        if (DisciplinesRifle.instance == null) {
            DisciplinesRifle.instance = new DisciplinesRifle();
        }
        return DisciplinesRifle.instance;
    }

    private DisciplinesRifle() {
        super(Division.RIFLE, List.of(
                Discipline.RIFLE_SEMI_AUTO_OPEN,
                Discipline.RIFLE_SEMI_AUTO_STANDARD,
                Discipline.RIFLE_MANUAL_ACTION_CONTEMPORARY,
                Discipline.RIFLE_MANUAL_ACTION_BOLT
        ));
    }
}
