package za.co.hpsc.web.models.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class ClassifyRequestForXml {
    @JsonProperty(required = true)
    private Integer memberId;
    @JsonProperty(required = true)
    private Integer divisionId;

    @JsonProperty(value = "IntlId", required = true)
    private String internationalClassificationId;
    @JsonProperty(value = "NatlId", required = true)
    private String nationalClassificationId;
    @JsonProperty(value = "ClassId")
    private String scoreClassificationId;
}
