package za.co.hpsc.web.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"title", "summary", "description", "category", "tags", "filePath", "fileName"})
public abstract class ImageRequestForCsv {
    @NotNull
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    @NotNull
    private String filePath;
    @NotNull
    private String fileName;
}
