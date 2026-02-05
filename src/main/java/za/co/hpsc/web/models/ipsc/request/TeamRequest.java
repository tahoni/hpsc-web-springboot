package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a team request within the system.
 *
 * <p>
 * The class maps to the `TEAM.XML` content in the `WinMSS.cab` file.
 * It stores data of all teams in a match.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    @JacksonXmlText
    private String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchId", required = true)
    private Integer matchId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TeamId", required = true)
    private Integer teamId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Team")
    private String teamName;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DivId")
    private Integer divisionId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CatId")
    private Integer nonTeamCategoryId;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<TeamRequest> row;
    }
}
