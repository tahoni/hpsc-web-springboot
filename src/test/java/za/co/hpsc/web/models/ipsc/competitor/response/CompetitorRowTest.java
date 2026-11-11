package za.co.hpsc.web.models.ipsc.competitor.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorRowTest {

    // CompetitorRow(CompetitorRequest)
    @Test
    void testConstructor_whenRequestIsNull_thenEveryValueIsEmpty() {
        // Act
        CompetitorRow row = new CompetitorRow(null);

        // Assert
        assertEquals("", row.getFirstName());
        assertEquals("", row.getLastName());
        assertEquals("", row.getMiddleNames());
        assertEquals("", row.getNickName());
        assertEquals("", row.getDateOfBirth());
        assertEquals("", row.getGender());
        assertEquals("", row.getHomeClub());
        assertEquals("", row.getSapsaNumber());
        assertEquals("", row.getCompetitorNumber());
        assertEquals("", row.getClubNumber());
        assertEquals("", row.getIdNumber());
        assertEquals("", row.getCellphoneNumber());
        assertEquals("", row.getEmailAddresses());
        assertEquals("", row.getPaidUpSapsa());
        assertEquals("", row.getPaidUpNgpsa());
        assertEquals("", row.getPaidUpClub());
        assertEquals("", row.getIsVerified());
    }

    @Test
    void testConstructor_whenRequestSuppliesEverything_thenDescribesItAsText() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest(null, "Jane", "Doe", "Ann", "Janie",
                LocalDate.of(1990, 1, 31), "Female", "Test Club", 12345, "7001", "HPSC-001", "9001015800083",
                "0821234567", List.of("jane@example.com", "jane2@example.com"), true, false, true, true);

        // Act
        CompetitorRow row = new CompetitorRow(request);

        // Assert
        assertEquals("Jane", row.getFirstName());
        assertEquals("Doe", row.getLastName());
        assertEquals("Ann", row.getMiddleNames());
        assertEquals("Janie", row.getNickName());
        assertEquals("1990-01-31", row.getDateOfBirth());
        assertEquals("Female", row.getGender());
        assertEquals("Test Club", row.getHomeClub());
        assertEquals("12345", row.getSapsaNumber());
        assertEquals("7001", row.getCompetitorNumber());
        assertEquals("HPSC-001", row.getClubNumber());
        assertEquals("9001015800083", row.getIdNumber());
        assertEquals("0821234567", row.getCellphoneNumber());
        assertEquals("jane@example.com;jane2@example.com", row.getEmailAddresses());
        assertEquals("true", row.getPaidUpSapsa());
        assertEquals("false", row.getPaidUpNgpsa());
        assertEquals("true", row.getPaidUpClub());
        assertEquals("true", row.getIsVerified());
    }

    @Test
    void testConstructor_whenValuesAreMissing_thenReportsThemAsEmpty() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest(null, null, "Doe", null, null, null, null, null, null,
                null, "HPSC-001", null, null, Arrays.asList("jane@example.com", null, "jane2@example.com"), null,
                null, null, null);

        // Act
        CompetitorRow row = new CompetitorRow(request);

        // Assert
        assertEquals("", row.getFirstName());
        assertEquals("Doe", row.getLastName());
        assertEquals("", row.getDateOfBirth());
        assertEquals("", row.getSapsaNumber());
        assertEquals("HPSC-001", row.getClubNumber());
        assertEquals("jane@example.com;null;jane2@example.com", row.getEmailAddresses());
        assertEquals("", row.getPaidUpSapsa());
        assertEquals("", row.getIsVerified());
    }

    @Test
    void testConstructor_whenEmailAddressesAreNull_thenEmailAddressesIsEmpty() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setEmailAddresses(null);

        // Act
        CompetitorRow row = new CompetitorRow(request);

        // Assert
        assertEquals("", row.getEmailAddresses());
    }

    @Test
    void testConstructor_whenThereAreNoEmailAddresses_thenEmailAddressesIsEmpty() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();

        // Act
        CompetitorRow row = new CompetitorRow(request);

        // Assert
        assertEquals("", row.getEmailAddresses());
    }
}
