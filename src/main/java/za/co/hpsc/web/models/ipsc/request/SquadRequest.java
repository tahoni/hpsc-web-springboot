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
 * Represents a squad request within the system.
 *
 * <p>
 * The class maps to the `SQUAD.XML` content in the `WinMSS.cab` file.
 * It stores minimal data about all squads in the system.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SquadRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchId", required = true)
    private Integer matchId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "SquadId", required = true)
    private Integer squadId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Squad")
    private String squadName;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<SquadRequest> row;
    }
}
