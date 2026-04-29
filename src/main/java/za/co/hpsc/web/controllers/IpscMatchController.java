package za.co.hpsc.web.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
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
    void insertMatch(@RequestBody MatchResponse matchResponse) {
        ipscMatchService.insertMatch(matchResponse);
    }

    @PutMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateMatch(@PathVariable String matchId, @RequestBody MatchResponse matchResponse) {
        ipscMatchService.updateMatch(matchId, matchResponse);
    }

    @PatchMapping(value = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void modifyMatch(@PathVariable String matchId, @RequestBody MatchResponse matchResponse) {
        ipscMatchService.modifyMatch(matchId, matchResponse);
    }

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<MatchResponse> getMatches(@RequestBody MatchSearchRequest matchSearchRequest) {
        return ipscMatchService.getMatches(matchSearchRequest);
    }

    @GetMapping(value = "{matchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    MatchResponse getMatch(@PathVariable Integer matchId) {
        return ipscMatchService.getMatch(matchId);
    }
}
