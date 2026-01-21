package za.co.hpsc.web.models.ipsc.combos;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class DisciplinesMiniRifle extends DisciplinesInDivision {
    private static DisciplinesMiniRifle instance;

    public static DisciplinesMiniRifle getInstance() {
        if (DisciplinesMiniRifle.instance == null) {
            DisciplinesMiniRifle.instance = new DisciplinesMiniRifle();
        }
        return DisciplinesMiniRifle.instance;
    }

    private DisciplinesMiniRifle() {
        super(Division.MINI_RIFLE, List.of(
                Discipline.MINI_RIFLE_OPEN,
                Discipline.MINI_RIFLE_STANDARD
        ));
    }
}
