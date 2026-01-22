package za.co.hpsc.web.models.domain.divisions;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class DisciplinesPcc extends DisciplinesInDivision {
    private static DisciplinesPcc instance;

    public static DisciplinesPcc getInstance() {
        if (DisciplinesPcc.instance == null) {
            DisciplinesPcc.instance = new DisciplinesPcc();
        }
        return DisciplinesPcc.instance;
    }

    private DisciplinesPcc() {
        super(Division.PCC, List.of(
                Discipline.PCC_OPTICS,
                Discipline.PCC_IRON
        ));
    }
}
