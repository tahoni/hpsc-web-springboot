package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequestForCSV;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.services.IpscCompetitorService;

/**
 * Controller responsible for handling IPSC competitor CRUD API endpoints.
 *
 * <p>
 * Provides endpoints for creating, fully or partially updating and retrieving IPSC competitors.
 * </p>
 *
 * @since 8.0.0
 */
@Controller
@RequestMapping("/ipsc/competitors")
@Tag(name = "IPSC Competitor", description = "IPSC Competitor API")
public class IpscCompetitorController {
    private final IpscCompetitorService ipscCompetitorService;

    public IpscCompetitorController(IpscCompetitorService ipscCompetitorService) {
        this.ipscCompetitorService = ipscCompetitorService;
    }

    /**
     * Creates a new IPSC competitor.
     *
     * @param request the competitor to create.
     * @return the created {@link CompetitorResponse}, including its generated ID.
     * @throws ValidationException if a required field is missing, the gender is unrecognised or
     *                             the home club is HPSC without a club number.
     * @throws NonFatalException   if the named home club cannot be found.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create competitor", description = "Create a new IPSC competitor. A club number is "
            + "required when the home club is HPSC, and is otherwise ignored (forced to null).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Competitor created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, the gender is "
                    + "unrecognised, or the home club is HPSC without a club number.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "The named home club could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<CompetitorResponse> createCompetitor(@RequestBody CompetitorRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscCompetitorService.createCompetitor(request));
    }

    /**
     * Creates a batch of new IPSC competitors from CSV data.
     *
     * @param csvData the CSV content as a string containing details about competitors,
     *                formatted according to the expected schema. This parameter is required
     *                and cannot be null.
     * @return a {@link CompetitorResponseHolder} containing the created competitors.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's gender is unrecognised, or
     *                             if a row's home club is HPSC without a club number.
     * @throws NonFatalException   if a row's named home club cannot be found.
     * @throws FatalException      if a critical error occurs during processing, that prevents the
     *                             operation from completing successfully.
     */
    @PostMapping(value = "/bulk", consumes = "text/csv", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create competitors", description = "Create IPSC competitors in bulk from CSV data. A "
            + "row's club number is required when its home club is HPSC, and is otherwise ignored (forced to "
            + "null).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Competitors created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided, a required field is "
                    + "missing, the gender is unrecognised, or a row's home club is HPSC without a club number.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "A row's named home club could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the CSV data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<CompetitorResponseHolder> createCompetitors(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(implementation = CompetitorRequestForCSV.class),
                            examples = @ExampleObject("""
                                    FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                                    string,string,string,string,yyyy-MM-dd,string,string,0,string,string,string,string,string;string
                                    """)))
            @RequestBody String csvData)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscCompetitorService.createCompetitors(csvData));
    }

    /**
     * Fully replaces an existing IPSC competitor's fields with those on the request.
     *
     * @param competitorId the identifier of the competitor to replace.
     * @param request      the competitor's replacement fields.
     * @return the updated {@link CompetitorResponse}.
     * @throws ValidationException if a required field is missing, the gender is unrecognised or
     *                             the home club is HPSC without a club number.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    @PutMapping(value = "/{competitorId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace competitor", description = "Fully replace an existing IPSC competitor's fields. "
            + "A club number is required when the home club is HPSC, and is otherwise ignored (forced to null).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitor replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, the gender is "
                    + "unrecognised, or the home club is HPSC without a club number.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No competitor with this ID, or the named home club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<CompetitorResponse> updateCompetitor(
            @Parameter(description = "Identifier of the competitor to replace.") @PathVariable Long competitorId,
            @RequestBody CompetitorRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscCompetitorService.updateCompetitor(competitorId, request));
    }

    /**
     * Partially updates an existing IPSC competitor, applying only the non-null fields on the
     * request.
     *
     * @param competitorId the identifier of the competitor to update.
     * @param request      the fields to change; any field left {@code null} is left unchanged.
     *                     Touching either {@code homeClub} or {@code clubNumber} re-applies the
     *                     club number rule against the resulting home club.
     * @return the updated {@link CompetitorResponse}.
     * @throws ValidationException if the resulting home club is HPSC without a club number, or
     *                             the gender is unrecognised.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    @PatchMapping(value = "/{competitorId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update competitor", description = "Partially update an existing IPSC competitor; only "
            + "non-null fields are applied. Touching the home club or club number re-applies the club number "
            + "rule: required when the resulting home club is HPSC, forced to null otherwise.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitor updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "The resulting home club is HPSC without a club "
                    + "number, or the gender is unrecognised.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No competitor with this ID, or the named home club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<CompetitorResponse> patchCompetitor(
            @Parameter(description = "Identifier of the competitor to update.") @PathVariable Long competitorId,
            @RequestBody CompetitorRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscCompetitorService.patchCompetitor(competitorId, request));
    }

    /**
     * Retrieves an existing IPSC competitor.
     *
     * @param competitorId the identifier of the competitor to retrieve.
     * @return the {@link CompetitorResponse}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    @GetMapping(value = "/{competitorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get competitor", description = "Retrieve an IPSC competitor by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitor found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No competitor with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<CompetitorResponse> getCompetitor(
            @Parameter(description = "Identifier of the competitor to retrieve.") @PathVariable Long competitorId)
            throws NonFatalException {
        return ResponseEntity.ok(ipscCompetitorService.getCompetitor(competitorId));
    }
}
