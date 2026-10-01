package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequestCsv;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscCompetitorServiceImpl}'s impl-only protected helper methods
 * ({@code applyFields}, {@code findCompetitorOrThrow}, {@code isHpscMember}, {@code newCompetitor},
 * {@code readCompetitors},
 * {@code resolveClubNumber}, {@code resolveGender}, {@code resolveHomeClub},
 * {@code splitEmailAddresses}, {@code toRequest}, {@code toResponse}, {@code validateForCreate}) -
 * not declared on {@link za.co.hpsc.web.services.IpscCompetitorService}.
 * The interface's create/update/patch/get contract is covered by
 * {@link za.co.hpsc.web.services.IpscCompetitorServiceTest}.
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
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
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
        request.setPaidUpSapsa(true);
        request.setPaidUpClub(true);
        request.setEmailAddresses(List.of("jane.doe@example.com"));

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
        assertEquals(Boolean.TRUE, competitor.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpClub());
        assertEquals(List.of("jane.doe@example.com"), competitor.getEmailAddresses());
    }

    @Test
    void testApplyFields_whenPaidUpFlagsAreNull_thenCompetitorPaidUpFlagsAreNull() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();
        competitor.setPaidUpSapsa(true);
        competitor.setPaidUpClub(true);

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertNull(competitor.getPaidUpSapsa());
        assertNull(competitor.getPaidUpClub());
    }

    @Test
    void testApplyFields_whenEmailAddressesIsNull_thenCompetitorEmailAddressesIsEmpty() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertEquals(List.of(), competitor.getEmailAddresses());
    }

    @Test
    void testApplyFields_whenMultipleEmailAddresses_thenCompetitorHasAllOfThem() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));
        Competitor competitor = new Competitor();

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), competitor.getEmailAddresses());
    }

    @Test
    void testApplyFields_whenHomeClubIsBlank_thenHomeClubAndClubNumberAreNull() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setHomeClub("  ");
        request.setClubNumber("HPSC-001");
        Competitor competitor = new Competitor();

        // Act
        ipscCompetitorServiceImpl.applyFields(competitor, request);

        // Assert
        assertNull(competitor.getHomeClub());
        assertNull(competitor.getClubNumber());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testApplyFields_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscCompetitorServiceImpl.applyFields(new Competitor(), request));
    }

    // findCompetitorOrThrow()
    @Test
    void testFindCompetitorOrThrow_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findByIdWithHomeClubAndEmailAddresses(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorServiceImpl.findCompetitorOrThrow(999L));
    }

    @Test
    void testFindCompetitorOrThrow_whenCompetitorExists_thenReturnsCompetitor() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        when(competitorRepository.findByIdWithHomeClubAndEmailAddresses(1L)).thenReturn(Optional.of(competitor));

        // Act
        Competitor found = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.findCompetitorOrThrow(1L));

        // Assert
        assertSame(competitor, found);
    }

    // isHpscMember()
    @Test
    void testIsHpscMember_whenHomeClubIsNull_thenReturnsFalse() {
        assertFalse(ipscCompetitorServiceImpl.isHpscMember(null, ClubIdentifier.HPSC));
    }

    @Test
    void testIsHpscMember_whenHomeClubIdentifierParamIsNull_thenReturnsFalse() {
        // Arrange - simulates IpscConstants.HOME_CLUB_IDENTIFIER being null, without needing to
        // touch that real static final field (not reflectively settable on this JDK)
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(ipscCompetitorServiceImpl.isHpscMember(club, null));
    }

    @Test
    void testIsHpscMember_whenHomeClubIdentifierParamIsNullAndHomeClubIdentifierIsAlsoNull_thenReturnsFalse() {
        // Arrange - a club with a null identifier (shouldn't occur via a real persisted Club,
        // whose identifier column is non-null, but must never false-match a null "expected"
        // identifier either)
        Club club = new Club();

        // Act & Assert
        assertFalse(ipscCompetitorServiceImpl.isHpscMember(club, null));
    }

    @Test
    void testIsHpscMember_whenHomeClubIdentifierDoesNotMatch_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);

        // Act & Assert
        assertFalse(ipscCompetitorServiceImpl.isHpscMember(club, ClubIdentifier.HPSC));
    }

    @Test
    void testIsHpscMember_whenHomeClubIdentifierMatches_thenReturnsTrue() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertTrue(ipscCompetitorServiceImpl.isHpscMember(club, ClubIdentifier.HPSC));
    }

    // newCompetitor()
    @Test
    void testNewCompetitor_whenRequestIsValid_thenBuildsUnsavedCompetitor() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        Competitor competitor = ipscCompetitorServiceImpl.newCompetitor(request);

        // Assert
        assertNull(competitor.getId());
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals(List.of("jane.doe@example.com"), competitor.getEmailAddresses());
        verifyNoInteractions(competitorRepository);
    }

    @Test
    void testNewCompetitor_whenRequestIsInvalid_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setLastName("Doe");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.newCompetitor(request));
    }

    // readCompetitors()
    @Test
    void testReadCompetitors_whenValidCsv_thenReturnsCompetitorRequestForCSVList() {
        // Arrange
        String csvData = """
                FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                Jane,Doe,Ann,Janie,1990-01-01,Female,Test Club,12345,C-1,HPSC-001,9001015800083,0821234567,jane.doe@example.com
                John,Smith,,,,,,,,HPSC-002,,,
                """;

        // Act
        List<CompetitorRequestCsv> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertEquals(2, rows.size());

        CompetitorRequestCsv first = rows.getFirst();
        assertEquals("Jane", first.getFirstName());
        assertEquals("Doe", first.getLastName());
        assertEquals("Ann", first.getMiddleNames());
        assertEquals("Janie", first.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), first.getDateOfBirth());
        assertEquals("Female", first.getGender());
        assertEquals("Test Club", first.getHomeClub());
        assertEquals(12345, first.getSapsaNumber());
        assertEquals("C-1", first.getCompetitorNumber());
        assertEquals("HPSC-001", first.getClubNumber());
        assertEquals("9001015800083", first.getIdNumber());
        assertEquals("0821234567", first.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com"), first.getEmailAddresses());

        CompetitorRequestCsv second = rows.get(1);
        assertEquals("John", second.getFirstName());
        assertEquals("Smith", second.getLastName());
        assertEquals("HPSC-002", second.getClubNumber());
    }

    @Test
    void testReadCompetitors_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        // Arrange
        String csvData = """
                ClubNumber,LastName,FirstName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                HPSC-001,Doe,Jane,,,,,,,,,,
                """;

        // Act
        List<CompetitorRequestCsv> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertEquals("Doe", rows.getFirst().getLastName());
        assertEquals("HPSC-001", rows.getFirst().getClubNumber());
    }

    @Test
    void testReadCompetitors_whenMiddleNamesColumnIsMissing_thenMiddleNamesIsNull() {
        // Arrange
        String csvData = """
                FirstName,LastName,Nickname
                Jane,Doe,Janie
                """;

        // Act
        List<CompetitorRequestCsv> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertNull(rows.getFirst().getMiddleNames());
    }

    @Test
    void testReadCompetitors_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyList() {
        // Arrange
        String csvData =
                "FirstName,LastName,MiddleNames,Nickname,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub\n";

        // Act
        List<CompetitorRequestCsv> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertTrue(rows.isEmpty());
    }

    @Test
    void testReadCompetitors_whenHeaderIsMissingRequiredColumn_thenThrowsValidationException() {
        // Arrange
        String csvData = "FirstName,Nickname\nJane,Janie\n";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.readCompetitors(csvData));
    }

    @Test
    void testReadCompetitors_whenCsvHasNoHeaderRow_thenThrowsValidationException() {
        // Arrange
        String csvData = "Invalid CSV With One Column and no Header\nJane\n";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.readCompetitors(csvData));
    }

    @Test
    void testReadCompetitors_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.readCompetitors(null));
    }

    // resolveClubNumber()
    @Test
    void testResolveClubNumber_whenHomeClubIsNull_thenReturnsNull() {
        assertNull(ipscCompetitorServiceImpl.resolveClubNumber(null, "HPSC-001"));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsNotHpsc_thenReturnsNull() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);

        // Act & Assert
        assertNull(ipscCompetitorServiceImpl.resolveClubNumber(club, "HPSC-001"));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsNull_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.resolveClubNumber(club, null));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.resolveClubNumber(club, "  "));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsValid_thenReturnsClubNumber() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertEquals("HPSC-001", ipscCompetitorServiceImpl.resolveClubNumber(club, "HPSC-001"));
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

    // toRequest()
    @Test
    void testToRequest_whenAllFieldsPresent_thenMapsAllFieldsOntoCompetitorRequest() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "Jane", "Doe", "Ann", "Janie", LocalDate.of(1990, 1, 1), "Female", "Test Club",
                12345, "C-1", "HPSC-001", "9001015800083", "0821234567", true, false,
                List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertNull(request.getCompetitorId());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Ann", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), request.getDateOfBirth());
        assertEquals("Female", request.getGender());
        assertEquals("Test Club", request.getHomeClub());
        assertEquals(12345, request.getSapsaNumber());
        assertEquals("C-1", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(Boolean.TRUE, request.getPaidUpSapsa());
        assertEquals(Boolean.FALSE, request.getPaidUpClub());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testToRequest_whenTextFieldsAreMixedCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "jANE", "o'NEIL-smith", "ann marie", "JANIE", null, "FEMALE", "test CLUB",
                null, "c-1a", "hpsc-001", null, null, null, null, List.of("Jane.Doe@Example.com"));

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals("Female", request.getGender());
        assertEquals("test CLUB", request.getHomeClub());
        assertEquals("c-1a", request.getCompetitorNumber());
        assertEquals("hpsc-001", request.getClubNumber());
        assertEquals(List.of("Jane.Doe@Example.com"), request.getEmailAddresses());
    }

    @Test
    void testToRequest_whenTextFieldsAreAllUpperCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "JANE", "O'NEIL-SMITH", "ANN MARIE", "JANIE", LocalDate.of(1990, 1, 1), "FEMALE", "TEST CLUB",
                12345, "C-1A", "HPSC-001", "9001015800083", "0821234567", true, false,
                List.of("JANE.DOE@EXAMPLE.COM", "JANE2.DOE@EXAMPLE.COM"));

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals("Female", request.getGender());
        assertEquals("TEST CLUB", request.getHomeClub());
        assertEquals("C-1A", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(List.of("JANE.DOE@EXAMPLE.COM", "JANE2.DOE@EXAMPLE.COM"), request.getEmailAddresses());
    }

    @Test
    void testToRequest_whenTextFieldsAreAllLowerCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "jane", "o'neil-smith", "ann marie", "janie", null, "female", "test club",
                null, "c-1a", "hpsc-001", null, null, null, null, List.of("jane.doe@example.com"));

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals("Female", request.getGender());
        assertEquals("test club", request.getHomeClub());
        assertEquals("c-1a", request.getCompetitorNumber());
        assertEquals("hpsc-001", request.getClubNumber());
        assertEquals(List.of("jane.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testToRequest_whenIdAndCellphoneNumbersAreLowerCase_thenKeepsThemAsSupplied() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "jane", "doe", null, null, null, null, null, null, null, null, "ab123456x", "+27 82 abc-1234", null,
                null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("ab123456x", request.getIdNumber());
        assertEquals("+27 82 abc-1234", request.getCellphoneNumber());
    }

    @Test
    void testToRequest_whenIdAndCellphoneNumbersAreUpperCase_thenKeepsThemAsSupplied() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "JANE", "DOE", null, null, null, null, null, null, null, null, "AB123456X", "+27 82 ABC-1234", null,
                null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("AB123456X", request.getIdNumber());
        assertEquals("+27 82 ABC-1234", request.getCellphoneNumber());
    }

    @Test
    void testToRequest_whenUpperCaseLastNameHasMultipleParticles_thenLowerCasesAllOfThem() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "JANE", "DE LA REY", null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("de la Rey", request.getLastName());
    }

    @Test
    void testToRequest_whenUpperCaseLastNameStartsWithParticleLetters_thenProperCasesItOnly() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "JANE", "DUBE", null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("Dube", request.getLastName());
    }

    @Test
    void testToRequest_whenLastNameIsHyphenatedWithParticles_thenLowerCasesThem() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "Jane", "SMITH-VAN DER MERWE", null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("Smith-van der Merwe", request.getLastName());
    }

    @Test
    void testToRequest_whenLastNameHasMcOrApostrophePrefix_thenCapitalisesCorrectly() {
        // Arrange
        CompetitorRequestCsv mcRequest = new CompetitorRequestCsv(
                "JANE", "MCDONALD", null, null, null, null, null, null, null, null, null, null, null, null, null);
        CompetitorRequestCsv apostropheRequest = new CompetitorRequestCsv(
                "JANE", "o’NEIL", null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act & Assert
        assertEquals("McDonald", ipscCompetitorServiceImpl.toRequest(mcRequest).getLastName());
        assertEquals("O’Neil", ipscCompetitorServiceImpl.toRequest(apostropheRequest).getLastName());
    }

    @Test
    void testToRequest_whenLastNameHasParticles_thenLowerCasesThem() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "Jane", "VAN DER MERWE", null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertEquals("van der Merwe", request.getLastName());
    }

    @Test
    void testToRequest_whenOptionalFieldsAreNull_thenMapsNullsThrough() {
        // Arrange
        CompetitorRequestCsv competitorRequestCsv = new CompetitorRequestCsv(
                "Jane", "Doe", null, null, null, null, null, null, null, "HPSC-001", null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.toRequest(competitorRequestCsv);

        // Assert
        assertNull(request.getCompetitorId());
        assertNull(request.getMiddleNames());
        assertNull(request.getNickname());
        assertNull(request.getDateOfBirth());
        assertNull(request.getGender());
        assertNull(request.getHomeClub());
        assertNull(request.getSapsaNumber());
        assertNull(request.getCompetitorNumber());
        assertNull(request.getIdNumber());
        assertNull(request.getCellphoneNumber());
        assertNull(request.getPaidUpSapsa());
        assertNull(request.getPaidUpClub());
        assertEquals(List.of(), request.getEmailAddresses());
    }

    // toResponse()
    @Test
    void testToResponse_whenCompetitorHasHomeClub_thenMapsHomeClubIdentifier() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        Competitor competitor = new Competitor();
        competitor.setHomeClub(club);

        // Act
        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        // Assert
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, response.getHomeClub());
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
        competitor.setPaidUpSapsa(true);
        competitor.setPaidUpClub(false);
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));

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
        assertEquals(Boolean.TRUE, response.getPaidUpSapsa());
        assertEquals(Boolean.FALSE, response.getPaidUpClub());
        assertEquals(List.of("jane.doe@example.com"), response.getEmailAddresses());
    }

    @Test
    void testToResponse_whenMultipleEmailAddresses_thenAllAreMapped() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setEmailAddresses(List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), response.getEmailAddresses());
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

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenLastNameIsBlank_thenThrowsValidationException() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenRequestIsValid_thenDoesNotThrow() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        // Act & Assert
        assertDoesNotThrow(() -> ipscCompetitorServiceImpl.validateForCreate(request));
    }
}
