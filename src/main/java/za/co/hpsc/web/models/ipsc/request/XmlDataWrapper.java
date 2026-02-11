package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Wrapper class for encapsulating XML-based data with generic support.
 *
 * <p>
 * Generic wrapper for parsing XML structure:
 * {@code <xml><data><row>...</row></data></xml>}
 * </p>
 *
 * @param <T> The type of objects contained in the row elements
 *            of the XML data structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "xml")
public class XmlDataWrapper<T> {
    @JacksonXmlProperty(localName = "data")
    private DataWrapper<T> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper<T> {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<T> row;
    }
}
