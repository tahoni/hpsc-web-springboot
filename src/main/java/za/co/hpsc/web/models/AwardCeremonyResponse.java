package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AwardCeremonyResponse extends Response {
    private LocalDateTime date;
    @NotNull
    private String imageFilePath = "";

    @NotNull
    private List<AwardResponse> awards = new ArrayList<>();

    public AwardCeremonyResponse() {
        super();
        this.date = null;
        this.imageFilePath = "";
    }

    public AwardCeremonyResponse(LocalDateTime date, String imageFilePath,
                                 List<AwardResponse> awards) {
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    public AwardCeremonyResponse(UUID uuid, LocalDateTime date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(uuid);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    public AwardCeremonyResponse(UUID uuid, String title, String summary, String description,
                                 String category, List<String> tags, LocalDateTime date,
                                 String imageFilePath, List<AwardResponse> awards) {
        super(uuid, title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    public AwardCeremonyResponse(UUID uuid, String title, LocalDateTime date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(uuid, title);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    public AwardCeremonyResponse(String title, String summary, String description, String category,
                                 List<String> tags, LocalDateTime date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    public AwardCeremonyResponse(List<AwardRequest> awardRequestList) {
        this();
        if ((awardRequestList == null) || (awardRequestList.isEmpty())) {
            return;
        }

        this.date = awardRequestList.getFirst().getDate();
        this.imageFilePath = awardRequestList.getFirst().getImageFilePath();

        this.awards = awardRequestList.stream()
                .map(AwardResponse::new)
                .toList();
    }
}
