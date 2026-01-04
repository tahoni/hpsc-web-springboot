package za.co.hpsc.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

/**
 * Controller responsible for managing and processing image-related requests.
 * Provides endpoints for handling operations such as parsing and processing CSV
 * data containing image metadata.
 * <p>
 * This class is annotated with {@code @Controller} and {@code @RequestMapping}
 * to designate it as a Spring MVC controller and map requests with the "/image" base URI.
 * </p>
 */
@Controller
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Handles the processing of CSV data containing image-related details and returns a
     * structured response encapsulated in an {@code ImageResponseHolder}.
     *
     * @param csvData the CSV data containing image information. Each line represents
     *                an image and should follow the required format with columns:
     *                title, summary, description, category, tags, filePath, and fileName.
     * @return an {@code ImageResponseHolder} containing a list of image responses
     * parsed from the CSV data.
     * @throws ValidationException if the CSV data contains invalid or missing values.
     */
    @PostMapping(value = "/processCsv", produces = "application/json")
    ImageResponseHolder processCsv(@RequestBody String csvData)
            throws ValidationException {
        try {
            return imageService.processCsv(csvData);
        } catch (za.co.hpsc.web.exceptions.FatalException e) {
            throw new RuntimeException(e);
        }
    }
}
