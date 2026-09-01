package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
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
 * @since 1.1.0
 */
@Controller
@RequestMapping("/awards")
@Tag(name = "Awards API", description = "API for award-related functionality.")
public class AwardController {
    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Handles bulk creation of awards from CSV data, grouping them into ceremonies and returning a
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
    @PostMapping(value = "/bulk", consumes = "text/csv", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create awards", description = "Create awards in bulk from CSV data, grouped into ceremonies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Awards created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AwardCeremonyResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the CSV data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<AwardCeremonyResponseHolder> createAwards(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(implementation = AwardRequest.class),
                            examples = @ExampleObject("""
                                    title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                                    string,string,string,string,string;string,yyyy-MM-dd,string,string,string,string,string,string,string,string,string,string,string,string
                                    """)))
            @RequestBody String csvData)
            throws ValidationException, FatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(awardService.createAwards(csvData));
    }
}
