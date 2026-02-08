package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an IPSC request within the system.
 *
 * <p>
 * It contains the contents of each XML file in the `WinMSS.cab` file as a raw string.
 * </p>
 */
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
    private String classification;
    private String enrolled;
    private String squad;
    private String team;
    private String score;
}
