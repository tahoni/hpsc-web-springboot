package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.request.ImageRequest;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

/**
 * Controller responsible for handling image-related API endpoints.
 *
 * <p>
 * Provides endpoints for handling operations such as parsing and processing CSV
 * data containing image metadata.
 * </p>
 *
 * <p>
 * This class is annotated with {@code @Controller} and {@code @RequestMapping}
 * to designate it as a Spring MVC controller and map requests with the "/image" base URI.
 * </p>
 */
@Controller
@RequestMapping("/image")
@Tag(name = "Image", description = "API for image-related functionality.")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Handles the processing of CSV data containing image-related details and returns a
     * structured response encapsulated in an {@link ImageResponseHolder}.
     *
     * @param csvData The CSV content as a string containing details about images,
     *                formatted according to the expected schema. This parameter
     *                is required and cannot be null.
     * @return an {@link ImageResponseHolder} object containing a list of image responses which
     * encapsulates the JSON representation of the processed image data.
     * @throws ValidationException If the provided CSV data does not meet validation requirements
     *                             or contains invalid structures.
     * @throws FatalException      If a critical error occurs during processing, that prevents
     *                             the operation from completing successfully.
     */
    @PostMapping(value = "/processCsv", consumes = "text/csv", produces = "application/json")
    @Operation(summary = "Process image CSV", description = "Convert CSV data about images to JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted the CSV data to JSON.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ImageResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while " +
                    "processing the CSV data.", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<ImageResponseHolder> processCsv(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "text/csv",
                    schema = @Schema(implementation = ImageRequest.class),
                    examples = @ExampleObject("""
                            title,summary,description,category,tags,filePath,fileName
                            string,string,string,string|string,string,string
                            """)))
            @RequestBody String csvData)
            throws ValidationException, FatalException {
        return ResponseEntity.ok(imageService.processCsv(csvData));
    }
}
