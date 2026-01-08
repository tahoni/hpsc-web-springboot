package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardRequest extends Request {
    private String date;
    private String imageFilePath;

    @NotNull
    private String firstPlace;
    @NotNull
    private String secondPlace;
    @NotNull
    private String thirdPlace;

    private String firstPlaceImageFileName;
    private String secondPlaceImageFilePath;
    private String thirdPlaceImageFilePath;
}
