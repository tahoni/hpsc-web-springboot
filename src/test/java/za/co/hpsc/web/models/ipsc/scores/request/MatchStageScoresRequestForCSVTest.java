package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchStageScoresRequestForCSVTest {

    // JSON/CSV deserialization (via a concrete subclass, since the class is abstract)
    @Test
    void testJsonDeserialization_whenAllColumnsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "StageNumber": 2,
                  "Name": "Jane Doe",
                  "%": 95.5,
                  "Stg Pts": 85.0,
                  "Pts": 90,
                  "HF": 7.5,
                  "Time": 12.0,
                  "Div": "OPEN",
                  "Class": "HPSC",
                  "Cats": ["LADY"],
                  "PF": "MAJOR",
                  "Mem#": "HPSC-001",
                  "A": 8,
                  "C": 1,
                  "D": 0,
                  "M": 0,
                  "NPM": 0,
                  "NS": 0,
                  "Proc": 0,
                  "Apen": 0
                }
                """;

        // Act
        TestMatchStageScoresRequestForCSV request = mapper.readValue(json, TestMatchStageScoresRequestForCSV.class);

        // Assert
        assertNull(request.getMatchId());
        assertEquals(2, request.getStageNumber());
        assertEquals("Jane Doe", request.getName());
        assertEquals(new BigDecimal("95.5"), request.getStagePercentage());
        assertEquals(90, request.getPoints());
        assertEquals(Division.OPEN, request.getDivision());
        assertEquals(ClubIdentifier.HPSC, request.getClub());
        assertEquals(List.of(CompetitorCategory.LADY), request.getCategories());
        assertEquals(PowerFactor.MAJOR, request.getPowerFactor());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(8, request.getAlpha());
    }

    @Test
    void testJsonDeserialization_whenUpperCamelCaseColumnWithNoOverride_thenMapsOntoField() throws Exception {
        // Arrange - `time` has no field-level @JsonProperty override, so it's matched purely via
        // the class's @JsonNaming(UpperCamelCaseStrategy) transform
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "StageNumber": 2,
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001",
                  "Time": 12.0
                }
                """;

        // Act
        TestMatchStageScoresRequestForCSV request = mapper.readValue(json, TestMatchStageScoresRequestForCSV.class);

        // Assert
        assertEquals(new BigDecimal("12.0"), request.getTime());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredColumnsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "StageNumber": 2,
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001"
                }
                """;

        // Act
        TestMatchStageScoresRequestForCSV request = mapper.readValue(json, TestMatchStageScoresRequestForCSV.class);

        // Assert
        assertEquals(2, request.getStageNumber());
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertNull(request.getStagePercentage());
        assertNull(request.getDivision());
    }

    @Test
    void testJsonDeserialization_whenStageNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestMatchStageScoresRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "StageNumber": 2,
                  "Mem#": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestMatchStageScoresRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenMembershipNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "StageNumber": 2,
                  "Name": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestMatchStageScoresRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{}", TestMatchStageScoresRequestForCSV.class));
    }

    // Mixin usage onto MatchStageScoresRequest (the intended production usage, matching
    // AwardServiceImpl/ImageServiceImpl's csvMapper.addMixIn(...) pattern)
    @Test
    void testMixin_whenAppliedToMatchStageScoresRequest_thenMapsPractiscoreColumns() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MatchStageScoresRequest.class, MatchStageScoresRequestForCSV.class);
        String json = """
                {
                  "StageNumber": 2,
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001",
                  "HF": 7.5
                }
                """;

        // Act
        MatchStageScoresRequest request = mapper.readValue(json, MatchStageScoresRequest.class);

        // Assert
        assertEquals(2, request.getStageNumber());
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(new BigDecimal("7.5"), request.getHitFactor());
    }

    @Test
    void testMixin_whenStageNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MatchStageScoresRequest.class, MatchStageScoresRequestForCSV.class);
        String json = """
                {
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageScoresRequest.class));
    }

    private static class TestMatchStageScoresRequestForCSV extends MatchStageScoresRequestForCSV {
        @JsonCreator
        TestMatchStageScoresRequestForCSV(@JsonProperty("MatchId") Long matchId,
                                          @JsonProperty(value = "StageNumber", required = true) Integer stageNumber,
                                          @JsonProperty(value = "Name", required = true) String name,
                                          @JsonProperty("%") BigDecimal stagePercentage,
                                          @JsonProperty("Stg Pts") BigDecimal stagePoints,
                                          @JsonProperty("Pts") Integer points,
                                          @JsonProperty("HF") BigDecimal hitFactor,
                                          @JsonProperty("Time") BigDecimal time,
                                          @JsonProperty("Div") Division division,
                                          @JsonProperty("Class") ClubIdentifier club,
                                          @JsonProperty("Cats") List<CompetitorCategory> categories,
                                          @JsonProperty("PF") PowerFactor powerFactor,
                                          @JsonProperty(value = "Mem#", required = true) String membershipNumber,
                                          @JsonProperty("A") Integer alpha,
                                          @JsonProperty("C") Integer charlie,
                                          @JsonProperty("D") Integer delta,
                                          @JsonProperty("M") Integer misses,
                                          @JsonProperty("NPM") Integer noPenaltyMisses,
                                          @JsonProperty("NS") Integer noShoots,
                                          @JsonProperty("Proc") Integer proceduralErrors,
                                          @JsonProperty("Apen") Integer additionalPenalties) {
            super(matchId, stageNumber, name, stagePercentage, stagePoints, points, hitFactor, time, division, club,
                    categories, powerFactor, membershipNumber, alpha, charlie, delta, misses, noPenaltyMisses,
                    noShoots, proceduralErrors, additionalPenalties);
        }
    }
}
