package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an IPSC request within the system.
 *
 * <p>
 * It contains the contents of each XML file in the `WinMSS.cab` file as a request object,
 * parsed from the raw string content.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscRequestHolder {
    private List<ClubRequest> clubs = new ArrayList<>();
    private List<MatchRequest> matches = new ArrayList<>();
    private List<StageRequest> stages = new ArrayList<>();
    private List<TagRequest> tags = new ArrayList<>();
    private List<MemberRequest> members = new ArrayList<>();
    private List<ClassificationRequest> classifications = new ArrayList<>();
    private List<EnrolledRequest> enrolledMembers = new ArrayList<>();
    private List<SquadRequest> squads = new ArrayList<>();
    private List<TeamRequest> teams = new ArrayList<>();
    private List<ScoreRequest> scores = new ArrayList<>();
}
