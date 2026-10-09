package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;

import static org.junit.jupiter.api.Assertions.*;

public class MatchCompetitorHelpersTest {

    // getErrorMessagesForMissingRequiredFields(MatchCompetitor, MatchCompetitorRequest)
    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenNothingMissing_thenReturnsEmptyString() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        MatchCompetitorRequest request = new MatchCompetitorRequest();

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertEquals("", message);
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingAndNothingSpecified_thenReportsNotSpecified() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not specified"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingAndOnlyGenericNumber_thenReportsNotSpecified() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("15000");
        request.setCompetitorName("  ");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not specified"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithIdNameAndNumber_thenReportsNotFoundForEach() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(7L);
        request.setCompetitorName("John Smith");
        request.setCompetitorNumber("123");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithOnlyId_thenReportsNotFoundForId() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(7L);

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithOnlyName_thenReportsNotFoundForName() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("John Smith");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithUncleanName_thenReportsTheNormalisedName() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("1 - John   Smith (RO)");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertEquals("Competitor not found for name John Smith", message);
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithHyphenatedName_thenKeepsTheHyphen() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("2 - Jane Smith-Jones");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertEquals("Competitor not found for name Jane Smith-Jones", message);
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithOnlyNumber_thenReportsNotFoundForNumber() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("123");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithBlankNameAndNumber_thenReportsNumberOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("   ");
        request.setCompetitorNumber("123");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithGenericNumberAndId_thenReportsIdOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(7L);
        request.setCompetitorNumber("16000");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithGenericNumberAndName_thenReportsNameOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("John Smith");
        request.setCompetitorNumber("15000");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithIdAndName_thenReportsBothSeparatedByComma() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(7L);
        request.setCompetitorName("John Smith");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorPresentButRequestHasNoCompetitor_thenReportsNothing() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(7L);
        request.setCompetitorName("John Smith");
        request.setCompetitorNumber("123");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertEquals("", message);
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithBlankNumber_thenReportsNotSpecified() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest emptyNumberRequest = new MatchCompetitorRequest();
        emptyNumberRequest.setCompetitorNumber("");
        MatchCompetitorRequest blankNumberRequest = new MatchCompetitorRequest();
        blankNumberRequest.setCompetitorNumber("   ");

        // Act
        String emptyNumberMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, emptyNumberRequest);
        String blankNumberMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, blankNumberRequest);

        // Assert
        assertTrue(emptyNumberMessage.contains("not specified"));
        assertTrue(blankNumberMessage.contains("not specified"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithPaddedGenericNumber_thenReportsNotSpecified() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber(" 15000 ");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not specified"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorMissingWithBlankNumberAndName_thenReportsNameOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorName("John Smith");
        request.setCompetitorNumber("");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenMatchMissingWithoutId_thenReportsNotSpecified() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setMatch(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not specified"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenMatchMissingWithId_thenReportsNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setMatch(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchId(42L);

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenMatchClubMissingAndRequestClubNullOrBlank_thenReportsNothing() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setMatchClub(null);
        MatchCompetitorRequest nullClubRequest = new MatchCompetitorRequest();
        MatchCompetitorRequest blankClubRequest = new MatchCompetitorRequest();
        blankClubRequest.setMatchClub("  ");

        // Act
        String nullClubMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, nullClubRequest);
        String blankClubMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, blankClubRequest);

        // Assert
        assertEquals("", nullClubMessage);
        assertEquals("", blankClubMessage);
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenMatchClubMissingWithClub_thenReportsNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setMatchClub(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("Unknown Club");

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenCompetitorCategoryMissing_thenReportsNotSpecifiedOrNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setCompetitorCategory(null);
        MatchCompetitorRequest unspecifiedRequest = new MatchCompetitorRequest();
        MatchCompetitorRequest specifiedRequest = new MatchCompetitorRequest();
        specifiedRequest.setCompetitorCategory("Unknown");

        // Act
        String unspecifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, unspecifiedRequest);
        String specifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, specifiedRequest);

        // Assert
        assertTrue(unspecifiedMessage.contains("not specified"));
        assertTrue(specifiedMessage.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenDivisionMissing_thenReportsNotSpecifiedOrNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setDivision(null);
        MatchCompetitorRequest unspecifiedRequest = new MatchCompetitorRequest();
        MatchCompetitorRequest specifiedRequest = new MatchCompetitorRequest();
        specifiedRequest.setDivision("Unknown");

        // Act
        String unspecifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, unspecifiedRequest);
        String specifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, specifiedRequest);

        // Assert
        assertTrue(unspecifiedMessage.contains("not specified"));
        assertTrue(specifiedMessage.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenFirearmTypeMissing_thenReportsNotSpecifiedOrNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setFirearmType(null);
        MatchCompetitorRequest unspecifiedRequest = new MatchCompetitorRequest();
        MatchCompetitorRequest specifiedRequest = new MatchCompetitorRequest();
        specifiedRequest.setFirearmType("Unknown");

        // Act
        String unspecifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, unspecifiedRequest);
        String specifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, specifiedRequest);

        // Assert
        assertTrue(unspecifiedMessage.contains("not specified"));
        assertTrue(specifiedMessage.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenPowerFactorMissing_thenReportsNotSpecifiedOrNotFound() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setPowerFactor(null);
        MatchCompetitorRequest unspecifiedRequest = new MatchCompetitorRequest();
        MatchCompetitorRequest specifiedRequest = new MatchCompetitorRequest();
        specifiedRequest.setPowerFactor("Unknown");

        // Act
        String unspecifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, unspecifiedRequest);
        String specifiedMessage = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                matchCompetitor, specifiedRequest);

        // Assert
        assertTrue(unspecifiedMessage.contains("not specified"));
        assertTrue(specifiedMessage.contains("not found for"));
    }

    @Test
    void testGetErrorMessagesForMissingRequiredFields_whenSeveralFieldsMissing_thenJoinsMessagesInFieldOrder() {
        // Arrange
        MatchCompetitor matchCompetitor = completeMatchCompetitor();
        matchCompetitor.setMatch(null);
        matchCompetitor.setDivision(null);
        matchCompetitor.setPowerFactor(null);
        MatchCompetitorRequest request = new MatchCompetitorRequest();

        // Act
        String message = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(matchCompetitor, request);

        // Assert
        assertTrue(message.contains("not specified"));
    }

    // Helpers
    private MatchCompetitor completeMatchCompetitor() {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(new Competitor());
        matchCompetitor.setMatch(new IpscMatch());
        matchCompetitor.setMatchClub(ClubIdentifier.HPSC);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.NONE);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setPowerFactor(PowerFactor.MINOR);
        return matchCompetitor;
    }
}
