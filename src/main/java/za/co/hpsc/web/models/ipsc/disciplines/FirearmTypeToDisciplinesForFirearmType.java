package za.co.hpsc.web.models.ipsc.disciplines;

import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.ValidationException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central mapping of {@link FirearmType} to its corresponding {@link DisciplinesForFirearmType}
 * singleton implementation.
 */
public final class FirearmTypeToDisciplinesForFirearmType {

    private static final Map<FirearmType, DisciplinesForFirearmType> MAPPING = buildMapping();

    private FirearmTypeToDisciplinesForFirearmType() {
        // Utility class, not be instantiated
    }

    /**
     * Retrieves the {@link DisciplinesForFirearmType} instance associated with the
     * specified {@link FirearmType}.
     *
     * @param firearmType the {@link FirearmType} for which the corresponding {@link DisciplinesForFirearmType}
     *                    is to be returned.
     *                    Must not be null.
     * @return the {@link DisciplinesForFirearmType} associated with the provided {@link FirearmType}.
     * @throws ValidationException if the {@code firearmType} is null or if no mapping exists
     *                             for the provided {@code FirearmType}.
     */
    public static DisciplinesForFirearmType getDisciplinesForFirearmType(FirearmType firearmType)
            throws ValidationException {
        if (firearmType == null) {
            throw new ValidationException("Firearm type cannot be null");
        }

        DisciplinesForFirearmType result = MAPPING.get(firearmType);
        if (result == null) {
            throw new ValidationException("No DisciplinesForFirearmType mapping found for firearm type: " +
                    firearmType);
        }

        return result;
    }

    /**
     * Determines the {@code FirearmType} associated with a given {@code Discipline}.
     *
     * <p>
     * The method searches for a mapping between the provided {@code Discipline} and
     * the {@code FirearmType} in the predefined mapping.
     * If no valid mapping exists, or if the input is null, a {@code ValidationException} is thrown.
     * </p>
     *
     * @param discipline the {@code Discipline} for which the corresponding {@code FirearmType}
     *                   is to be determined. Must not be null.
     * @return the {@code FirearmType} associated with the given {@code Discipline}.
     * @throws ValidationException if the {@code discipline} is null or if no mapping exists
     *                             for the provided {@code Discipline}.
     */
    // TODO: Javadoc review
    // TODO: add tests
    public static FirearmType getFirearmTypeFromDiscipline(Discipline discipline)
            throws ValidationException {

        if (discipline == null) {
            throw new ValidationException("Discipline cannot be null");
        }

        for (Map.Entry<FirearmType, DisciplinesForFirearmType> entry : MAPPING.entrySet()) {
            if (entry.getValue().getDisciplines().contains(discipline)) {
                return entry.getKey();
            }
        }

        throw new ValidationException("No FirearmType mapping found for discipline: " + discipline);
    }

    public static Map<FirearmType, DisciplinesForFirearmType> getAll() {
        return Map.copyOf(MAPPING);
    }

    /**
     * Builds immutable firearm type‑to‑disciplines mapping.
     */
    private static Map<FirearmType, DisciplinesForFirearmType> buildMapping() {
        EnumMap<FirearmType, DisciplinesForFirearmType> map = new EnumMap<>(FirearmType.class);

        map.put(FirearmType.HANDGUN, DisciplinesHandgun.getInstance());
        map.put(FirearmType.PCC, DisciplinesPcc.getInstance());
        map.put(FirearmType.SHOTGUN, DisciplinesShotgun.getInstance());
        map.put(FirearmType.RIFLE, DisciplinesRifle.getInstance());
        map.put(FirearmType.HANDGUN_22, Disciplines22Handgun.getInstance());
        map.put(FirearmType.MINI_RIFLE, DisciplinesMiniRifle.getInstance());

        return map;
    }
}