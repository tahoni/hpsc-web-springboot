package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponseHolder;
import za.co.hpsc.web.services.IpscMatchResultService;

import java.util.List;

/**
 * Controller responsible for handling IPSC match result API endpoints.
 *
 * <p>
 * Provides endpoints for calculating each verified competitor's result in a match, and for retrieving them for a
 * match, or for every match.
 * </p>
 *
 * @since 18.1.0
 */
@Controller
@RequestMapping("/ipsc/match-results")
@Tag(name = "IPSC Match Results", description = "IPSC Match Results API")
public class IpscMatchResultController {
    private final IpscMatchResultService ipscMatchResultService;

    public IpscMatchResultController(IpscMatchResultService ipscMatchResultService) {
        this.ipscMatchResultService = ipscMatchResultService;
    }

    /**
     * Calculates the match results for one match, creating or updating one for each of its verified competitors with
     * points above zero.
     *
     * @param matchId the identifier of the match to calculate results for.
     * @return the {@link MatchResultResponseHolder} linking the match to its freshly calculated match results.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 18.1.0
     */
    @PostMapping(value = "/matches/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculate match results for match", description = "Calculate the result of "
            + "every verified competitor with points above zero in a match: each is ranked by match points "
            + "across the whole match, regardless of firearm type, division or competitor category, with their "
            + "percentage set relative to the match winner. Competitors who are not verified, or have no points, get "
            + "no result. Calculating again updates the existing results and removes those of competitors who no "
            + "longer qualify.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match results calculated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResultResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResultResponseHolder> createMatchResults(
            @Parameter(description = "Identifier of the match to calculate results for.")
            @PathVariable Long matchId) throws NonFatalException {
        return ResponseEntity.ok(ipscMatchResultService.calculateMatchResults(matchId));
    }

    /**
     * Retrieves the match results for one match.
     *
     * @param matchId the identifier of the match to retrieve results for.
     * @return the {@link MatchResultResponseHolder} linking the match to its match results.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 18.1.0
     */
    @GetMapping(value = "/matches/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get match results for match",
            description = "Retrieve every calculated match result in a match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MatchResultResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<MatchResultResponseHolder> getMatchResults(
            @Parameter(description = "Identifier of the match to retrieve results for.")
            @PathVariable Long matchId) throws NonFatalException {
        return ResponseEntity.ok(ipscMatchResultService.getMatchResults(matchId));
    }

    /**
     * Retrieves the match results for every match.
     *
     * @return one {@link MatchResultResponseHolder} per match, oldest match date first, including those without
     * results.
     * @since 18.1.0
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all match results",
            description = "Retrieve every calculated match result in every match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MatchResultResponseHolder.class))))
    })
    ResponseEntity<List<MatchResultResponseHolder>> getAllMatchResults() {
        return ResponseEntity.ok(ipscMatchResultService.getAllMatchResults());
    }
}