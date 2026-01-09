package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardRequest extends Request {
    private LocalDateTime date;
    @NotNull
    private String imageFilePath;

    @NotNull
    private String ceremonyTitle;
    private String ceremonySummary;
    private String ceremonyDescription;
    private String ceremonyCategory;
    private List<String> ceremonyTags = new ArrayList<>();

    @NotNull
    private String firstPlaceName;
    @NotNull
    private String secondPlaceName;
    @NotNull
    private String thirdPlaceName;

    private String firstPlaceImageFileName;
    private String secondPlaceImageFilePath;
    private String thirdPlaceImageFilePath;
}
