package za.co.hpsc.web.services;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.models.ImageResponseHolder;

import java.io.IOException;

/**
 * Provides functionality for processing image-related data from various sources.
 * Designed to handle operations such as parsing CSV files containing image information
 * and returning structured data encapsulated within a response holder.
 * <p>
 * This service defines a contract for implementing and managing image-related workflows,
 * ensuring consistency and reusability across different components of the application.
 * </p>
 */
@Service
public interface ImageService {
    /**
     * Processes the provided CSV data to extract image details and returns an object
     * containing a list of image responses.
     *
     * @param csvData the CSV data containing image information. Each line represents
     *                an image and should follow the required format with columns:
     *                title, summary, description, category, tags, filePath, and fileName.
     * @return an {@code ImageResponseHolder} containing a list of image responses
     * parsed from the CSV data.
     * @throws IOException if there is an error reading or parsing the provided CSV data.
     *                     This may occur if the CSV data is malformed or incomplete.
     */
    ImageResponseHolder processCsv(String csvData) throws IOException;
}
