package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
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

class MatchOverallScoresRequestForCSVTest {

    // JSON/CSV deserialization (via a concrete subclass, since the class is abstract)
    @Test
    void testJsonDeserialization_whenAllColumnsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Name": "Jane Doe",
                  "%": 95.5,
                  "Pts": 450,
                  "Time": 60.2,
                  "%psbl": 88.0,
                  "HF": 7.5,
                  "Div": "OPEN",
                  "Class": "HPSC",
                  "Cats": ["LADY"],
                  "PF": "MAJOR",
                  "Mem#": "HPSC-001",
                  "A": 40,
                  "C": 8,
                  "D": 2,
                  "M": 1,
                  "NPM": 0,
                  "NS": 1,
                  "Proc": 1,
                  "Apen": 2
                }
                """;

        // Act
        TestMatchOverallScoresRequestForCSV request = mapper.readValue(json, TestMatchOverallScoresRequestForCSV.class);

        // Assert
        assertNull(request.getMatchId());
        assertEquals("Jane Doe", request.getName());
        assertEquals(new BigDecimal("95.5"), request.getPercentage());
        assertEquals(new BigDecimal("450"), request.getPoints());
        assertEquals(Division.OPEN, request.getDivision());
        assertEquals(ClubIdentifier.HPSC, request.getClub());
        assertEquals(List.of(CompetitorCategory.LADY), request.getCategories());
        assertEquals(PowerFactor.MAJOR, request.getPowerFactor());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(40, request.getAlpha());
    }

    @Test
    void testJsonDeserialization_whenUpperCamelCaseColumnWithNoOverride_thenMapsOntoField() throws Exception {
        // Arrange - `time` has no field-level @JsonProperty override, so it's matched purely via
        // the class's @JsonNaming(UpperCamelCaseStrategy) transform
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001",
                  "Time": 60.2
                }
                """;

        // Act
        TestMatchOverallScoresRequestForCSV request = mapper.readValue(json, TestMatchOverallScoresRequestForCSV.class);

        // Assert
        assertEquals(new BigDecimal("60.2"), request.getTime());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredColumnsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001"
                }
                """;

        // Act
        TestMatchOverallScoresRequestForCSV request = mapper.readValue(json, TestMatchOverallScoresRequestForCSV.class);

        // Assert
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertNull(request.getPercentage());
        assertNull(request.getDivision());
    }

    @Test
    void testJsonDeserialization_whenNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Mem#": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestMatchOverallScoresRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenMembershipNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "Name": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestMatchOverallScoresRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{}", TestMatchOverallScoresRequestForCSV.class));
    }

    // Mixin usage onto MatchOverallScoresRequest (the intended production usage, matching
    // AwardServiceImpl/ImageServiceImpl's csvMapper.addMixIn(...) pattern)
    @Test
    void testMixin_whenAppliedToMatchOverallScoresRequest_thenMapsPractiscoreColumns() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MatchOverallScoresRequest.class, MatchOverallScoresRequestForCSV.class);
        String json = """
                {
                  "Name": "Jane Doe",
                  "Mem#": "HPSC-001",
                  "HF": 7.5
                }
                """;

        // Act
        MatchOverallScoresRequest request = mapper.readValue(json, MatchOverallScoresRequest.class);

        // Assert
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(new BigDecimal("7.5"), request.getHitFactor());
    }

    @Test
    void testMixin_whenNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MatchOverallScoresRequest.class, MatchOverallScoresRequestForCSV.class);
        String json = """
                {
                  "Mem#": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchOverallScoresRequest.class));
    }

    private static class TestMatchOverallScoresRequestForCSV extends MatchOverallScoresRequestForCSV {
        @JsonCreator
        TestMatchOverallScoresRequestForCSV(@JsonProperty("MatchId") Long matchId,
                                            @JsonProperty(value = "Name", required = true) String name,
                                            @JsonProperty("%") BigDecimal percentage,
                                            @JsonProperty("Pts") BigDecimal points,
                                            @JsonProperty("Time") BigDecimal time,
                                            @JsonProperty("%psbl") BigDecimal percentageOfPossiblePoints,
                                            @JsonProperty("HF") BigDecimal hitFactor,
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
            super(matchId, name, percentage, points, time, percentageOfPossiblePoints, hitFactor, division, club,
                    categories, powerFactor, membershipNumber, alpha, charlie, delta, misses, noPenaltyMisses,
                    noShoots, proceduralErrors, additionalPenalties);
        }
    }
}
