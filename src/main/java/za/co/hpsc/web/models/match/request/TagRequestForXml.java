package za.co.hpsc.web.models.match.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class TagRequestForXml {
    @JsonProperty(required = true)
    private Integer tagId;
    @JsonProperty(value = "Tag")
    private String tagName;
}
