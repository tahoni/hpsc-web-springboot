package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@RestController("/award")
@Tag(name = "Award", description = "API for award-related functionality.")
public class AwardController {
}
