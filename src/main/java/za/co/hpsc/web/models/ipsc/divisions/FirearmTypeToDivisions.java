package za.co.hpsc.web.models.ipsc.divisions;

import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.ValidationException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central mapping of {@link FirearmType} to its corresponding {@link DivisionsForFirearmType}
 * singleton implementation.
 */
public final class FirearmTypeToDivisions {

    private static final Map<FirearmType, DivisionsForFirearmType> MAPPING = buildMapping();

    private FirearmTypeToDivisions() {
        // Utility class, not be instantiated
    }

    /**
     * Retrieves the {@link DivisionsForFirearmType} instance associated with the
     * specified {@link FirearmType}.
     *
     * @param firearmType the {@link FirearmType} for which the corresponding {@link DivisionsForFirearmType}
     *                    is to be returned.
     *                    Must not be null.
     * @return the {@link DivisionsForFirearmType} associated with the provided {@link FirearmType}.
     * @throws ValidationException if the {@code firearmType} is null or if no mapping exists
     *                             for the provided {@code FirearmType}.
     */
    public static DivisionsForFirearmType getDivisionsForFirearmType(FirearmType firearmType)
            throws ValidationException {
        if (firearmType == null) {
            throw new ValidationException("Firearm type cannot be null");
        }

        DivisionsForFirearmType result = MAPPING.get(firearmType);
        if (result == null) {
            throw new ValidationException("No DivisionsForFirearmType mapping found for firearm type: " +
                    firearmType);
        }

        return result;
    }

    /**
     * Determines the {@code FirearmType} associated with a given {@code Division}.
     *
     * <p>
     * The method searches for a mapping between the provided {@code Division} and
     * the {@code FirearmType} in the predefined mapping.
     * If no valid mapping exists, or if the input is null, a {@code ValidationException} is thrown.
     * </p>
     *
     * @param division the {@link Division} for which the corresponding {@link FirearmType}
     *                 is to be determined. Must not be null.
     * @return the {@link FirearmType} associated with the given {@link Division}.
     * @throws ValidationException if the {@code division} is null or if no mapping exists
     *                             for the provided {@link Division}.
     */
    public static FirearmType getFirearmTypeFromDivision(Division division)
            throws ValidationException {

        if (division == null) {
            throw new ValidationException("Division cannot be null");
        }

        for (Map.Entry<FirearmType, DivisionsForFirearmType> entry : MAPPING.entrySet()) {
            if (entry.getValue().getDivisions().contains(division)) {
                return entry.getKey();
            }
        }

        throw new ValidationException("No FirearmType mapping found for division: " + division);
    }

    public static Map<FirearmType, DivisionsForFirearmType> getAll() {
        return Map.copyOf(MAPPING);
    }

    /**
     * Builds immutable firearm type‑to‑divisions mapping.
     */
    private static Map<FirearmType, DivisionsForFirearmType> buildMapping() {
        EnumMap<FirearmType, DivisionsForFirearmType> map = new EnumMap<>(FirearmType.class);

        map.put(FirearmType.HANDGUN, DivisionsHandgun.getInstance());
        map.put(FirearmType.PCC, DivisionsPcc.getInstance());
        map.put(FirearmType.SHOTGUN, DivisionsShotgun.getInstance());
        map.put(FirearmType.RIFLE, DivisionsRifle.getInstance());
        map.put(FirearmType.HANDGUN_22, Divisions22Handgun.getInstance());
        map.put(FirearmType.MINI_RIFLE, DivisionsMiniRifle.getInstance());

        return map;
    }
}