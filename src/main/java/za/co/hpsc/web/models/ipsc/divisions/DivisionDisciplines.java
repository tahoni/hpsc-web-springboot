package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.models.ipsc.divisions.disciplines.*;

import java.util.Map;
import java.util.Optional;

// TODO: consider using a dependency injection framework for better scalability
// TODO: consider using a factory pattern if more complex logic is needed in the future
// TODO: consider caching the results if performance becomes an issue
// TODO: consider adding logging for better traceability
// TODO: consider adding unit tests for this class
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
        Optional<Division> division = Division.getByName(divisionName);
        // Get the division discipline class if the division enum is present
        return division.flatMap(DivisionDisciplines::getDivisionByEnum);
    }
}
