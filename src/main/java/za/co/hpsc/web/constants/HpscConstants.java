package za.co.hpsc.web.constants;

/**
 * Defines constants specific to the HPSC module.
 *
 * <p>
 * This class provides standardized settings used within the HPSC domain for date formatting.
 * These constants help ensure uniform input and output formats across various components
 * of the application that rely on date handling specific to the HPSC context.
 * </p>
 *
 * <p>
 * The {@code HPSC_INPUT_DATE_FORMAT} constant reuses the ISO date format defined
 * in {@link DateConstants}, promoting consistency in the representation of dates
 * throughout the application.
 * </p>
 *
 * <p>
 * This class is immutable and cannot be instantiated.
 * </p>
 */
// TODO: Javadoc
public final class HpscConstants {
    public static final String HPSC_INPUT_DATE_FORMAT = DateConstants.ISO_DATE_FORMAT;
}
