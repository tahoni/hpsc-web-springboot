package za.co.hpsc.web.models.ipsc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResponse {
    private ClubResponse club;
    private MatchResponse match;
    private List<StageResponse> stages;
    private List<TagResponse> tags;
    private List<MemberResponse> members;
    private List<EnrolledResponse> enrolledMembers;
    private List<ScoreResponse> scores;

    public IpscResponse(List<TagRequest> tagRequests, MatchResponse matchResponse, List<StageRequest> stageRequests, List<EnrolledRequest> enrolledRequests, List<ScoreRequest> scoreRequests) {
        this.match = matchResponse;
        this.tags = tagRequests.stream().map(TagResponse::new).toList();
        this.stages = stageRequests.stream().map(StageResponse::new).toList();
        this.enrolledMembers = enrolledRequests.stream().map(EnrolledResponse::new).toList();
        this.scores = scoreRequests.stream().map(ScoreResponse::new).toList();
    }

    public void setMembers(List<MemberRequest> memberRequests) {
        this.members = memberRequests.stream().map(MemberResponse::new).toList();
    }
}
