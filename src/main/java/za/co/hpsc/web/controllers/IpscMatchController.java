package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
import za.co.hpsc.web.services.IpscMatchService;

@Controller
@RequestMapping("/v2/ipsc/matches")
@Tag(name = "IPSC Matches", description = "Operations pertaining to IPSC matches")
public final class IpscMatchController {
    private final IpscMatchService ipscMatchService;

    IpscMatchController(IpscMatchService ipscMatchService) {
        this.ipscMatchService = ipscMatchService;
    }

    @Operation(
            summary = "Create a new IPSC match",
            description = "Creates and persists a new match with optional stages payload."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to process match creation",
                    content = @Content)
    })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MatchOnlyResponse> insertMatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Match payload including stages",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MatchOnlyRequest.class))
            )
            @RequestBody MatchOnlyRequest matchOnlyRequest)
            throws FatalException {
        return ResponseEntity.ok(ipscMatchService.insertMatch(matchOnlyRequest)
                .orElseThrow(() -> new FatalException("Failed to create match")));
    }

    @Operation(
            summary = "Fully update an IPSC match",
            description = "Replaces the current state of an existing match by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Match not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to process match update",
                    content = @Content)
    })
    @PutMapping(value = "{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MatchOnlyResponse> updateMatch(
            @Parameter(description = "Unique identifier of the match", required = true, example = "123")
            @PathVariable Long matchId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Full replacement payload for the match",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MatchOnlyRequest.class))
            )
            @RequestBody MatchOnlyRequest matchOnlyRequest)
            throws FatalException {
        return ResponseEntity.ok(ipscMatchService.updateMatch(matchId, matchOnlyRequest).orElse(null));
    }

    @Operation(
            summary = "Partially update an IPSC match",
            description = "Applies partial modifications to an existing match by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match modified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Match not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to process match modification",
                    content = @Content)
    })
    @PatchMapping(value = "{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MatchOnlyResponse> modifyMatch(
            @Parameter(description = "Unique identifier of the match", required = true, example = "123")
            @PathVariable Long matchId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Partial payload with fields to modify",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MatchOnlyRequest.class))
            )
            @RequestBody MatchOnlyRequest matchOnlyRequest)
            throws FatalException {


        return ResponseEntity.ok(ipscMatchService.modifyMatch(matchId, matchOnlyRequest)
                .orElseThrow(() -> new FatalException("Failed to modify match")));
    }

    @Operation(
            summary = "Get IPSC match by id",
            description = "Returns a single match when it exists."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MatchOnlyRequest.class))),
            @ApiResponse(responseCode = "404", description = "Match not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve match",
                    content = @Content)
    })
    @GetMapping(value = "/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MatchOnlyResponse> getMatch(
            @Parameter(description = "Unique identifier of the match", required = true, example = "123")
            @PathVariable Long matchId) throws FatalException {
        return ResponseEntity.ok(ipscMatchService.getMatch(matchId)
                .orElseThrow(() -> new FatalException("Failed to retrieve match")));
    }
}