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
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;

/**
 * Controller responsible for handling IPSC match CRUD API endpoints.
 *
 * <p>
 * Provides endpoints for creating, fully or partially updating, retrieving and deleting IPSC
 * matches.
 * </p>
 *
 * @since 8.0.0
 */
@Controller
@RequestMapping("/ipsc/matches")
@Tag(name = "IPSC Match", description = "IPSC Match API")
public class IpscMatchController {
    private final IpscMatchService ipscMatchService;

    public IpscMatchController(IpscMatchService ipscMatchService) {
        this.ipscMatchService = ipscMatchService;
    }

    /**
     * Creates a new IPSC match.
     *
     * @param request the match to create.
     * @return the created {@link MatchResponse}, including its generated ID.
     * @throws ValidationException if a required field is missing, or the firearm type/category is unrecognised.
     * @throws NonFatalException   if the named club cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create match", description = "Create a new IPSC match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, or the firearm type/category is unrecognised.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "The named club could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while resolving the default match club.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> createMatch(@RequestBody MatchRequest request)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscMatchService.createMatch(request));
    }

    /**
     * Creates a batch of new IPSC matches from CSV data.
     *
     * @param csvData the CSV content as a string containing details about matches, formatted
     *                according to the expected schema. This parameter is required and cannot be
     *                null.
     * @return a {@link MatchResponseHolder} containing the created matches.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed, if a row is
     *                             missing a required field, if a row's firearm type/category is
     *                             unrecognised.
     * @throws NonFatalException   if a row's named club cannot be found.
     * @throws FatalException      if a critical error occurs during processing, that prevents the
     *                             operation from completing successfully — including a row naming no club while
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    @PostMapping(value = "/bulk", consumes = "text/plain", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create matches", description = "Create IPSC matches in bulk from CSV data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matches created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid CSV data provided, a required field is "
                    + "missing or the firearm type/category is unrecognised.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "A row's named club could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the CSV data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponseHolder> createMatches(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject("""
                                    MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                                    yyyy-MM-dd,string,string,string,string,HH:mm,HH:mm,string
                                    """)))
            @RequestBody String csvData)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscMatchService.createMatches(csvData));
    }

    /**
     * Fully replaces an existing IPSC match's fields with those on the request.
     *
     * @param matchId the identifier of the match to replace.
     * @param request the match's replacement fields.
     * @return the updated {@link MatchResponse}.
     * @throws ValidationException if a required field is missing, or the firearm type/category is unrecognised.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     * @throws FatalException      if no club is named and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    @PutMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace match", description = "Fully replace an existing IPSC match's fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing, or the firearm type/category is unrecognised.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID, or the named club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while resolving the default match club.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> updateMatch(
            @Parameter(description = "Identifier of the match to replace.") @PathVariable Long matchId,
            @RequestBody MatchRequest request)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.ok(ipscMatchService.updateMatch(matchId, request));
    }

    /**
     * Partially updates an existing IPSC match, applying only the non-null fields on the
     * request.
     *
     * @param matchId the identifier of the match to update.
     * @param request the fields to change; any field left {@code null} is left unchanged.
     * @return the updated {@link MatchResponse}.
     * @throws ValidationException if the named club is blank, or the firearm type/category is unrecognised.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     * @throws FatalException      if the request's {@code club} is blank and
     *                             {@link za.co.hpsc.web.constants.IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     *                             is null.
     */
    @PatchMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update match", description = "Partially update an existing IPSC match; only non-null fields are applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "The named club is blank, or the firearm type/category is unrecognised.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID, or the named club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while resolving the default match club.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> patchMatch(
            @Parameter(description = "Identifier of the match to update.") @PathVariable Long matchId,
            @RequestBody MatchPatchRequest request)
            throws ValidationException, NonFatalException, FatalException {
        return ResponseEntity.ok(ipscMatchService.patchMatch(matchId, request));
    }

    /**
     * Retrieves an existing IPSC match.
     *
     * @param matchId the identifier of the match to retrieve.
     * @return the {@link MatchResponse}.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    @GetMapping(value = "/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get match", description = "Retrieve an IPSC match by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> getMatch(
            @Parameter(description = "Identifier of the match to retrieve.") @PathVariable Long matchId)
            throws NonFatalException {
        return ResponseEntity.ok(ipscMatchService.getMatch(matchId));
    }

    /**
     * Retrieves every IPSC match.
     *
     * @return the list of {@link MatchResponse}.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all matches", description = "Retrieve every IPSC match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MatchResponse.class))))
    })
    ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(ipscMatchService.getAllMatches());
    }

    /**
     * Deletes an existing IPSC match.
     *
     * @param matchId the identifier of the match to delete.
     * @return an empty {@code 204 No Content} response.
     * @throws ValidationException if the match still has competitor results or shooter-log
     *                             entries.
     * @throws NonFatalException   if no match with {@code matchId} exists.
     */
    @DeleteMapping(value = "/{matchId}")
    @Operation(summary = "Delete match", description = "Delete an IPSC match by ID. A match "
            + "with recorded competitor results or shooter-log entries can't be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match deleted."),
            @ApiResponse(responseCode = "400", description = "The match has recorded competitor results or "
                    + "shooter-log entries.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<Void> deleteMatch(
            @Parameter(description = "Identifier of the match to delete.") @PathVariable Long matchId)
            throws ValidationException, NonFatalException {
        ipscMatchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }
}
