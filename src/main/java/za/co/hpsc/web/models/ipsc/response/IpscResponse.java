package za.co.hpsc.web.models.ipsc.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a comprehensive response model for IPSC (International Practical Shooting
 * Confederation)-related data.
 *
 * <p>
 * This class aggregates various components such as club details, match information, stages, tags,
 * members, enrolled members, and scores. It acts as a single response object to encapsulate all
 * data related to matches and participants in an IPSC event.
 * The class provides constructors to transform and initialise the encapsulated data from
 * request objects. Additionally, it includes functionality to update the list of members
 * dynamically.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResponse {
    private MatchResponse match = new MatchResponse();
    private List<StageResponse> stages = new ArrayList<>();
    private List<TagResponse> tags = new ArrayList<>();
    private List<MemberResponse> members = new ArrayList<>();
    private List<EnrolledResponse> enrolledMembers = new ArrayList<>();
    private List<ScoreResponse> scores = new ArrayList<>();

    /**
     * Constructs a new {@code IpscResponse} instance by transforming and initialising the various
     * data components from the provided request objects.
     *
     * @param tagRequests      a list of {@link TagRequest} objects, representing tag data
     *                         to initialise the {@code tags} field.
     * @param matchResponse    a {@link MatchResponse} object containing match details
     *                         to initialise the {@code match} field.
     * @param stageRequests    a list of {@link StageRequest} objects, representing stage data
     *                         to initialise the {@code stages} field.
     * @param enrolledRequests a list of {@link EnrolledRequest} objects, representing enrolled
     *                         member data to initialise the {@code enrolledMembers} field.
     * @param scoreRequests    a list of {@link ScoreRequest} objects, representing scoring data
     *                         to initialise the {@code scores} field.
     */
    public IpscResponse(List<TagRequest> tagRequests, MatchResponse matchResponse,
                        List<StageRequest> stageRequests, List<EnrolledRequest> enrolledRequests,
                        List<ScoreRequest> scoreRequests) {

        this.match = matchResponse;
        this.tags = tagRequests.stream().map(TagResponse::new).toList();
        this.stages = stageRequests.stream().map(StageResponse::new).toList();
        this.enrolledMembers = enrolledRequests.stream().map(EnrolledResponse::new).toList();
        this.scores = scoreRequests.stream().map(ScoreResponse::new).toList();
    }

    /**
     * Updates the list of members by transforming each {@link MemberRequest} object in the
     * provided list into a {@link MemberResponse} object and storing the results.
     *
     * @param memberRequests a list of {@link MemberRequest} objects representing the members
     *                       to be updated and initialised as {@link MemberResponse} objects.
     */
    public void setMembers(List<MemberRequest> memberRequests) {
        if (memberRequests == null) {
            return;
        }
        this.members = memberRequests.stream().map(MemberResponse::new).toList();
    }
}
