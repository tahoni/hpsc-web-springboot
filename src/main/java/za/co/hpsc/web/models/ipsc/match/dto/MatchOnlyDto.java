package za.co.hpsc.web.models.ipsc.match.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.utils.IpscUtil;

import java.time.LocalDateTime;

// TODO: add Javadoc
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

    // TODO: add tests
    public MatchOnlyDto(IpscMatch matchEntity) {
        if (matchEntity != null) {
            // Initialises match details
            this.id = matchEntity.getId();

            // Initialises club details from the associated entity
            if (matchEntity.getClub() != null) {
                this.club = new ClubDto(matchEntity.getClub());
            }

            // Initialises the match attributes
            this.name = matchEntity.getName();
            this.scheduledDate = matchEntity.getScheduledDate();
            this.matchFirearmType = matchEntity.getMatchFirearmType();
            this.matchCategory = matchEntity.getMatchCategory();

            // Initialises the date fields
            this.dateEdited = matchEntity.getDateEdited();
        }
    }

    @Override
    public String toString() {
        return IpscUtil.matchToString(this.name, this.clubName, "");
    }
}
