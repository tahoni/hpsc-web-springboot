package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscRequestHolder {
    private MatchRequest match;
    private StageRequest stage;
    private TagRequest tag;
    private MemberRequest member;
    private EnrolledRequest enrolled;
    private SquadRequest squad;
    private ScoreRequest score;
}
