package za.co.hpsc.web.models.ipsc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResponse {
    private ClubResponse club;
    private MatchResponse match;
    private List<StageResponse> stage;
    private List<TagResponse> tag;
    private List<MemberResponse> member;
    private List<EnrolledResponse> enrolled;
    private List<SquadResponse> squad;
    private List<ScoreResponse> score;
}
