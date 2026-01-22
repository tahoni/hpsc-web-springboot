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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.ImageRequest;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;

@Controller
@RequestMapping("/match")
@Tag(name = "Match", description = "API for match-relate functionality.")
public class MatchController {
    @PostMapping(value = "/processWinMssCab")
    @Operation(summary = "Process a WinMSS.cab file", description = "Convert the WinMSS.cab file to JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            MatchResultLogResponseHolder.class)))
    })
    ResponseEntity processWinMssCab(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType =
                    "application/json",
                    schema = @Schema(implementation = ImageRequest.class)
            ))
            @RequestBody String cabFileContent)
            throws ValidationException, FatalException {
        return ResponseEntity.ok().build();
    }
}
