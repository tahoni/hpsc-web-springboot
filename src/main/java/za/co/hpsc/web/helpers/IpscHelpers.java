package za.co.hpsc.web.helpers;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;

public final class IpscHelpers {
    private static final String SEPARATOR = " - ";

    public static String getDivisionDiscipleName(Division division, Discipline discipline) {
        return division.getNames().getFirst() + SEPARATOR + discipline.getName();
    }
}
