package za.co.hpsc.web.constants;

import java.util.List;

/**
 * Defines constants specific to the IPSC module.
 *
 * <p>
 * This class provides a centralised location for settings and configurations
 * used within the IPSC domain. The constants defined here ensure standardised
 * formats for date and time handling specific to the IPSC context.
 * </p>
 */
public final class IpscConstants {
    public static final String IPSC_INPUT_DATE_TIME_FORMAT =
            SystemConstants.T_SEPARATED_DATE_TIME_FORMAT;

    public static final List<Integer> EXCLUDE_ICS_ALIAS =
            List.of(15000, 16000);
}
