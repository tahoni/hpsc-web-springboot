package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscRequest {
    private String club;
    private String match;
    private String stage;
    private String tag;
    private String member;
    private String enrolled;
    private String squad;
    private String score;
}
