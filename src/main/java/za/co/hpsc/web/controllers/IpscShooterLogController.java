package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.services.IpscShooterLogService;

import java.util.List;

/**
 * Controller responsible for handling IPSC shooter log CRUD API endpoints.
 *
 * <p>
 * Provides endpoints for creating, fully or partially updating, retrieving and deleting shooter logs, each a date range
 * covering a set of matches.
 * </p>
 *
 * @since 16.1.0
 */
@Controller
@RequestMapping("/ipsc/shooter-logs")
@Tag(name = "IPSC Shooter Logs", description = "IPSC Shooter Logs API")
public class IpscShooterLogController {
    private final IpscShooterLogService ipscShooterLogService;

    public IpscShooterLogController(IpscShooterLogService ipscShooterLogService) {
        this.ipscShooterLogService = ipscShooterLogService;
    }

    /**
     * Creates a new shooter log.
     *
     * @param request the shooter log to create.
     * @return the created {@link ShooterLogResponse}, including its generated ID.
     * @throws ValidationException if a date is missing, the end date is before the start date or a match is
     *                             scheduled outside the log's dates.
     * @throws NonFatalException   if a match in the request cannot be found.
     * @since 16.1.0
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create shooter log", description = "Create a new shooter log: a date range and the "
            + "matches it covers, set by match ID. Each match must be scheduled within the date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shooter log created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "A date is missing, the end date is before the "
                    + "start date, or a match is scheduled outside the log's dates.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "A match could not be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogResponse> createShooterLog(@Valid @RequestBody ShooterLogRequest request)
            throws ValidationException, NonFatalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipscShooterLogService.createShooterLog(request));
    }

    /**
     * Fully replaces an existing shooter log's dates and matches with those on the request.
     *
     * @param shooterLogId the identifier of the shooter log to replace.
     * @param request      the shooter log's replacement dates and matches.
     * @return the updated {@link ShooterLogResponse}.
     * @throws ValidationException if a date is missing, the end date is before the start date or a match is
     *                             scheduled outside the log's dates.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists, or a match cannot be found.
     * @since 16.1.0
     */
    @PutMapping(value = "/{shooterLogId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace shooter log", description = "Fully replace an existing shooter log's dates and "
            + "matches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log replaced.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "A date is missing, the end date is before the "
                    + "start date, or a match is scheduled outside the log's dates.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID, or a match, could be "
                    + "found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogResponse> updateShooterLog(
            @Parameter(description = "Identifier of the shooter log to replace.") @PathVariable Long shooterLogId,
            @Valid @RequestBody ShooterLogRequest request) throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscShooterLogService.updateShooterLog(shooterLogId, request));
    }

    /**
     * Partially updates an existing shooter log, applying only the non-null fields on the request.
     *
     * @param shooterLogId the identifier of the shooter log to update.
     * @param request      the fields to change; any field left {@code null} is left unchanged.
     * @return the updated {@link ShooterLogResponse}.
     * @throws ValidationException if the log's end date would end up before its start date, or a match would
     *                             be scheduled outside its dates.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists, or a match cannot be found.
     * @since 16.1.0
     */
    @PatchMapping(value = "/{shooterLogId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update shooter log", description = "Partially update an existing shooter log; only "
            + "non-null fields are applied, and a supplied list of match IDs replaces the log's matches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "The end date would be before the start date, or a "
                    + "match would be scheduled outside the log's dates.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID, or a match, could be "
                    + "found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogResponse> patchShooterLog(
            @Parameter(description = "Identifier of the shooter log to update.") @PathVariable Long shooterLogId,
            @Valid @RequestBody ShooterLogPatchRequest request) throws ValidationException, NonFatalException {
        return ResponseEntity.ok(ipscShooterLogService.patchShooterLog(shooterLogId, request));
    }

    /**
     * Retrieves an existing shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to retrieve.
     * @return the {@link ShooterLogResponse}.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @GetMapping(value = "/{shooterLogId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get shooter log", description = "Retrieve a shooter log by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter log found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShooterLogResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<ShooterLogResponse> getShooterLog(
            @Parameter(description = "Identifier of the shooter log to retrieve.") @PathVariable Long shooterLogId)
            throws NonFatalException {
        return ResponseEntity.ok(ipscShooterLogService.getShooterLog(shooterLogId));
    }

    /**
     * Retrieves every shooter log.
     *
     * @return the list of {@link ShooterLogResponse}, earliest start date first.
     * @since 16.1.0
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all shooter logs", description = "Retrieve every shooter log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shooter logs retrieved.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ShooterLogResponse.class))))
    })
    ResponseEntity<List<ShooterLogResponse>> getAllShooterLogs() {
        return ResponseEntity.ok(ipscShooterLogService.getAllShooterLogs());
    }

    /**
     * Deletes an existing shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to delete.
     * @return an empty {@code 204 No Content} response.
     * @throws ValidationException if the shooter log still has overall results.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    @DeleteMapping(value = "/{shooterLogId}")
    @Operation(summary = "Delete shooter log", description = "Delete a shooter log by ID. A shooter log with "
            + "overall results can't be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shooter log deleted."),
            @ApiResponse(responseCode = "400", description = "The shooter log has overall results.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "404", description = "No shooter log with this ID could be found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    ResponseEntity<Void> deleteShooterLog(
            @Parameter(description = "Identifier of the shooter log to delete.") @PathVariable Long shooterLogId)
            throws ValidationException, NonFatalException {
        ipscShooterLogService.deleteShooterLog(shooterLogId);
        return ResponseEntity.noContent().build();
    }
}
