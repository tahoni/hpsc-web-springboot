package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.impl.IpscCompetitorServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscCompetitorService} contract, exercised entirely through the
 * interface type, with {@link CompetitorRepository}/{@link ClubRepository} mocked. See
 * {@link IpscCompetitorServiceIntegrationTest} for the same contract exercised against a real
 * H2-backed Spring context.
 */
@ExtendWith(MockitoExtension.class)
public class IpscCompetitorServiceTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscCompetitorServiceImpl ipscCompetitorServiceImpl;

    private IpscCompetitorService ipscCompetitorService;

    @BeforeEach
    void setUp() {
        ipscCompetitorService = ipscCompetitorServiceImpl;
    }

    // createCompetitor()
    @Test
    void testCreateCompetitor_whenRequestIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(null));
    }

    @Test
    void testCreateCompetitor_whenFirstNameIsBlank_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName("  ");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenLastNameIsBlank_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setLastName("  ");

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
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsBlank_thenPersistsCompetitorWithNoHomeClub() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("  ");
        stubSaveReturnsSameEntity();

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertNull(response.getHomeClub());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testCreateCompetitor_whenRequestIsValid_thenReturnsMappedResponse() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        stubSaveReturnsSameEntity();

        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("Test Club");
        request.setMiddleNames("Ann");
        request.setNickname("Janie");
        request.setGender(Gender.Female.toString());
        request.setSapsaNumber(12345);
        request.setCompetitorNumber("C-1");
        request.setIdNumber("9001015800083");
        request.setCellphoneNumber("0821234567");
        request.setEmailAddress("jane.doe@example.com");

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertEquals(1L, response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Ann", response.getMiddleNames());
        assertEquals("Janie", response.getNickname());
        assertEquals(Gender.Female, response.getGender());
        assertEquals(ClubIdentifier.HPSC, response.getHomeClub());
        assertEquals(12345, response.getSapsaNumber());
        assertEquals("C-1", response.getCompetitorNumber());
        assertEquals("HPSC-001", response.getClubNumber());
        assertEquals("9001015800083", response.getIdNumber());
        assertEquals("0821234567", response.getCellphoneNumber());
        assertEquals("jane.doe@example.com", response.getEmailAddress());
    }

    @Test
    void testCreateCompetitor_whenRequestIsValid_thenDelegatesToRepository() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        stubSaveReturnsSameEntity();

        // Act
        ipscCompetitorService.createCompetitor(request);

        // Assert
        verify(competitorRepository, times(1)).save(any(Competitor.class));
        verifyNoMoreInteractions(competitorRepository);
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.getCompetitor(999L));
    }

    @Test
    void testGetCompetitor_whenCompetitorExists_thenReturnsMappedResponse() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.getCompetitor(1L));

        // Assert
        assertEquals(1L, response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    // patchCompetitor()
    @Test
    void testPatchCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Renamed");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(999L, request));
    }

    @Test
    void testPatchCompetitor_whenClubNumberIsBlank_thenThrowsValidationException() {
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("  ");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setGender("Not A Gender");

        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenOnlyFirstNameIsProvided_thenOnlyFirstNameChanges() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setFirstName("Jane");
        existing.setLastName("Doe");
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setFirstName("Janet");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals("Janet", patched.getFirstName());
        assertEquals("Doe", patched.getLastName());
        assertEquals("HPSC-001", patched.getClubNumber());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testPatchCompetitor_whenHomeClubIsProvided_thenHomeClubIsResolvedAndSet() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("Test Club");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals(ClubIdentifier.HPSC, patched.getHomeClub());
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class,
                () -> ipscCompetitorService.updateCompetitor(999L, validRequest("HPSC-001")));
    }

    @Test
    void testUpdateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        assertThrows(ValidationException.class, () -> ipscCompetitorService.updateCompetitor(1L, request));
    }

    @Test
    void testUpdateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscCompetitorService.updateCompetitor(1L, request));
    }

    @Test
    void testUpdateCompetitor_whenRequestIsValid_thenReplacesAllFields() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setFirstName("Jane");
        existing.setLastName("Doe");
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        Club otherClub = new Club();
        otherClub.setId(20L);
        otherClub.setName("Other Club");
        otherClub.setIdentifier(ClubIdentifier.SOSC);
        when(clubRepository.findByName("Other Club")).thenReturn(Optional.of(otherClub));
        stubSaveReturnsSameEntity();

        CompetitorRequest replacement = new CompetitorRequest();
        replacement.setFirstName("Different");
        replacement.setLastName("Name");
        replacement.setClubNumber("HPSC-002");
        replacement.setHomeClub("Other Club");

        // Act
        CompetitorResponse updated =
                assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(1L, replacement));

        // Assert
        assertEquals(1L, updated.getCompetitorId());
        assertEquals("Different", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("HPSC-002", updated.getClubNumber());
        assertEquals(ClubIdentifier.SOSC, updated.getHomeClub());
    }

    // Helpers
    private CompetitorRequest validRequest(String clubNumber) {
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber(clubNumber);
        return request;
    }

    private void stubSaveReturnsSameEntity() {
        when(competitorRepository.save(any(Competitor.class))).thenAnswer(invocation -> {
            Competitor competitor = invocation.getArgument(0);
            competitor.setId(1L);
            return competitor;
        });
    }
}
