package za.co.hpsc.web.constants;

import java.util.List;

/**
 * Defines constants specific to the IPSC (International Practical Shooting Confederation) domain.
 * <p>
 * This class provides standardized settings and configurations used within the IPSC module,
 * including format patterns and resource file names utilized across the application. By defining
 * these constants in a single immutable class, consistency and maintainability are improved,
 * reducing the risk of errors and allowing for easier updates.
 * <p>
 * Constants:
 * - {@code IPSC_INPUT_DATE_TIME_FORMAT} specifies the date-time format used for IPSC-related inputs.
 * It is derived from {@code DateConstants.T_SEPARATED_DATE_TIME_FORMAT}, providing a standardized
 * representation of date and time within the IPSC module.
 * <p>
 * - {@code RELEVANT_CAB_FILES} lists the relevant XML file names used by the IPSC system.
 * These files are crucial to organizing and processing resources related to matches, stages,
 * tags, enrolled participants, members, squads, and scores in the IPSC domain.
 * <p>
 * This class is immutable and cannot be instantiated.
 */
public final class IpscConstants {
    public static final String IPSC_INPUT_DATE_TIME_FORMAT = DateConstants.T_SEPARATED_DATE_TIME_FORMAT;

    public static final List<String> RELEVANT_CAB_FILES = List.of(
            "THEMATCH.XML",
            "STAGE.XML",
            "TAG.XML",
            "ENROLLED.XML",
            "MEMBER.XML",
            "SQUAD.XML",
            "SCORE.XML"
    );
}
