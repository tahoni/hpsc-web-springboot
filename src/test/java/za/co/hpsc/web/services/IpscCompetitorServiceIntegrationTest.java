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
import za.co.hpsc.web.repositories.ClubRepository;

import java.time.LocalDate;

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
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(null));
    }

    @Test
    void testCreateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenLastNameIsMissing_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setLastName(null);

        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenClubNumberIsBlank_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("  ");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setGender("Not A Gender");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsOmitted_thenPersistsCompetitorWithNoHomeClub() {
        CompetitorRequest request = validRequest("HPSC-001");

        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        assertNotNull(response.getCompetitorId());
        assertNull(response.getHomeClub());
    }

    @Test
    void testCreateCompetitor_whenRequestIsValid_thenPersistsCompetitor() {
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
        request.setEmailAddress("jane.doe@example.com");

        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

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
        assertEquals("jane.doe@example.com", response.getEmailAddress());
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.getCompetitor(999L));
    }

    @Test
    void testGetCompetitor_whenCompetitorExists_thenReturnsCompetitor() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorResponse fetched = assertDoesNotThrow(() -> ipscCompetitorService.getCompetitor(created.getCompetitorId()));

        assertEquals(created.getCompetitorId(), fetched.getCompetitorId());
        assertEquals("Jane", fetched.getFirstName());
        assertEquals("Doe", fetched.getLastName());
    }

    // patchCompetitor()
    @Test
    void testPatchCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Renamed");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(999L, request));
    }

    @Test
    void testPatchCompetitor_whenOnlyFirstNameIsProvided_thenOnlyFirstNameChanges() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setFirstName("Janet");

        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));

        assertEquals("Janet", patched.getFirstName());
        assertEquals("Doe", patched.getLastName());
        assertEquals("HPSC-001", patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenClubNumberIsBlank_thenThrowsValidationException() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("  ");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    @Test
    void testPatchCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    @Test
    void testPatchCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setGender("Not A Gender");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(created.getCompetitorId(), patch));
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        CompetitorRequest request = validRequest("HPSC-001");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.updateCompetitor(999L, request));
    }

    @Test
    void testUpdateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        assertThrows(ValidationException.class, () -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), request));
    }

    @Test
    void testUpdateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), request));
    }

    @Test
    void testUpdateCompetitor_whenRequestIsValid_thenReplacesAllFields() {
        createClub("Test Club", ClubIdentifier.HPSC);
        createClub("Other Club", ClubIdentifier.SOSC);
        CompetitorResponse created = ipscCompetitorService.createCompetitor(validRequest("HPSC-001"));

        CompetitorRequest replacement = new CompetitorRequest();
        replacement.setFirstName("Different");
        replacement.setLastName("Name");
        replacement.setClubNumber("HPSC-002");
        replacement.setHomeClub("Other Club");

        CompetitorResponse updated = assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), replacement));

        assertEquals(created.getCompetitorId(), updated.getCompetitorId());
        assertEquals("Different", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("HPSC-002", updated.getClubNumber());
        assertEquals(ClubIdentifier.SOSC, updated.getHomeClub());
    }

    @Test
    void testUpdateCompetitor_whenHomeClubIsOmitted_thenExistingHomeClubIsCleared() {
        createClub("Test Club", ClubIdentifier.HPSC);
        CompetitorRequest createRequest = validRequest("HPSC-001");
        createRequest.setHomeClub("Test Club");
        CompetitorResponse created = ipscCompetitorService.createCompetitor(createRequest);

        CompetitorRequest replacement = validRequest("HPSC-001");

        CompetitorResponse updated = assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(created.getCompetitorId(), replacement));

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
