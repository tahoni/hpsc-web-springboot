package za.co.hpsc.web.models.ipsc.match.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyDto {
    private Long id;
    private ClubDto club;
    private String clubName;

    @NotNull
    private String name = "";
    @NotNull
    private LocalDateTime scheduledDate;

    private FirearmType matchFirearmType;
    private MatchCategory matchCategory;

    private LocalDateTime dateEdited;

    public void init(MatchOnlyResponse matchOnlyResponse) {
        init((MatchOnlyRequest) matchOnlyResponse);
    }

    public void init(MatchOnlyRequest matchOnlyRequest) {
        if (matchOnlyRequest != null) {
            // Initialises match details
            this.id = matchOnlyRequest.getMatchId();
            this.clubName = matchOnlyRequest.getClub();

            // Initialises the match attributes
            this.name = matchOnlyRequest.getMatchName();
            this.scheduledDate = matchOnlyRequest.getMatchDate();

            // Determines the firearm type based on the firearm ID
            this.matchFirearmType = FirearmType.getByName(matchOnlyRequest.getFirearm())
                    .orElse(this.matchFirearmType);
            this.matchCategory = IpscConstants.DEFAULT_MATCH_CATEGORY;

            // Sets the date edited to the current date and time
            this.dateEdited = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        String clubString = this.clubName;
        String nameString = ((this.name != null) ? this.name.trim() : null);

        // Returns name, optionally with club if available
        String result = "";
        if ((clubString != null) && (!clubString.isBlank())) {
            result = nameString + " @ " + clubString;
        } else if (nameString != null) {
            result = nameString;
        }
        return result.trim();
    }
}
