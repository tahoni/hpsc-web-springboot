package za.co.hpsc.web.models.match.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class TeamRequestForXml {
    private Integer matchId;
    private Integer teamId;
    @JsonProperty(value = "Team")
    private String teamName;

    @JsonProperty(value = "DivId")
    private Integer divisionId;
    @JsonProperty(value = "CatId")
    private Integer nonTeamCategoryId;
}
