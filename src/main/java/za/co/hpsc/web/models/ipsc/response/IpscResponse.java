package za.co.hpsc.web.models.ipsc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResponse {
    private ClubResponse club = new ClubResponse();
    private MatchResponse match = new MatchResponse();
    private List<StageResponse> stages = new ArrayList<>();
    private List<TagResponse> tags = new ArrayList<>();
    private List<MemberResponse> members = new ArrayList<>();
    private List<EnrolledResponse> enrolledMembers = new ArrayList<>();
    private List<ScoreResponse> scores = new ArrayList<>();

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
