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
import za.co.hpsc.web.models.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.AwardRequest;
import za.co.hpsc.web.services.AwardService;

/**
 * Controller class responsible for handling award-related API endpoints.
 * Provides functionality for processing award data in CSV format and converting
 * it to JSON.
 * <p>
 * This class is designed to manage HTTP requests related to awards and delegate
 * the processing tasks to the {@link AwardService}. It uses RESTful design principles
 * and leverages Spring Boot annotations for mapping requests and responses.
 * <p>
 * Annotations:
 * - {@code @Controller}: Indicates that this class is a Spring MVC controller.
 * - {@code @RequestMapping("/award")}: Maps incoming requests with paths prefixed
 * by {@code /award} to the methods in this class.
 * - {@code @Tag}: Adds OpenAPI metadata for describing the purpose of this controller.
 * <p>
 * Dependencies:
 * - {@link AwardService}: A service layer interface for processing award-related operations.
 */
// TODO: Javadoc
@Controller
@RequestMapping("/award")
@Tag(name = "Award", description = "API for award-related functionality.")
public class AwardController {
    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Processes the provided CSV data about awards and converts it into a JSON response.
     *
     * @param csvData The CSV content as a string containing details about awards,
     *                formatted according to the expected schema. This parameter
     *                is required and cannot be null.
     * @return A {@code ResponseEntity} containing an {@link AwardCeremonyResponseHolder},
     * which encapsulates the JSON representation of the processed awards data.
     * @throws ValidationException If the provided CSV data does not meet validation requirements
     *                             or contains invalid structures.
     * @throws FatalException      If a critical error occurs during processing that prevents
     *                             the operation from completing successfully.
     */
    @PostMapping(value = "/processCsv")
    @Operation(summary = "Process award CSV", description = "Convert CSV data about awards to JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AwardCeremonyResponseHolder.class)))
    })
    ResponseEntity<AwardCeremonyResponseHolder> processCsv(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "text/csv",
                    schema = @Schema(implementation = AwardRequest.class),
                    examples = @ExampleObject("""
                            
                            
                            """)))
            @RequestBody String csvData)
            throws ValidationException, FatalException {
        return ResponseEntity.ok(awardService.processCsv(csvData));
    }
}
