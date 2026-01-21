package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.awards.AwardCeremonyResponseHolder;

/**
 * The {@code AwardService} interface provides methods for processing award-related data
 * and transforming it into structured response objects. Implementations of this
 * interface are responsible for handling and validating input data, as well as mapping
 * it to domain-specific models for further use.
 */
public interface AwardService {
    /**
     * Processes the provided CSV data to extract award details and returns an object
     * containing a list of award responses.
     *
     * @param csvData the CSV data containing award information. Each line represents
     *                an award and should follow the required format.
     * @return an {@link AwardCeremonyResponseHolder} containing a list of award responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data contains invalid or missing values.
     * @throws FatalException      if there is an error processing the CSV data.
     */
    AwardCeremonyResponseHolder processCsv(String csvData)
            throws ValidationException, FatalException;
}
