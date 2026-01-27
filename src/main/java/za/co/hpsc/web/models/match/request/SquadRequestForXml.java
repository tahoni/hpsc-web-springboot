package za.co.hpsc.web.models.match.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class SquadRequestForXml {
    private Integer matchId;
    private Integer squadId;
    @JsonProperty(value = "Squad")
    private String squadName;
}
