package za.co.hpsc.web.models.ipsc;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.models.ipsc.divisions.*;

import java.util.Map;
import java.util.Optional;

public class DivisionDisciplines {
    private static final Map<String, Class<? extends DisciplinesInDivision>> divisionDisciplines;

    static {
        divisionDisciplines = Map.of(
                "Handgun", DisciplinesHandgun.class,
                "Handgun .22", Disciplines22Handgun.class,
                "Rifle", DisciplinesRifle.class,
                "Shotgun", DisciplinesShotgun.class,
                "PCC", DisciplinesPcc.class,
                "Mini Rifle", DisciplinesMiniRifle.class
        );
    }

    public static Optional<Class<? extends DisciplinesInDivision>> getDivisionByEnum(Division division) {
        // Get the corresponding division discipline class
        return Optional.ofNullable(divisionDisciplines.get(division.getNames().getFirst()));
    }

    public static Optional<Class<? extends DisciplinesInDivision>> getDivisionByName(String divisionName) {
        // Get the division enum by name
        Optional<Division> division = Division.findByName(divisionName);
        // Get the division discipline class if the division enum is present
        return division.flatMap(DivisionDisciplines::getDivisionByEnum);
    }
}
