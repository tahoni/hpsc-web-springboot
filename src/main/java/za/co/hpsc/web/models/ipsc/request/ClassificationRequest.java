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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Integer memberId;
    @JacksonXmlProperty(isAttribute = true)
    @NotNull
    @JsonProperty(required = true)
    private Integer divisionId;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "IntlId", required = true)
    private String internationalClassificationId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "NatlId", required = true)
    private String nationalClassificationId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ClassId")
    private String scoreClassificationId;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ClassificationRequest> row;
    }
}
