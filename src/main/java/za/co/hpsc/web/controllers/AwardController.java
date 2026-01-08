package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import za.co.hpsc.web.models.AwardRequest;
import za.co.hpsc.web.models.AwardResponseHolder;
import za.co.hpsc.web.services.AwardService;

@Controller
@RequestMapping("/award")
@Tag(name = "Award", description = "API for award-related functionality.")
public class AwardController {
    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @PostMapping(value = "/processCsv")
    @Operation(summary = "Process award CSV", description = "Convert CSV data about awards to JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AwardResponseHolder.class)))
    })
    ResponseEntity<AwardResponseHolder> processCsv(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "text/csv",
                    schema = @Schema(implementation = AwardRequest.class),
                    examples = @ExampleObject("""
                            
                            
                            """)))
            @RequestBody String csvData)
            throws ValidationException, FatalException {
        return ResponseEntity.ok(awardService.processCsv(csvData));
    }
}
