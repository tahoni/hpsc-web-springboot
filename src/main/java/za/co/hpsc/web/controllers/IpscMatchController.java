package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.shared.MatchWithStages;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;

@Controller
@RequestMapping("ipsc/matches")
@Tag(name = "IPSC Matches", description = "Operations pertaining to IPSC matches")
public final class IpscMatchController {
    private final IpscMatchService ipscMatchService;

    IpscMatchController(IpscMatchService ipscMatchService) {
        this.ipscMatchService = ipscMatchService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void insertMatch(@RequestBody MatchWithStages matchWithStages) throws FatalException {
        ipscMatchService.insertMatch(matchWithStages);
    }

    @PutMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateMatch(@PathVariable Long matchId, @RequestBody MatchWithStages matchWithStages) {
        ipscMatchService.updateMatch(matchId, matchWithStages);
    }

    @PatchMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void modifyMatch(@PathVariable Long matchId, @RequestBody MatchWithStages matchWithStages) {
        ipscMatchService.modifyMatch(matchId, matchWithStages);
    }

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<MatchWithStages> getMatches(@RequestBody MatchWithStages matchWithStages) {
        return ipscMatchService.getMatches(matchWithStages);
    }

    @GetMapping(value = "{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    MatchWithStages getMatch(@PathVariable Long matchId) {
        return ipscMatchService.getMatch(matchId).orElse(null);
    }
}
