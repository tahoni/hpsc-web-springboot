package za.co.hpsc.web.utils;

import org.apache.commons.lang3.math.NumberUtils;
import za.co.hpsc.web.constants.IpscConstants;

/**
 * Utility class providing methods related to IPSC (International Practical Shooting Confederation)
 * operations.
 *
 * <p>
 * This class contains methods to validate and manage IPSC-related data, such as SAPSA numbers
 * and other identifiers. It leverages predefined constants from {@code IpscConstants} to
 * ensure compliance with domain-specific rules and exclusions.
 * </p>
 */
public class IpscUtil {
    private IpscUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Validates the given ICS Alias number and returns it if it passes all validation checks.
     *
     * <p>
     * The method ensures that the provided ICS Alias number is non-null, not blank, and is a valid
     * numeric value. Additionally, it checks if the number is not part of a predefined exclusion
     * list. If any of these conditions fail, the method returns {@code null}.
     * </p>
     *
     * @param icsAlias the ICS Alias number to be validated; may be {@code null}
     * @return the validated ICS Alias number if it passes all conditions,
     * or {@code null} if it fails validation
     */
    public static Integer getValidSapsaNumber(String icsAlias) {
        // Check if the ICS Alias is empty
        if ((icsAlias == null) || (icsAlias.isBlank())) {
            return null;
        }

        // Check if the ICS Alias is not a valid number
        if (!NumberUtils.isCreatable(icsAlias)) {
            return null;
        }

        // Check if the ICS Alias is in the exclude list
        if (IpscConstants.EXCLUDE_ICS_ALIAS.contains(icsAlias)) {
            return null;
        }

        // Convert the ICS Alias to an integer
        Integer sapsaNumber = null;
        try {
            sapsaNumber = Integer.valueOf(icsAlias);
        } catch (NumberFormatException ex) {
            // If the conversion fails, the ICS Alias is invalid
            return null;
        }

        // Check if the ICS Alias is within the valid range
        if (sapsaNumber > IpscConstants.MAX_SAPSA_NUMBER) {
            return null;
        }

        // Return the validated ICS Alias number
        return sapsaNumber;
    }
}
