package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.AwardResponseHolder;

/**
 * Service interface for handling award processing operations. This interface
 * defines methods for processing award data and generating responses.
 */
public interface AwardService {
    /**
     * Processes the provided CSV data to extract award details and returns an object
     * containing a list of award responses.
     *
     * @param csvData the CSV data containing award information. Each line represents
     *                an award and should follow the required format.
     * @return an {@code AwardResponseHolder} containing a list of award responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data contains invalid or missing values.
     * @throws FatalException      if there is an error processing the CSV data.
     */
    AwardResponseHolder processCsv(String csvData)
            throws ValidationException, FatalException;
}
