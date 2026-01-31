package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscRequestHolder {
    private List<ClubRequest> clubs;
    private List<MatchRequest> matches;
    private List<StageRequest> stages;
    private List<TagRequest> tags;
    private List<MemberRequest> members;
    private List<EnrolledRequest> enrolledMembers;
    private List<SquadRequest> squads;
    private List<ScoreRequest> scores;
}
