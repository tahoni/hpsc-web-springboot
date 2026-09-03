package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscCompetitorService} - exercised through the
 * interface type, with a real Spring-wired {@code IpscCompetitorServiceImpl} bean backed by the
 * H2 {@code test} profile database.
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscCompetitorServiceIntegrationTest {

    @Autowired
    private IpscCompetitorService ipscCompetitorService;

    @Autowired
    private ClubRepository clubRepository;

    // createCompetitor()
    @Test
    void testCreateCompetitor_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(null));
    }

    @Test
    void testCreateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenLastNameIsMissing_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setLastName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest request = validRequest("  ");
        request.setHomeClub("Test Club");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsNotHpscAndClubNumberIsProvided_thenClubNumberIsNull() {
        // Arrange
        createClub("Other Club", ClubIdentifier.SOSC);
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("Other Club");

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertNull(response.getClubNumber());
    }

    @Test
    void testCreateCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsOmitted_thenPersistsCompetitorWithNoHomeClub() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertNotNull(response.getCompetitorId());
        assertNull(response.getHomeClub());
        assertNull(response.getClubNumber());
    }

    @Test
    void testCreateCompetitor_whenRequestIsValid_thenPersistsCompetitor() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("Test Club");
        request.setMiddleNames("Ann");
        request.setNickname("Janie");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.Female.toString());
        request.setSapsaNumber(12345);
        request.setCompetitorNumber("C-1");
        request.setIdNumber("9001015800083");
        request.setCellphoneNumber("0821234567");
        request.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertNotNull(response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Ann", response.getMiddleNames());
        assertEquals("Janie", response.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), response.getDateOfBirth());
        assertEquals(Gender.Female, response.getGender());
        assertEquals(ClubIdentifier.HPSC, response.getHomeClub());
        assertEquals(12345, response.getSapsaNumber());
        assertEquals("C-1", response.getCompetitorNumber());
        assertEquals("HPSC-001", response.getClubNumber());
        assertEquals("9001015800083", response.getIdNumber());
        assertEquals("0821234567", response.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com"), response.getEmailAddresses());
    }

    @Test
    void testCreateCompetitor_whenMultipleEmailAddresses_thenAllArePersisted() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));
        CompetitorResponse fetched = assertDoesNotThrow(
                () -> ipscCompetitorService.getCompetitor(response.getCompetitorId()));

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), response.getEmailAddresses());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), fetched.getEmailAddresses());
    }

    // createCompetitors()
    private static final String CSV_HEADER =
            "FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses\n";

    @Test
    void testCreateCompetitors_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(null));
    }

    @Test
    void testCreateCompetitors_whenCsvDataIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors("   \t\n  "));
    }

    @Test
    void testCreateCompetitors_whenCsvIsPlainText_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscCompetitorService.createCompetitors("This is not valid CSV data"));
    }

    @Test
    void testCreateCompetitors_whenRequiredColumnsAreMissing_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                FirstName,LastName
                Jane,Doe
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenRowIsMissingRequiredField_thenThrowsValidationException() {
        // Arrange
        String csvData = CSV_HEADER + ",Doe,,,,,,,,,,,\n";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenRowHasUnrecognisedGender_thenThrowsValidationException() {
        // Arrange
        String csvData = CSV_HEADER + "Jane,Doe,,,,Not A Gender,,,,HPSC-001,,,\n";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenRowHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        String csvData = CSV_HEADER + "Jane,Doe,,,,,No Such Club,,,HPSC-001,,,\n";

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyHolder() {
        // Act
        CompetitorResponseHolder holder =
                assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(CSV_HEADER));

        // Assert
        assertNotNull(holder);
        assertTrue(holder.getCompetitors().isEmpty());
    }

    @Test
    void testCreateCompetitors_whenSingleRowWithAllFields_thenPersistsCompetitorWithAllFieldsMapped() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        String csvData = CSV_HEADER +
                "Jane,Doe,Ann,Janie,1990-01-01,Female,Test Club,12345,C-1,HPSC-001,9001015800083,0821234567,jane.doe@example.com;jane2.doe@example.com\n";

        // Act
        CompetitorResponseHolder holder = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(csvData));

        // Assert
        assertEquals(1, holder.getCompetitors().size());
        CompetitorResponse response = holder.getCompetitors().getFirst();
        assertNotNull(response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Ann", response.getMiddleNames());
        assertEquals("Janie", response.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), response.getDateOfBirth());
        assertEquals(Gender.Female, response.getGender());
        assertEquals(ClubIdentifier.HPSC, response.getHomeClub());
        assertEquals(12345, response.getSapsaNumber());
        assertEquals("C-1", response.getCompetitorNumber());
        assertEquals("HPSC-001", response.getClubNumber());
        assertEquals("9001015800083", response.getIdNumber());
        assertEquals("0821234567", response.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), response.getEmailAddresses());

        // The competitor is actually persisted, not just mapped into a response
        CompetitorResponse fetched =
                assertDoesNotThrow(() -> ipscCompetitorService.getCompetitor(response.getCompetitorId()));
        assertEquals("Jane", fetched.getFirstName());
    }

    @Test
    void testCreateCompetitors_whenMultipleValidRows_thenPersistsEachCompetitorInOrder() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        String csvData = CSV_HEADER +
                "Jane,Doe,,,,,Test Club,,,HPSC-001,,,\n" +
                "John,Smith,,,,,Test Club,,,HPSC-002,,,\n";

        // Act
        CompetitorResponseHolder holder = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(csvData));

        // Assert
        List<CompetitorResponse> competitors = holder.getCompetitors();
        assertEquals(2, competitors.size());
        assertEquals("Jane", competitors.get(0).getFirstName());
        assertEquals("HPSC-001", competitors.get(0).getClubNumber());
        assertEquals("John", competitors.get(1).getFirstName());
        assertEquals("HPSC-002", competitors.get(1).getClubNumber());
        assertNotEquals(competitors.get(0).getCompetitorId(), competitors.get(1).getCompetitorId());
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.getCompetitor(999L));
    }

    @Test
    void testGetCompetitor_whenCompetitorExists_thenReturnsCompetitor() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        // Act
        CompetitorResponse fetched = assertDoesNotThrow(() -> ipscCompetitorService.getCompetitor(created.getCompetitorId()));

        // Assert
        assertEquals(created.getCompetitorId(), fetched.getCompetitorId());
        assertEquals("Jane", fetched.getFirstName());
        assertEquals("Doe", fetched.getLastName());
    }

    // patchCompetitor()
    @Test
    void testPatchCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Renamed");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(999L, request));
    }

    @Test
    void testPatchCompetitor_whenOnlyFirstNameIsProvided_thenOnlyFirstNameChanges() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest createRequest = validRequest("HPSC-001");
        createRequest.setHomeClub("Test Club");
        CompetitorResponse created = ipscCompetitorService.createCompetitor(createRequest);

        CompetitorRequest patch = new CompetitorRequest();
        patch.setFirstName("Janet");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));

        // Assert
        assertEquals("Janet", patched.getFirstName());
        assertEquals("Doe", patched.getLastName());
        assertEquals("HPSC-001", patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest createRequest = validRequest("HPSC-001");
        createRequest.setHomeClub("Test Club");
        CompetitorResponse created = ipscCompetitorService.createCompetitor(createRequest);

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    @Test
    void testPatchCompetitor_whenHomeClubChangesToNonHpsc_thenClubNumberBecomesNull() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        createClub("Other Club", ClubIdentifier.SOSC);
        CompetitorRequest createRequest = validRequest("HPSC-001");
        createRequest.setHomeClub("Test Club");
        CompetitorResponse created = ipscCompetitorService.createCompetitor(createRequest);

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("Other Club");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(
                () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));

        // Assert
        assertEquals(ClubIdentifier.SOSC, patched.getHomeClub());
        assertNull(patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    @Test
    void testPatchCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    @Test
    void testPatchCompetitor_whenMultipleEmailAddressesProvided_thenAllArePersisted() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorResponse patched = assertDoesNotThrow(
                () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
        CompetitorResponse fetched = assertDoesNotThrow(
                () -> ipscCompetitorService.getCompetitor(created.getCompetitorId()));

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), patched.getEmailAddresses());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), fetched.getEmailAddresses());
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.updateCompetitor(999L, request));
    }

    @Test
    void testUpdateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), request));
    }

    @Test
    void testUpdateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), request));
    }

    @Test
    void testUpdateCompetitor_whenRequestIsValid_thenReplacesAllFields() {
        // Arrange
        createClub("Other Club", ClubIdentifier.HPSC);
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest replacement = new CompetitorRequest();
        replacement.setFirstName("Different");
        replacement.setLastName("Name");
        replacement.setClubNumber("HPSC-002");
        replacement.setHomeClub("Other Club");

        // Act
        CompetitorResponse updated = assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), replacement));

        // Assert
        assertEquals(created.getCompetitorId(), updated.getCompetitorId());
        assertEquals("Different", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("HPSC-002", updated.getClubNumber());
        assertEquals(ClubIdentifier.HPSC, updated.getHomeClub());
    }

    @Test
    void testUpdateCompetitor_whenHomeClubIsNotHpsc_thenClubNumberIsForcedNull() {
        // Arrange
        createClub("Other Club", ClubIdentifier.SOSC);
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest replacement = validRequest("HPSC-002");
        replacement.setHomeClub("Other Club");

        // Act
        CompetitorResponse updated = assertDoesNotThrow(
                () -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), replacement));

        // Assert
        assertEquals(ClubIdentifier.SOSC, updated.getHomeClub());
        assertNull(updated.getClubNumber());
    }

    @Test
    void testUpdateCompetitor_whenHomeClubIsOmitted_thenExistingHomeClubIsCleared() {
        // Arrange
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest createRequest = validRequest("HPSC-001");
        createRequest.setHomeClub("Test Club");
        CompetitorResponse created = ipscCompetitorService.createCompetitor(createRequest);

        CompetitorRequest replacement = validRequest("HPSC-001");

        // Act
        CompetitorResponse updated = assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), replacement));

        // Assert
        assertNull(updated.getHomeClub());
    }

    // Helpers
    private Club createClub(String name, ClubIdentifier identifier) {
        Club club = new Club();
        club.setName(name);
        club.setIdentifier(identifier);
        return clubRepository.save(club);
    }

    private CompetitorRequest validRequest(String clubNumber) {
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber(clubNumber);
        return request;
    }
}
