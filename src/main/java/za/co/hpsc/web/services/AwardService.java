package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;

/**
 * The {@code AwardService} interface provides methods for processing award-related data
 * and transforming it into structured response objects. Implementations of this
 * interface are responsible for handling and validating input data, as well as mapping
 * it to domain-specific models for further use.
 *
 * @since 1.1.0
 */
public interface AwardService {
    /**
     * Parses the provided CSV data into award details and groups them into ceremonies,
     * returning a holder containing the resulting award ceremony responses.
     * <p>
     * This does not persist any data — the CSV data is transformed into response objects only.
     * </p>
     *
     * @param csvData the CSV data containing award information. Each line represents
     *                an award and should follow the required format.
     * @return an {@link AwardCeremonyResponseHolder} containing a list of award responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    AwardCeremonyResponseHolder createAwards(String csvData)
            throws FatalException;
}
