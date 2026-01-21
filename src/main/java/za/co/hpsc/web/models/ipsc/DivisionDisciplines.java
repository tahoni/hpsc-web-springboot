package za.co.hpsc.web.models.ipsc;

import za.co.hpsc.web.models.ipsc.combos.*;

import java.util.Map;

public class DivisionDisciplines {
    private static final Map<String, Class<? extends DisciplinesInDivision>> divisionDisciplines;

    static {
        divisionDisciplines = Map.of(
                "Handgun", DisciplinesHandgun.class,
                "Handgun .22 ", Disciplines22Handgun.class,
                "Rifle", DisciplinesRifle.class,
                "Shotgun", DisciplinesShotgun.class,
                "PCC", DisciplinesPcc.class,
                "Mini Rifle", DisciplinesMiniRifle.class
        );
    }

    public Class<? extends DisciplinesInDivision> getDivisionDisciplines(String division) {
        return divisionDisciplines.get(division);
    }
}
