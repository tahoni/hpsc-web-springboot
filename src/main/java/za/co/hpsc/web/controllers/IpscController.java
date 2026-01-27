package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;
import za.co.hpsc.web.services.IpscService;

import java.io.IOException;

@Controller
@RequestMapping("/ipsc")
@Tag(name = "IPSC", description = "API for functionality related to IPSC directly.")
public class IpscController {
    private final IpscService ipscService;

    public IpscController(IpscService ipscService) {
        this.ipscService = ipscService;
    }

    @PostMapping(value = "/processWinMssCab", consumes = "multipart/form-data",
            produces = "application/json")
    @Operation(summary = "Process a WinMSS.cab file", description = "Save the data in the WinMSS.cab file " +
            "to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the WinMSS.cab file data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            MatchResultLogResponseHolder.class))),
            @ApiResponse(responseCode = "400", description = "Not a valid WinMSS.cab file.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred processing " +
                    "the WinMSS.cab file.", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<MatchResultLogResponseHolder> processWinMssCab(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType =
                    "multipart/form-data"
            ))
            @RequestParam("file") MultipartFile cabFile)
            throws ValidationException, FatalException, IOException {
        return ResponseEntity.ok(ipscService.processWinMssCabFile(cabFile));
    }
}
