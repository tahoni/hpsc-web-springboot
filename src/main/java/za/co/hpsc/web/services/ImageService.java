package za.co.hpsc.web.services;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;

/**
 * The {@code ImageService} interface provides methods for processing image-related data
 * and transforming it into structured response objects. Implementations of this
 * interface are responsible for handling and validating input data, as well as mapping
 * it to domain-specific models for further use.
 *
 * @since 1.0.0
 */
@Service
public interface ImageService {
    /**
     * Parses the provided CSV data into image details and maps each one to an image response,
     * returning a holder containing the results.
     * <p>
     * This does not persist any data — the CSV data is transformed into response objects only.
     * </p>
     *
     * @param csvData the CSV data containing image information. Each line represents
     *                an image and should follow the required format.
     * @return an {@link ImageResponseHolder} containing a list of image responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    ImageResponseHolder createImages(String csvData)
            throws FatalException;
}
