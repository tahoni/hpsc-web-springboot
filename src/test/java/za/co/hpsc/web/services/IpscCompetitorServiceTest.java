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
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.impl.IpscCompetitorServiceImpl;

import java.time.LocalDate;
import java.util.List;
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
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(null));
    }

    @Test
    void testCreateCompetitor_whenFirstNameIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenLastNameIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setLastName("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsHpscAndClubNumberIsMissing_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setName("HPSC");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("HPSC")).thenReturn(Optional.of(club));

        CompetitorRequest request = validRequest(null);
        request.setHomeClub("HPSC");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setName("HPSC");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("HPSC")).thenReturn(Optional.of(club));

        CompetitorRequest request = validRequest("  ");
        request.setHomeClub("HPSC");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenHomeClubIsNotHpscAndClubNumberIsProvided_thenClubNumberIsNull() {
        // Arrange
        Club club = new Club();
        club.setName("Other Club");
        club.setIdentifier(ClubIdentifier.SOSC);
        when(clubRepository.findByName("Other Club")).thenReturn(Optional.of(club));
        stubSaveReturnsSameEntity();

        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("Other Club");

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertNull(response.getClubNumber());
    }

    @Test
    void testCreateCompetitor_whenNoHomeClubAndClubNumberIsProvided_thenClubNumberIsNull() {
        // Arrange
        stubSaveReturnsSameEntity();
        CompetitorRequest request = validRequest("HPSC-001");

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
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
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
        assertNull(response.getClubNumber());
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
        request.setEmailAddresses(List.of("jane.doe@example.com"));

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
        assertEquals(List.of("jane.doe@example.com"), response.getEmailAddresses());
    }

    @Test
    void testCreateCompetitor_whenMultipleEmailAddresses_thenAllAreMapped() {
        // Arrange
        stubSaveReturnsSameEntity();
        CompetitorRequest request = validRequest("HPSC-001");
        request.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorResponse response = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitor(request));

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), response.getEmailAddresses());
    }

    @Test
    void testCreateCompetitor_whenRequestIsValid_thenDelegatesToRepository() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        stubSaveReturnsSameEntity();

        // Act
        ipscCompetitorService.createCompetitor(request);

        // Assert
        verify(competitorRepository).save(any(Competitor.class));
        verifyNoMoreInteractions(competitorRepository);
    }

    // createCompetitors()
    @Test
    void testCreateCompetitors_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(null));
    }

    @Test
    void testCreateCompetitors_whenCsvDataIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors("   "));
    }

    @Test
    void testCreateCompetitors_whenCsvDataIsMalformed_thenThrowsValidationException() {
        // Arrange
        String csvData = "NotAHeader\nJane,Doe";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenSingleValidRow_thenReturnsHolderWithMappedResponse() {
        // Arrange
        Club club = new Club();
        club.setName("HPSC");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("HPSC")).thenReturn(Optional.of(club));
        stubSaveReturnsSameEntity();
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                Jane,Doe,,,,,HPSC,,,HPSC-001,,,
                """;

        // Act
        CompetitorResponseHolder holder = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(csvData));

        // Assert
        assertEquals(1, holder.getCompetitors().size());
        assertEquals("Jane", holder.getCompetitors().getFirst().getFirstName());
        assertEquals("Doe", holder.getCompetitors().getFirst().getLastName());
        assertEquals("HPSC-001", holder.getCompetitors().getFirst().getClubNumber());
    }

    @Test
    void testCreateCompetitors_whenRowHasMultipleEmailAddresses_thenAllAreMapped() {
        // Arrange
        stubSaveReturnsSameEntity();
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                Jane,Doe,,,,,,,,HPSC-001,,,jane.doe@example.com;jane2.doe@example.com
                """;

        // Act
        CompetitorResponseHolder holder = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(csvData));

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"),
                holder.getCompetitors().getFirst().getEmailAddresses());
    }

    @Test
    void testCreateCompetitors_whenMultipleValidRows_thenPersistsEachRowInOrder() {
        // Arrange
        stubSaveReturnsSameEntity();
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                Jane,Doe,,,,,,,,HPSC-001,,,
                John,Smith,,,,,,,,HPSC-002,,,
                """;

        // Act
        CompetitorResponseHolder holder = assertDoesNotThrow(() -> ipscCompetitorService.createCompetitors(csvData));

        // Assert
        assertEquals(2, holder.getCompetitors().size());
        assertEquals("Jane", holder.getCompetitors().get(0).getFirstName());
        assertEquals("John", holder.getCompetitors().get(1).getFirstName());
        verify(competitorRepository, times(2)).save(any(Competitor.class));
    }

    @Test
    void testCreateCompetitors_whenRowIsMissingRequiredField_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                ,Doe,,,,,,,,,,,
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenRowHasUnrecognisedGender_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                Jane,Doe,,,,Not A Gender,,,,HPSC-001,,,
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    @Test
    void testCreateCompetitors_whenRowHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses
                Jane,Doe,,,,,No Such Club,,,HPSC-001,,,
                """;

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.createCompetitors(csvData));
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
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
        // Arrange
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Renamed");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorService.patchCompetitor(999L, request));
    }

    @Test
    void testPatchCompetitor_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setHomeClub(club);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenClubNumberIsNotTouched_thenClubNumberIsUnchanged() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setFirstName("Janet");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals("HPSC-001", patched.getClubNumber());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testPatchCompetitor_whenHomeClubChangesToNonHpsc_thenClubNumberBecomesNull() {
        // Arrange
        Club existingClub = new Club();
        existingClub.setIdentifier(ClubIdentifier.HPSC);
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setHomeClub(existingClub);
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        Club otherClub = new Club();
        otherClub.setName("Other Club");
        otherClub.setIdentifier(ClubIdentifier.SOSC);
        when(clubRepository.findByName("Other Club")).thenReturn(Optional.of(otherClub));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("Other Club");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertNull(patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenHomeClubChangesToHpscWithoutClubNumber_thenThrowsValidationException() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        Club club = new Club();
        club.setName("HPSC");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("HPSC")).thenReturn(Optional.of(club));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("HPSC");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        CompetitorRequest patch = new CompetitorRequest();
        patch.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.patchCompetitor(1L, patch));
    }

    @Test
    void testPatchCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        CompetitorRequest patch = new CompetitorRequest();
        patch.setHomeClub("No Such Club");

        // Act & Assert
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
        patch.setClubNumber("HPSC-010");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals(ClubIdentifier.HPSC, patched.getHomeClub());
        assertEquals("HPSC-010", patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenGenderIsProvided_thenGenderIsResolvedAndSet() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setGender(Gender.Female.toString());

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals(Gender.Female, patched.getGender());
    }

    @Test
    void testPatchCompetitor_whenClubNumberIsProvided_thenClubNumberChanges() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setHomeClub(club);
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("HPSC-002");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals("HPSC-002", patched.getClubNumber());
    }

    @Test
    void testPatchCompetitor_whenClubNumberIsProvidedButHomeClubIsNotHpsc_thenClubNumberIsForcedNull() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setHomeClub(club);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setClubNumber("HPSC-002");

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertNull(patched.getClubNumber());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testPatchCompetitor_whenRemainingSimpleFieldsAreProvided_thenAllChange() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        CompetitorRequest patch = new CompetitorRequest();
        patch.setLastName("Smith");
        patch.setMiddleNames("Ann");
        patch.setNickname("Janie");
        patch.setDateOfBirth(dateOfBirth);
        patch.setSapsaNumber(12345);
        patch.setCompetitorNumber("C-001");
        patch.setIdNumber("8001015800083");
        patch.setCellphoneNumber("0821234567");
        patch.setEmailAddresses(List.of("jane@example.com"));

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals("Smith", patched.getLastName());
        assertEquals("Ann", patched.getMiddleNames());
        assertEquals("Janie", patched.getNickname());
        assertEquals(dateOfBirth, patched.getDateOfBirth());
        assertEquals(12345, patched.getSapsaNumber());
        assertEquals("C-001", patched.getCompetitorNumber());
        assertEquals("8001015800083", patched.getIdNumber());
        assertEquals("0821234567", patched.getCellphoneNumber());
        assertEquals(List.of("jane@example.com"), patched.getEmailAddresses());
    }

    @Test
    void testPatchCompetitor_whenMultipleEmailAddressesProvided_thenAllAreSet() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubSaveReturnsSameEntity();

        CompetitorRequest patch = new CompetitorRequest();
        patch.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorResponse patched = assertDoesNotThrow(() -> ipscCompetitorService.patchCompetitor(1L, patch));

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), patched.getEmailAddresses());
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscCompetitorService.updateCompetitor(999L, validRequest("HPSC-001")));
    }

    @Test
    void testUpdateCompetitor_whenFirstNameIsMissing_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = validRequest("HPSC-001");
        request.setFirstName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorService.updateCompetitor(1L, request));
    }

    @Test
    void testUpdateCompetitor_whenHomeClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        CompetitorRequest request = validRequest("HPSC-001");
        request.setHomeClub("No Such Club");

        // Act & Assert
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
        otherClub.setIdentifier(ClubIdentifier.HPSC);
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
        assertEquals(ClubIdentifier.HPSC, updated.getHomeClub());
    }

    @Test
    void testUpdateCompetitor_whenHomeClubIsNotHpsc_thenClubNumberIsForcedNull() {
        // Arrange
        Competitor existing = new Competitor();
        existing.setId(1L);
        existing.setClubNumber("HPSC-001");
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existing));

        Club otherClub = new Club();
        otherClub.setName("Other Club");
        otherClub.setIdentifier(ClubIdentifier.SOSC);
        when(clubRepository.findByName("Other Club")).thenReturn(Optional.of(otherClub));
        stubSaveReturnsSameEntity();

        CompetitorRequest replacement = validRequest("HPSC-002");
        replacement.setHomeClub("Other Club");

        // Act
        CompetitorResponse updated =
                assertDoesNotThrow(() -> ipscCompetitorService.updateCompetitor(1L, replacement));

        // Assert
        assertEquals(ClubIdentifier.SOSC, updated.getHomeClub());
        assertNull(updated.getClubNumber());
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
