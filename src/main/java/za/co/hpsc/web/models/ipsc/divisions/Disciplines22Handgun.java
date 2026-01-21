package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

import java.util.List;

public final class Disciplines22Handgun extends DisciplinesInDivision {
    private static Disciplines22Handgun instance;

    public static Disciplines22Handgun getInstance() {
        if (Disciplines22Handgun.instance == null) {
            Disciplines22Handgun.instance = new Disciplines22Handgun();
        }
        return Disciplines22Handgun.instance;
    }

    private Disciplines22Handgun() {
        super(Division.HANDGUN_22, List.of(
                Discipline.OPEN_22,
                Discipline.STANDARD_22,
                Discipline.CLASSIC_22
        ));
    }
}
