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
import za.co.hpsc.web.models.award.request.AwardRequest;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.services.AwardService;

/**
 * Controller responsible for handling award-related API endpoints.
 *
 * <p>
 * Provides endpoints for handling operations such as parsing and processing CSV
 * data containing award metadata.
 * </p>
 *
 * <p>
 * This class is annotated with {@code @Controller} and {@code @RequestMapping}
 * to designate it as a Spring MVC controller and map requests with the "/award" base URI.
 * </p>
 */
@Controller
@RequestMapping("/award")
@Tag(name = "Award", description = "API for award-related functionality.")
public class AwardController {
    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Handles the processing of CSV data containing award-related details and returns a
     * structured response encapsulated in an {@link AwardCeremonyResponseHolder}.
     *
     * @param csvData The CSV content as a string containing details about awards,
     *                formatted according to the expected schema. This parameter
     *                is required and cannot be null.
     * @return an {@link AwardCeremonyResponseHolder} object containing a list of award responses
     * which encapsulates the JSON representation of the processed awards data.
     * @throws ValidationException If the provided CSV data does not meet validation requirements
     *                             or contains invalid structures.
     * @throws FatalException      If a critical error occurs during processing, that prevents
     *                             the operation from completing successfully.
     */
    @PostMapping(value = "/processCsv", consumes = "text/csv", produces = "application/json")
    @Operation(summary = "Process award CSV", description = "Convert CSV data about awards to JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted the CSV data to JSON.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            AwardCeremonyResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while " +
                    "processing the CSV data.", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<AwardCeremonyResponseHolder> processCsv(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "text/csv",
                    schema = @Schema(implementation = AwardRequest.class),
                    examples = @ExampleObject("""
                            title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                            string,string,string,string,string|string,2023-10-10,string,string,string,string,string,string,string,string,string,string,string,string
                            """)))
            @RequestBody String csvData)
            throws ValidationException, FatalException {
        return ResponseEntity.ok(awardService.processCsv(csvData));
    }
}
