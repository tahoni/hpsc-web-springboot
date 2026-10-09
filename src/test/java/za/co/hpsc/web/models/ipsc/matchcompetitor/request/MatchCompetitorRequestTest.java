package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.ValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchCompetitorRequest request = new MatchCompetitorRequest(7L, 1L, "Jane Doe", "123", 2L, "HPSC", "Junior",
                "Handgun", "Open", "Major", new BigDecimal("95.5"), new BigDecimal("98.25"),
                new BigDecimal("41.5"), new BigDecimal("93.75"), 30, 4, 1, 2, 1, 0, 3, 0,
                new BigDecimal("2"), new BigDecimal("1"), false);

        // Act
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        // Assert
        assertEquals(7, node.get("matchCompetitorId").asInt());
        assertEquals(1, node.get("competitorId").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals("123", node.get("competitorNumber").asText());
        assertEquals(2, node.get("matchId").asInt());
        assertEquals("HPSC", node.get("matchClub").asText());
        assertEquals("Junior", node.get("competitorCategory").asText());
        assertEquals("Handgun", node.get("firearmType").asText());
        assertEquals("Open", node.get("division").asText());
        assertEquals("Major", node.get("powerFactor").asText());
        assertEquals(0, new BigDecimal("95.5").compareTo(node.get("points").decimalValue()));
        assertEquals(0, new BigDecimal("98.25").compareTo(node.get("percentage").decimalValue()));
        assertEquals(0, new BigDecimal("41.5").compareTo(node.get("time").decimalValue()));
        assertEquals(0, new BigDecimal("93.75").compareTo(node.get("percentageOfPossiblePoints").decimalValue()));
        assertEquals(30, node.get("alpha").asInt());
        assertEquals(4, node.get("charlie").asInt());
        assertEquals(1, node.get("delta").asInt());
        assertEquals(2, node.get("misses").asInt());
        assertEquals(1, node.get("noPenaltyMisses").asInt());
        assertEquals(0, node.get("noShoots").asInt());
        assertEquals(3, node.get("proceduralErrors").asInt());
        assertEquals(0, node.get("additionalPenalties").asInt());
        assertEquals(2, node.get("overallRanking").asInt());
        assertEquals(1, node.get("clubRanking").asInt());
        assertFalse(node.get("isVisitor").asBoolean());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchCompetitorRequest request = new MatchCompetitorRequest(null, 1L, null, null, 2L, null, "Junior", null,
                "Open", null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null);

        // Act
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        // Assert
        assertEquals(1, node.get("competitorId").asInt());
        assertEquals(2, node.get("matchId").asInt());
        assertEquals("Junior", node.get("competitorCategory").asText());
        assertEquals("Open", node.get("division").asText());
        assertTrue(node.get("matchCompetitorId").isNull());
        assertTrue(node.get("matchClub").isNull());
        assertTrue(node.get("firearmType").isNull());
        assertTrue(node.get("powerFactor").isNull());
        assertTrue(node.get("points").isNull());
        assertTrue(node.get("overallRanking").isNull());
        assertTrue(node.get("clubRanking").isNull());
        assertTrue(node.get("isVisitor").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        String json = """
                {
                  "matchCompetitorId": 7,
                  "competitorId": 1,
                  "matchId": 2,
                  "matchClub": "HPSC",
                  "competitorCategory": "Junior",
                  "firearmType": "Handgun",
                  "division": "Open",
                  "powerFactor": "Major",
                  "points": 95.5,
                  "overallRanking": 2,
                  "clubRanking": 1,
                  "isVisitor": false
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertEquals(7L, request.getMatchCompetitorId());
        assertEquals(1L, request.getCompetitorId());
        assertEquals(2L, request.getMatchId());
        assertEquals("HPSC", request.getMatchClub());
        assertEquals("Junior", request.getCompetitorCategory());
        assertEquals("Handgun", request.getFirearmType());
        assertEquals("Open", request.getDivision());
        assertEquals("Major", request.getPowerFactor());
        assertEquals(0, new BigDecimal("95.5").compareTo(request.getPoints()));
        assertEquals(0, new BigDecimal("2").compareTo(request.getOverallRanking()));
        assertEquals(0, new BigDecimal("1").compareTo(request.getClubRanking()));
        assertEquals(Boolean.FALSE, request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenOptionalsAreNull() throws Exception {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": "Junior",
                  "firearmType": "Handgun",
                  "division": "Open"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getMatchCompetitorId());
        assertNull(request.getMatchClub());
        assertNull(request.getPowerFactor());
        assertNull(request.getPoints());
        assertNull(request.getOverallRanking());
        assertNull(request.getClubRanking());
        assertNull(request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenFirearmTypeMissing_thenFirearmTypeIsNull() throws Exception {
        // Arrange - firearmType isn't a Jackson-required property
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": "Junior",
                  "division": "Open"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getFirearmType());
    }

    @Test
    void testJsonDeserialization_whenCompetitorIdMissingAndNameGiven_thenDeserializesWithNameOnly() throws Exception {
        // Arrange
        String json = """
                {
                  "name": "Jane Doe",
                  "matchId": 2,
                  "competitorCategory": "Junior",
                  "division": "Open"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getCompetitorId());
        assertEquals("Jane Doe", request.getCompetitorName());
    }

    @Test
    void testJsonDeserialization_whenMatchIdMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "competitorCategory": "Junior",
                  "division": "Open"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenCompetitorCategoryMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "division": "Open"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenDivisionMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": "Junior"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue("{}", MatchCompetitorRequest.class));
    }

    // validate()
    @Test
    void testValidate_whenAllRequiredFieldsPresent_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> validRequest().validate());
    }

    @Test
    void testValidate_whenOnlyCompetitorIdGiven_thenDoesNotThrow() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorNumber(null);
        request.setCompetitorName(null);

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenOnlyCompetitorNumberGiven_thenDoesNotThrow() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorId(null);
        request.setCompetitorName(null);

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenOnlyCompetitorNameGiven_thenDoesNotThrow() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorId(null);
        request.setCompetitorNumber(null);

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenNoCompetitorIdNumberOrName_thenThrowsValidationException() {
        for (String blank : new String[]{null, "", "   "}) {
            // Arrange
            MatchCompetitorRequest request = validRequest();
            request.setCompetitorId(null);
            request.setCompetitorNumber(blank);
            request.setCompetitorName(blank);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Competitor ID, number or name is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenMatchIdIsNull_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setMatchId(null);

        // Act
        ValidationException exception = assertThrows(ValidationException.class, request::validate);

        // Assert
        assertEquals("Match ID is required.", exception.getMessage());
    }

    @Test
    void testValidate_whenCompetitorCategoryIsNullEmptyOrBlank_thenThrowsValidationException() {
        for (String value : new String[]{null, "", "   "}) {
            // Arrange
            MatchCompetitorRequest request = validRequest();
            request.setCompetitorCategory(value);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Competitor category is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenFirearmTypeIsNullEmptyOrBlank_thenDoesNotThrow() {
        for (String value : new String[]{null, "", "   "}) {
            // Arrange
            MatchCompetitorRequest request = validRequest();
            request.setFirearmType(value);

            // Act & Assert
            assertDoesNotThrow(request::validate);
        }
    }

    @Test
    void testValidate_whenDivisionIsNullEmptyOrBlank_thenThrowsValidationException() {
        for (String value : new String[]{null, "", "   "}) {
            // Arrange
            MatchCompetitorRequest request = validRequest();
            request.setDivision(value);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Division is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenPowerFactorIsNullEmptyOrBlank_thenThrowsValidationException() {
        for (String value : new String[]{null, "", "   "}) {
            // Arrange
            MatchCompetitorRequest request = validRequest();
            request.setPowerFactor(value);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Power factor is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenDivisionDoesNotMatchFirearmType_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setFirearmType("Shotgun");

        // Act
        ValidationException exception = assertThrows(ValidationException.class, request::validate);

        // Assert
        assertEquals("Division Open is not a Shotgun division.", exception.getMessage());
    }

    @Test
    void testValidate_whenDivisionMatchesNonHandgunFirearmType_thenDoesNotThrow() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setFirearmType("Shotgun");
        request.setDivision("Shotgun Open");

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenUnknownEnumValuesGiven_thenStillReturnsTrue() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorCategory("Not A Category");
        request.setFirearmType("Not A Firearm");
        request.setDivision("Not A Division");
        request.setPowerFactor("Not A Power Factor");

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenOnlyOneOfFirearmTypeAndDivisionIsUnknown_thenStillReturnsTrue() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setDivision("Not A Division");

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    // Helpers
    private MatchCompetitorRequest validRequest() {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setCompetitorNumber("123");
        request.setCompetitorName("Jane Doe");
        request.setMatchId(2L);
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open");
        request.setPowerFactor("Major");
        return request;
    }
}
