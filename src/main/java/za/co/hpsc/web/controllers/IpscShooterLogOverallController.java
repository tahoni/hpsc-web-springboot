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
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.services.IpscShooterLogOverallService;

import java.util.List;

/**
 * Controller responsible for handling IPSC shooter log overall API endpoints.
 *
 * <p>
 * Provides endpoints for calculating each competitor's overall standing in a shooter log, and for retrieving them for
 * a shooter log, or for every shooter log.
 * </p>
 *
 * @since 16.1.0
 */
@Controller
@RequestMapping("/ipsc/shooter-log-overall-results")
@Tag(name = "IPSC Shooter Log Overall Results", description = "IPSC Shooter Log Overall Results API")
public class IpscShooterLogOverallController {
    private final IpscShooterLogOverallService ipscShooterLogOverallService;

    public IpscShooterLogOverallController(IpscShooterLogOverallService ipscShooterLogOverallService) {
        this.ipscShooterLogOverallService = ipscShooterLogOverallService;
    }

    /**
     * Calculates the overall results for one shooter log, creating or updating one for each of its verified
     * competitors.
     *
     * @param shooterLogId the identifier of the shooter log to calculate overall results for.
     * @return the {@link ShooterLogOverallResponseHolder} linking the shooter log to its freshly calculated overall
     * results.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @PostMapping(value = "/logs/{shooterLogId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculate overall results for shooter log",
            description = "Calculate every verified competitor's "
            + "overall standing in a shooter log: the average of their best four percentages across the log's "
            + "matches, ranked within their division and competitor category (no category is a group of its own). "
            + "Competitors who are not verified are not included. Every match must be scheduled within the log's "
            + "dates, which is checked again as a match can be rescheduled after it was added. The matches' shooter "
            + "results are brought up to date first. Calculating again updates the existing overall results and "
            + "removes those of competitors who no longer qualify.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overall results calculated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogOverallResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "A match the log covers is scheduled outside the log's "
                    + "dates.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogOverallResponseHolder> createShooterLogOverallResults(
            @Parameter(description = "Identifier of the shooter log to calculate overall results for.")
            @PathVariable Long shooterLogId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterLogOverallService.calculateShooterLogOverall(shooterLogId));
    }

    /**
     * Retrieves the overall results for one shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to retrieve overall results for.
     * @return the {@link ShooterLogOverallResponseHolder} linking the shooter log to its overall results.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @GetMapping(value = "/logs/{shooterLogId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get overall results for shooter log", description = "Retrieve every verified competitor's "
            + "overall standing in a shooter log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overall results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogOverallResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogOverallResponseHolder> getShooterLogOverallResults(
            @Parameter(description = "Identifier of the shooter log to retrieve overall results for.")
            @PathVariable Long shooterLogId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterLogOverallService.getShooterLogOverallResults(shooterLogId));
    }

    /**
     * Retrieves the overall results for every shooter log.
     *
     * @return one {@link ShooterLogOverallResponseHolder} per shooter log, earliest start date first, including
     * those without overall results.
     * @since 16.1.0
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all shooter log overall results", description = "Retrieve every verified competitor's "
            + "overall standing in every shooter log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overall results retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(
                                    implementation = ShooterLogOverallResponseHolder.class))))
    })
    ResponseEntity<List<ShooterLogOverallResponseHolder>> getAllShooterLogOverallResults() {
        return ResponseEntity.ok(ipscShooterLogOverallService.getAllShooterLogOverallResults());
    }
}
