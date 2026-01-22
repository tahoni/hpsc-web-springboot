package za.co.hpsc.web.services;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.ImageResponseHolder;

/**
 * The {@code ImageService} interface provides methods for processing image-related data
 * and transforming it into structured response objects. Implementations of this
 * interface are responsible for handling and validating input data, as well as mapping
 * it to domain-specific models for further use.
 */
@Service
public interface ImageService {
    /**
     * Processes the provided CSV data to extract image details and returns an object
     * containing a list of image responses.
     *
     * @param csvData the CSV data containing image information. Each line represents
     *                an image and should follow the required format.
     * @return an {@link ImageResponseHolder} containing a list of image responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data contains invalid or missing values.
     * @throws FatalException      if there is an error processing the CSV data.
     */
    ImageResponseHolder processCsv(String csvData)
            throws ValidationException, FatalException;
}
