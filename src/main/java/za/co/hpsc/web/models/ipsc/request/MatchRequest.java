package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchId", required = true)
    private Integer matchId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchName")
    private String matchName;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchDt")
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT)
    private LocalDateTime matchDate;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ClubId")
    Integer clubId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "SquadCount")
    private Integer squadCount;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "FirearmId")
    private Integer firearmId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchLevel")
    private Integer matchLevel;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Chrono")
    private Boolean chrono;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Location")
    private String location;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CountryId")
    private String countryId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty("MD")
    private String matchDirector;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty("RM")
    private String rangeMaster;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty("SD")
    private String statsDirector;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<MatchRequest> row;
    }
}
