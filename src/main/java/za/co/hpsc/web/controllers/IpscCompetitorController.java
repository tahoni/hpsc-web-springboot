package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
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
     * @throws ValidationException if a required field is missing, or the gender is unrecognised.
     * @throws NonFatalException   if the named home club cannot be found.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create competitor", description = "Create a new IPSC competitor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Competitor created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, or the gender is unrecognised.",
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
     * Fully replaces an existing IPSC competitor's fields with those on the request.
     *
     * @param competitorId the identifier of the competitor to replace.
     * @param request      the competitor's replacement fields.
     * @return the updated {@link CompetitorResponse}.
     * @throws ValidationException if a required field is missing, or the gender is unrecognised.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    @PutMapping(value = "/{competitorId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace competitor", description = "Fully replace an existing IPSC competitor's fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitor replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, or the gender is unrecognised.",
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
     * @return the updated {@link CompetitorResponse}.
     * @throws ValidationException if the club number is blank, or the gender is unrecognised.
     * @throws NonFatalException   if no competitor with {@code competitorId} exists, or the
     *                             named home club cannot be found.
     */
    @PatchMapping(value = "/{competitorId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update competitor", description = "Partially update an existing IPSC competitor; only non-null fields are applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitor updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "The club number is blank, or the gender is unrecognised.",
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
