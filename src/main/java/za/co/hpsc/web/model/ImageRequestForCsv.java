package za.co.hpsc.web.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
