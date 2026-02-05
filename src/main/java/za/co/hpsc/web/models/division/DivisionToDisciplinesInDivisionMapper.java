package za.co.hpsc.web.models.division;

import za.co.hpsc.web.enums.Division;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central mapping of {@link Division} to its corresponding {@link DisciplinesInDivision}
 * singleton implementation.
 */
public final class DivisionToDisciplinesInDivisionMapper {

    private static final Map<Division, DisciplinesInDivision> MAPPING = buildMapping();

    private DivisionToDisciplinesInDivisionMapper() {
        // Utility class
    }

    /**
     * Retrieves the {@link DisciplinesInDivision} instance associated with the
     * specified {@link Division}.
     *
     * @param division the {@link Division} for which the corresponding {@link DisciplinesInDivision}
     *                 is to be returned. Must not be null.
     * @return the {@link DisciplinesInDivision} associated with the provided {@link Division}.
     * @throws IllegalArgumentException if the {@code division} is null.
     * @throws IllegalStateException    if no mapping exists for the provided {@code Division}.
     */
    public static DisciplinesInDivision getDisciplinesForDivision(Division division) {
        if (division == null) {
            throw new IllegalArgumentException("Division cannot be null");
        }

        DisciplinesInDivision result = MAPPING.get(division);
        if (result == null) {
            throw new IllegalStateException("No DisciplinesInDivision mapping found for division: " + division);
        }

        return result;
    }

    public static Map<Division, DisciplinesInDivision> getAll() {
        return Map.copyOf(MAPPING);
    }

    /**
     * Builds immutable division‑to‑disciplines mapping
     */
    private static Map<Division, DisciplinesInDivision> buildMapping() {
        EnumMap<Division, DisciplinesInDivision> map = new EnumMap<>(Division.class);

        map.put(Division.HANDGUN, DisciplinesHandgun.getInstance());
        map.put(Division.PCC, DisciplinesPcc.getInstance());
        map.put(Division.SHOTGUN, DisciplinesShotgun.getInstance());
        map.put(Division.RIFLE, DisciplinesRifle.getInstance());
        map.put(Division.HANDGUN_22, Disciplines22Handgun.getInstance());
        map.put(Division.MINI_RIFLE, DisciplinesMiniRifle.getInstance());

        return map;
    }
}