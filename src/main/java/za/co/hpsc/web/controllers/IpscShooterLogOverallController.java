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
@RequestMapping("/ipsc/shooter-log-overalls")
@Tag(name = "IPSC Shooter Log Overalls", description = "IPSC Shooter Log Overalls API")
public class IpscShooterLogOverallController {
    private final IpscShooterLogOverallService ipscShooterLogOverallService;

    public IpscShooterLogOverallController(IpscShooterLogOverallService ipscShooterLogOverallService) {
        this.ipscShooterLogOverallService = ipscShooterLogOverallService;
    }

    /**
     * Calculates the overalls for one shooter log, creating or updating one for each of its competitors.
     *
     * @param shooterLogId the identifier of the shooter log to calculate overalls for.
     * @return the {@link ShooterLogOverallResponseHolder} linking the shooter log to its freshly calculated overalls.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @PostMapping(value = "/logs/{shooterLogId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculate overalls for shooter log", description = "Calculate every competitor's overall "
            + "standing in a shooter log: the average of their best four percentages across the log's matches, "
            + "ranked within their division and competitor category (no category is a group of its own). The "
            + "matches' shooter results are brought up to date first. Calculating again updates the existing "
            + "overalls.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overalls calculated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogOverallResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogOverallResponseHolder> createShooterLogOveralls(
            @Parameter(description = "Identifier of the shooter log to calculate overalls for.")
            @PathVariable Long shooterLogId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterLogOverallService.calculateShooterLogOverall(shooterLogId));
    }

    /**
     * Retrieves the overalls for one shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to retrieve overalls for.
     * @return the {@link ShooterLogOverallResponseHolder} linking the shooter log to its overalls.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @GetMapping(value = "/logs/{shooterLogId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get overalls for shooter log", description = "Retrieve every competitor's overall "
            + "standing in a shooter log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overalls retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogOverallResponseHolder.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogOverallResponseHolder> getShooterLogOveralls(
            @Parameter(description = "Identifier of the shooter log to retrieve overalls for.")
            @PathVariable Long shooterLogId) throws NonFatalException {
        return ResponseEntity.ok(ipscShooterLogOverallService.getShooterLogOveralls(shooterLogId));
    }

    /**
     * Retrieves the overalls for every shooter log.
     *
     * @return one {@link ShooterLogOverallResponseHolder} per shooter log, earliest start date first, including
     * those without overalls.
     * @since 16.1.0
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all shooter log overalls", description = "Retrieve every competitor's overall "
            + "standing in every shooter log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log overalls retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(
                                    implementation = ShooterLogOverallResponseHolder.class))))
    })
    ResponseEntity<List<ShooterLogOverallResponseHolder>> getAllShooterLogOveralls() {
        return ResponseEntity.ok(ipscShooterLogOverallService.getAllShooterLogOveralls());
    }
}
