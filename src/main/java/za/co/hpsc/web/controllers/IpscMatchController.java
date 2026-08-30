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
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;

/**
 * Controller responsible for handling IPSC match CRUD API endpoints.
 *
 * <p>
 * Provides endpoints for creating, fully or partially updating and retrieving IPSC matches
 * together with their stages.
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
     * Creates a new IPSC match together with any stages supplied on the request.
     *
     * @param request the match to create.
     * @return the created {@link MatchResponse}, including its generated ID and any persisted stages.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if the named club cannot be found.
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create match", description = "Create a new IPSC match, optionally together with its stages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "The named club could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> createMatch(@RequestBody MatchRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscMatchService.createMatch(request));
    }

    /**
     * Fully replaces an existing IPSC match's fields and stages with those on the request.
     *
     * @param matchId the identifier of the match to replace.
     * @param request the match's replacement fields.
     * @return the updated {@link MatchResponse}, including its persisted stages.
     * @throws ValidationException if a required field is missing.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     */
    @PutMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace match", description = "Fully replace an existing IPSC match's fields and stages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "A required field is missing.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID, or the named club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> updateMatch(
            @Parameter(description = "Identifier of the match to replace.") @PathVariable Long matchId,
            @RequestBody MatchRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscMatchService.updateMatch(matchId, request));
    }

    /**
     * Partially updates an existing IPSC match, applying only the non-null fields on the
     * request.
     *
     * @param matchId the identifier of the match to update.
     * @param request the fields to change; any field left {@code null} is left unchanged.
     * @return the updated {@link MatchResponse}, including its persisted stages.
     * @throws ValidationException if the named club is blank.
     * @throws NonFatalException   if no match with {@code matchId} exists, or the named club
     *                             cannot be found.
     */
    @PatchMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update match", description = "Partially update an existing IPSC match; only non-null fields are applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "The named club is blank.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID, or the named club, could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResponse> patchMatch(
            @Parameter(description = "Identifier of the match to update.") @PathVariable Long matchId,
            @RequestBody MatchRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscMatchService.patchMatch(matchId, request));
    }

    /**
     * Retrieves an existing IPSC match together with its stages.
     *
     * @param matchId the identifier of the match to retrieve.
     * @return the {@link MatchResponse}, including its persisted stages.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    @GetMapping(value = "/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get match", description = "Retrieve an IPSC match by ID, together with its stages.")
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
     * Retrieves every IPSC match together with its stages.
     *
     * @return the list of {@link MatchResponse}, each including its persisted stages.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all matches", description = "Retrieve every IPSC match, together with its stages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResponse.class)))
    })
    ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(ipscMatchService.getAllMatches());
    }
}
