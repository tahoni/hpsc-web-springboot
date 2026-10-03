package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponseHolder;
import za.co.hpsc.web.services.IpscMatchCompetitorService;

import java.util.List;

/**
 * Controller responsible for handling IPSC match competitor CRUD API endpoints.
 *
 * <p>
 * Provides endpoints for creating, fully or partially updating, retrieving (individually or all
 * at once) and deleting match competitors: one competitor's entry in one match, in one firearm type.
 * </p>
 */
@Controller
@RequestMapping("/ipsc/match-competitors")
@Tag(name = "IPSC Match Competitor", description = "IPSC Match Competitor API")
public class IpscMatchCompetitorController {
    private final IpscMatchCompetitorService ipscMatchCompetitorService;

    public IpscMatchCompetitorController(IpscMatchCompetitorService ipscMatchCompetitorService) {
        this.ipscMatchCompetitorService = ipscMatchCompetitorService;
    }

    /**
     * Creates a new match competitor.
     *
     * @param request the match competitor to create.
     * @return the created {@link MatchCompetitorResponse}, including its generated ID.
     * @throws ValidationException if a required field is missing, an enumerated value is unrecognised, or the
     *                             competitor already has an entry for the match and firearm type.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create match competitor", description = "Create a competitor's entry in a match. A "
            + "competitor can have only one entry per match and firearm type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match competitor created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchCompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, an enumerated value is "
                    + "unrecognised, or the competitor already has an entry for the match and firearm type.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "The competitor or match could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchCompetitorResponse> createMatchCompetitor(@RequestBody MatchCompetitorRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    /**
     * Creates a batch of new match competitors from CSV data.
     *
     * @param csvData the CSV content as a string containing details about match competitors, formatted according
     *                to the expected schema. This parameter is required and cannot be null.
     * @return a {@link MatchCompetitorResponseHolder} containing the created match competitors.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is missing a
     *                             required field or has an unrecognised enumerated value, or if a row duplicates
     *                             another row, or an existing entry, for the competitor, match and firearm type.
     * @throws NonFatalException   if a row's competitor or match cannot be found.
     * @throws FatalException      if a critical error occurs during processing, that prevents the operation from
     *                             completing successfully.
     */
    @PostMapping(value = "/bulk", consumes = "text/csv", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create match competitors", description = "Create competitors' entries in matches in bulk "
            + "from CSV data. Every row is checked before any is saved, so either every row is created or none is.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match competitors created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchCompetitorResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided, a required field is "
                    + "missing, an enumerated value is unrecognised, or a row duplicates another entry for the "
                    + "competitor, match and firearm type.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "A row's competitor or match could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the CSV data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchCompetitorResponseHolder> createMatchCompetitors(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject("""
                                    CompetitorId,Name,Mem #,MatchId,Class,Cats,FirearmType,Div,PF,Pts,%,Time,% psbl,HitFactor,A,C,D,M,NPM,NS,Proc,Apen,OverallRanking,ClubRanking,IsVisitor
                                    0,string,string,0,string,string,string,string,string,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false
                                    """)))
            @RequestBody String csvData)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscMatchCompetitorService.createMatchCompetitors(csvData));
    }

    /**
     * Fully replaces an existing match competitor's fields with those on the request.
     *
     * @param matchCompetitorId the identifier of the match competitor to replace.
     * @param request           the match competitor's replacement fields.
     * @return the updated {@link MatchCompetitorResponse}.
     * @throws ValidationException if a required field is missing, an enumerated value is unrecognised, or the
     *                             replacement would duplicate another entry for the competitor, match and firearm
     *                             type.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists, or the competitor
     *                             or match cannot be found.
     */
    @PutMapping(value = "/{matchCompetitorId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace match competitor", description = "Fully replace an existing match competitor's "
            + "fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match competitor replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchCompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, an enumerated value is "
                    + "unrecognised, or the replacement would duplicate another entry.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match competitor with this ID, or the competitor "
                    + "or match, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchCompetitorResponse> updateMatchCompetitor(
            @Parameter(description = "Identifier of the match competitor to replace.")
            @PathVariable Long matchCompetitorId,
            @RequestBody MatchCompetitorRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscMatchCompetitorService.updateMatchCompetitor(matchCompetitorId, request));
    }

    /**
     * Partially updates an existing match competitor, applying only the non-null fields on the request.
     *
     * @param matchCompetitorId the identifier of the match competitor to update.
     * @param request           the fields to change; any field left {@code null} is left unchanged.
     * @return the updated {@link MatchCompetitorResponse}.
     * @throws ValidationException if an enumerated value is unrecognised, or the result would duplicate another
     *                             entry for the competitor, match and firearm type.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists, or a changed
     *                             competitor or match cannot be found.
     */
    @PatchMapping(value = "/{matchCompetitorId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update match competitor", description = "Partially update an existing match competitor; "
            + "only non-null fields are applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match competitor updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchCompetitorResponse.class))),
            @ApiResponse(responseCode = "400", description = "An enumerated value is unrecognised, or the result "
                    + "would duplicate another entry.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match competitor with this ID, or a changed "
                    + "competitor or match, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchCompetitorResponse> patchMatchCompetitor(
            @Parameter(description = "Identifier of the match competitor to update.")
            @PathVariable Long matchCompetitorId,
            @RequestBody MatchCompetitorPatchRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscMatchCompetitorService.patchMatchCompetitor(matchCompetitorId, request));
    }

    /**
     * Retrieves an existing match competitor.
     *
     * @param matchCompetitorId the identifier of the match competitor to retrieve.
     * @return the {@link MatchCompetitorResponse}.
     * @throws NonFatalException if no match competitor with {@code matchCompetitorId} exists.
     */
    @GetMapping(value = "/{matchCompetitorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get match competitor", description = "Retrieve a match competitor by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match competitor found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchCompetitorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match competitor with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchCompetitorResponse> getMatchCompetitor(
            @Parameter(description = "Identifier of the match competitor to retrieve.")
            @PathVariable Long matchCompetitorId)
            throws NonFatalException {
        return ResponseEntity.ok(ipscMatchCompetitorService.getMatchCompetitor(matchCompetitorId));
    }

    /**
     * Retrieves every match competitor.
     *
     * @return the list of {@link MatchCompetitorResponse}.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all match competitors", description = "Retrieve every match competitor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match competitors retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MatchCompetitorResponse.class))))
    })
    ResponseEntity<List<MatchCompetitorResponse>> getAllMatchCompetitors() {
        return ResponseEntity.ok(ipscMatchCompetitorService.getAllMatchCompetitors());
    }

    /**
     * Deletes an existing match competitor.
     *
     * @param matchCompetitorId the identifier of the match competitor to delete.
     * @return an empty {@code 204 No Content} response.
     * @throws ValidationException if the match competitor is still referenced by other records.
     * @throws NonFatalException   if no match competitor with {@code matchCompetitorId} exists.
     */
    @DeleteMapping(value = "/{matchCompetitorId}")
    @Operation(summary = "Delete match competitor", description = "Delete a match competitor by ID. One still "
            + "referenced by other records, such as stage scores, can't be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match competitor deleted."),
            @ApiResponse(responseCode = "400", description = "The match competitor is still referenced by other "
                    + "records.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match competitor with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<Void> deleteMatchCompetitor(
            @Parameter(description = "Identifier of the match competitor to delete.")
            @PathVariable Long matchCompetitorId)
            throws ValidationException, NonFatalException {
        ipscMatchCompetitorService.deleteMatchCompetitor(matchCompetitorId);
        return ResponseEntity.noContent().build();
    }
}
