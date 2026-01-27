package za.co.hpsc.web.models.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchRequestForXml {
    @JsonProperty(required = true)
    private Integer matchId;
    private String matchName;
    @JsonProperty(value = "MatchDt")
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT)
    private LocalDateTime matchDate;

    private Integer squadCount;
    private Integer firearmTypeId;
    @JsonProperty(value = "Level")

    private Integer matchLevel;
    @JsonProperty(required = true)
    private Boolean chrono;

    private String location;
    private String countryId;

    private String matchDirector;
    @JsonProperty("RM")
    private String rangeMaster;
    @JsonProperty("SD")
    private String statsDirector;
}
