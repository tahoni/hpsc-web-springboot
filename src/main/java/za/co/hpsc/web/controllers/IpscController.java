package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.request.IpscRequestHolder;
import za.co.hpsc.web.services.WinMssService;

/**
 * Controller responsible for handling IPSC-related API endpoints.
 *
 * <p>
 * This class provides the necessary operations to process and store data related
 * to IPSC, e.g. data provided in WinMSS.cab files.
 * </p>
 */
@Controller
@RequestMapping("/ipsc")
@Tag(name = "IPSC", description = "API for functionality related to IPSC directly.")
public class IpscController {
    private final WinMssService winMssService;

    public IpscController(WinMssService winMssService) {
        this.winMssService = winMssService;
    }

    /**
     * Processes the content of a WinMSS.cab file contained in a JSON structure and saves the data.
     *
     * <p>
     * This method is exposed as a POST endpoint that accepts raw WinMSS.cab file content
     * in JSON format. It validates and processes the provided data and returns an appropriate
     * response indicating success, validation errors, or server errors.
     * </p>
     *
     * @param cabFileContent the JSON-formatted content of the WinMSS.cab file to be processed.
     *                       Must not be null or empty.
     * @return a {@link ControllerResponse} object if the processing is successful.
     */
    @PostMapping(value = "/importWinMssCabData", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully imported the WinMSS.cab file data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IpscMatchRecordHolder.class))),
            @ApiResponse(responseCode = "400", description = "Invalid WinMSS.cab file data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class))),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred importing the WinMSS.cab file data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControllerResponse.class)))
    })
    @Operation(summary = "Import WinMSS.cab file", description = "Import and persist WinMSS.cab content.")
    ResponseEntity<IpscMatchRecordHolder> importWinMssCabData(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IpscRequestHolder.class)
                    ))
            @RequestBody String cabFileContent)
            throws ValidationException, FatalException {
        return ResponseEntity.ok(winMssService.importWinMssCabFile(cabFileContent));
    }
}
