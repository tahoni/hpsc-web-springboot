package za.co.hpsc.web.services.impl;

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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link IpscCompetitorServiceImpl}'s impl-only protected helper methods
 * ({@code applyFields}, {@code findCompetitorOrThrow}, {@code resolveGender},
 * {@code resolveHomeClub}, {@code toResponse}, {@code validateForCreate}) - not declared on
 * {@link za.co.hpsc.web.services.IpscCompetitorService}. The interface's create/update/patch/get
 * contract is covered by {@link za.co.hpsc.web.services.IpscCompetitorServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscCompetitorServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscCompetitorServiceImpl ipscCompetitorServiceImpl;

    // applyFields()
    @Test
    void testApplyFields_whenRequestHasAllFields_thenCopiesAllFieldsOntoCompetitor() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setMiddleNames("Ann");
        request.setNickname("Janie");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.Female.toString());
        request.setHomeClub("Test Club");
        request.setSapsaNumber(12345);
        request.setCompetitorNumber("C-1");
        request.setClubNumber("HPSC-001");
        request.setIdNumber("9001015800083");
        request.setCellphoneNumber("0821234567");
        request.setEmailAddress("jane.doe@example.com");

        Competitor competitor = new Competitor();

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals("Ann", competitor.getMiddleNames());
        assertEquals("Janie", competitor.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), competitor.getDateOfBirth());
        assertEquals(Gender.Female, competitor.getGender());
        assertSame(club, competitor.getHomeClub());
        assertEquals(12345, competitor.getSapsaNumber());
        assertEquals("C-1", competitor.getCompetitorNumber());
        assertEquals("HPSC-001", competitor.getClubNumber());
        assertEquals("9001015800083", competitor.getIdNumber());
        assertEquals("0821234567", competitor.getCellphoneNumber());
        assertEquals("jane.doe@example.com", competitor.getEmailAddress());
    }

    @Test
    void testApplyFields_whenHomeClubIsBlank_thenHomeClubIsNull() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber("HPSC-001");
        request.setHomeClub("  ");
        Competitor competitor = new Competitor();

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertNull(competitor.getHomeClub());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testApplyFields_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber("HPSC-001");
        request.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscCompetitorServiceImpl.applyFields(new Competitor(), request));
    }

    // findCompetitorOrThrow()
    @Test
    void testFindCompetitorOrThrow_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorServiceImpl.findCompetitorOrThrow(999L));
    }

    @Test
    void testFindCompetitorOrThrow_whenCompetitorExists_thenReturnsCompetitor() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Competitor found = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.findCompetitorOrThrow(1L));

        // Assert
        assertSame(competitor, found);
    }

    // resolveGender()
    @Test
    void testResolveGender_whenGenderIsNull_thenReturnsNull() {
        assertNull(ipscCompetitorServiceImpl.resolveGender(null));
    }

    @Test
    void testResolveGender_whenGenderIsBlank_thenReturnsNull() {
        assertNull(ipscCompetitorServiceImpl.resolveGender("  "));
    }

    @Test
    void testResolveGender_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.resolveGender("Not A Gender"));
    }

    @Test
    void testResolveGender_whenGenderIsValid_thenReturnsMatchingGender() {
        assertEquals(Gender.Female, ipscCompetitorServiceImpl.resolveGender(Gender.Female.toString()));
    }

    // resolveHomeClub()
    @Test
    void testResolveHomeClub_whenClubNameIsNull_thenReturnsNull() {
        assertNull(ipscCompetitorServiceImpl.resolveHomeClub(null));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testResolveHomeClub_whenClubNameIsBlank_thenReturnsNull() {
        assertNull(ipscCompetitorServiceImpl.resolveHomeClub("  "));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testResolveHomeClub_whenClubNameDoesNotMatchExistingClub_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorServiceImpl.resolveHomeClub("No Such Club"));
    }

    @Test
    void testResolveHomeClub_whenClubNameMatchesExistingClub_thenReturnsClub() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.resolveHomeClub("Test Club"));

        // Assert
        assertSame(club, resolved);
    }

    // toResponse()
    @Test
    void testToResponse_whenCompetitorHasHomeClub_thenMapsHomeClubIdentifier() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);
        Competitor competitor = new Competitor();
        competitor.setHomeClub(club);

        // Act
        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        // Assert
        assertEquals(ClubIdentifier.HPSC, response.getHomeClub());
    }

    @Test
    void testToResponse_whenCompetitorHasNoHomeClub_thenHomeClubIsNull() {
        Competitor competitor = new Competitor();

        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        assertNull(response.getHomeClub());
    }

    @Test
    void testToResponse_whenCompetitorHasAllFields_thenMapsAllFields() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Ann");
        competitor.setNickname("Janie");
        competitor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        competitor.setGender(Gender.Female);
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("C-1");
        competitor.setClubNumber("HPSC-001");
        competitor.setIdNumber("9001015800083");
        competitor.setCellphoneNumber("0821234567");
        competitor.setEmailAddress("jane.doe@example.com");

        // Act
        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        // Assert
        assertEquals(1L, response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Ann", response.getMiddleNames());
        assertEquals("Janie", response.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), response.getDateOfBirth());
        assertEquals(Gender.Female, response.getGender());
        assertEquals(12345, response.getSapsaNumber());
        assertEquals("C-1", response.getCompetitorNumber());
        assertEquals("HPSC-001", response.getClubNumber());
        assertEquals("9001015800083", response.getIdNumber());
        assertEquals("0821234567", response.getCellphoneNumber());
        assertEquals("jane.doe@example.com", response.getEmailAddress());
    }

    // validateForCreate()
    @Test
    void testValidateForCreate_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(null));
    }

    @Test
    void testValidateForCreate_whenFirstNameIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("  ");
        request.setLastName("Doe");
        request.setClubNumber("HPSC-001");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenLastNameIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("  ");
        request.setClubNumber("HPSC-001");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenRequestIsValid_thenDoesNotThrow() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClubNumber("HPSC-001");

        // Act & Assert
        assertDoesNotThrow(() -> ipscCompetitorServiceImpl.validateForCreate(request));
    }
}
