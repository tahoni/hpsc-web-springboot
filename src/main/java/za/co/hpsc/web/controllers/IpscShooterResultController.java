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
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.services.IpscShooterResultService;

import java.util.List;

/**
 * Controller responsible for handling IPSC shooter result API endpoints.
 *
 * <p>
 * Provides endpoints for calculating each competitor's result in a match, and for retrieving them for a match, or
 * for every match.
 * </p>
 *
 * @since 15.0.0
 */
@Controller
@RequestMapping("/ipsc/shooter-results")
@Tag(name = "IPSC Shooter Results", description = "IPSC Match Shooter Results API")
public class IpscShooterResultController {
    private final IpscShooterResultService ipscShooterResultService;

    public IpscShooterResultController(IpscShooterResultService ipscShooterResultService) {
        this.ipscShooterResultService = ipscShooterResultService;
    }

    /**
     * Calculates the shooter results for one match, creating or updating one for each of its competitors.
     *
     * @param matchId the identifier of the match to calculate results for.
     * @return the {@link ShooterResponseHolder} linking the match to its freshly calculated shooter results.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 15.0.0
     */
    @PostMapping(value = "/matches/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculate shooter results for match", description = "Calculate every competitor's result "
            + "in a match: each is ranked by match points within their firearm type, division and competitor "
            + "category (no category is a group of its own), with their percentage set relative to the group "
            + "winner. Calculating again updates the existing results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter results calculated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterResponseHolder> createShooterResults(
            @Parameter(description = "Identifier of the match to calculate results for.")
            @PathVariable Long matchId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterResultService.calculateMatchShooterResult(matchId));
    }

    /**
     * Retrieves the shooter results for one match.
     *
     * @param matchId the identifier of the match to retrieve results for.
     * @return the {@link ShooterResponseHolder} linking the match to its shooter results.
     * @throws NonFatalException if no match with {@code matchId} exists.
     * @since 15.0.0
     */
    @GetMapping(value = "/matches/{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get shooter results for match", description = "Retrieve every shooter's result in a match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No match with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterResponseHolder> getShooterResults(
            @Parameter(description = "Identifier of the match to retrieve results for.")
            @PathVariable Long matchId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterResultService.getShooterResults(matchId));
    }

    /**
     * Retrieves the shooter results for every match.
     *
     * @return one {@link ShooterResponseHolder} per match, oldest match date first, including those without
     * results.
     * @since 15.0.0
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all shooter results", description = "Retrieve every shooter's result in every match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ShooterResponseHolder.class))))
    })
    ResponseEntity<List<ShooterResponseHolder>> getAllShooterResults() {
        return ResponseEntity.ok(ipscShooterResultService.getAllShooterResults());
    }
}