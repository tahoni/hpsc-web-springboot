package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.mappers.CompetitorMapper;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscCompetitorServiceImpl}'s impl-only protected helper methods
 * ({@code findCompetitorOrThrow}, {@code newCompetitor}, {@code readCompetitors}, {@code normaliseCsvRequest},
 * {@code toResponse}, {@code validateForCreate}) - not declared on
 * {@link za.co.hpsc.web.services.IpscCompetitorService}. The field-copying and lookup helpers are covered by
 * {@link za.co.hpsc.web.mappers.CompetitorMapperTest}.
 * The interface's create/update/patch/get contract is covered by
 * {@link za.co.hpsc.web.services.IpscCompetitorServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscCompetitorServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Spy
    private CompetitorMapper competitorMapper = new CompetitorMapper(mock(ClubRepository.class), new IpscEntityClubServiceImpl(mock(ClubRepository.class)));

    @InjectMocks
    private IpscCompetitorServiceImpl ipscCompetitorServiceImpl;

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

    // normaliseCsvRequest()
    @Test
    void testNormaliseCsvRequest_whenAllFieldsPresent_thenMapsAllFieldsOntoCompetitorRequest() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "Jane", "Doe", "Ann", "Janie", LocalDate.of(1990, 1, 1), "Female", "Test Club",
                12345, "7001", "HPSC-001", "9001015800083", "0821234567", List.of("jane.doe@example.com", "jane2.doe@example.com"), true, true, false,
                true);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertNull(request.getCompetitorId());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Ann", request.getMiddleNames());
        assertEquals("Janie", request.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), request.getDateOfBirth());
        assertEquals("Female", request.getGender());
        assertEquals("Test Club", request.getHomeClub());
        assertEquals(12345, request.getSapsaNumber());
        assertEquals("7001", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(Boolean.TRUE, request.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, request.getPaidUpNgpsa());
        assertEquals(Boolean.FALSE, request.getPaidUpClub());
        assertEquals(Boolean.TRUE, request.getIsVerified());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testNormaliseCsvRequest_whenTextFieldsAreMixedCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "jANE", "o'NEIL-smith", "ann marie", "JANIE", null, "FEMALE", "test CLUB",
                null, "7002", "hpsc-001", null, null, List.of("Jane.Doe@Example.com"), null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickName());
        assertEquals("Female", request.getGender());
        assertEquals("test CLUB", request.getHomeClub());
        assertEquals("7002", request.getCompetitorNumber());
        assertEquals("hpsc-001", request.getClubNumber());
        assertEquals(List.of("Jane.Doe@Example.com"), request.getEmailAddresses());
    }

    @Test
    void testNormaliseCsvRequest_whenTextFieldsAreAllUpperCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "JANE", "O'NEIL-SMITH", "ANN MARIE", "JANIE", LocalDate.of(1990, 1, 1), "FEMALE", "TEST CLUB",
                12345, "7002", "HPSC-001", "9001015800083", "0821234567", List.of("JANE.DOE@EXAMPLE.COM", "JANE2.DOE@EXAMPLE.COM"), true, null, false,
                null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickName());
        assertEquals("Female", request.getGender());
        assertEquals("TEST CLUB", request.getHomeClub());
        assertEquals("7002", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(List.of("JANE.DOE@EXAMPLE.COM", "JANE2.DOE@EXAMPLE.COM"), request.getEmailAddresses());
    }

    @Test
    void testNormaliseCsvRequest_whenTextFieldsAreAllLowerCase_thenProperCasesAllButHomeClubNumbersAndEmailAddresses() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "jane", "o'neil-smith", "ann marie", "janie", null, "female", "test club",
                null, "7002", "hpsc-001", null, null, List.of("jane.doe@example.com"), null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("O'Neil-Smith", request.getLastName());
        assertEquals("Ann Marie", request.getMiddleNames());
        assertEquals("Janie", request.getNickName());
        assertEquals("Female", request.getGender());
        assertEquals("test club", request.getHomeClub());
        assertEquals("7002", request.getCompetitorNumber());
        assertEquals("hpsc-001", request.getClubNumber());
        assertEquals(List.of("jane.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testNormaliseCsvRequest_whenIdAndCellphoneNumbersAreLowerCase_thenKeepsThemAsSupplied() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "jane", "doe", null, null, null, null, null, null, null, null, "ab123456x", "+27 82 abc-1234", null,
                null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("ab123456x", request.getIdNumber());
        assertEquals("+27 82 abc-1234", request.getCellphoneNumber());
    }

    @Test
    void testNormaliseCsvRequest_whenIdAndCellphoneNumbersAreUpperCase_thenKeepsThemAsSupplied() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "JANE", "DOE", null, null, null, null, null, null, null, null, "AB123456X", "+27 82 ABC-1234", null,
                null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("AB123456X", request.getIdNumber());
        assertEquals("+27 82 ABC-1234", request.getCellphoneNumber());
    }

    @Test
    void testNormaliseCsvRequest_whenUpperCaseLastNameHasMultipleParticles_thenLowerCasesAllOfThem() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "JANE", "DE LA REY", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("de la Rey", request.getLastName());
    }

    @Test
    void testNormaliseCsvRequest_whenUpperCaseLastNameStartsWithParticleLetters_thenProperCasesItOnly() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "JANE", "DUBE", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("Dube", request.getLastName());
    }

    @Test
    void testNormaliseCsvRequest_whenLastNameIsHyphenatedWithParticles_thenLowerCasesThem() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "Jane", "SMITH-VAN DER MERWE", null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("Smith-van der Merwe", request.getLastName());
    }

    @Test
    void testNormaliseCsvRequest_whenLastNameHasMcOrApostrophePrefix_thenCapitalisesCorrectly() {
        // Arrange
        CompetitorRequest mcRequest = new CompetitorRequest(null, 
                "JANE", "MCDONALD", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        CompetitorRequest apostropheRequest = new CompetitorRequest(null, 
                "JANE", "o’NEIL", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act & Assert
        assertEquals("McDonald", ipscCompetitorServiceImpl.normaliseCsvRequest(mcRequest).getLastName());
        assertEquals("O’Neil", ipscCompetitorServiceImpl.normaliseCsvRequest(apostropheRequest).getLastName());
    }

    @Test
    void testNormaliseCsvRequest_whenLastNameHasParticles_thenLowerCasesThem() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "Jane", "VAN DER MERWE", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertEquals("van der Merwe", request.getLastName());
    }

    @Test
    void testNormaliseCsvRequest_whenOptionalFieldsAreNull_thenMapsNullsThrough() {
        // Arrange
        CompetitorRequest csvRow = new CompetitorRequest(null, 
                "Jane", "Doe", null, null, null, null, null, null, null, "HPSC-001", null, null, null, null, null, null, null);

        // Act
        CompetitorRequest request = ipscCompetitorServiceImpl.normaliseCsvRequest(csvRow);

        // Assert
        assertNull(request.getCompetitorId());
        assertNull(request.getMiddleNames());
        assertNull(request.getNickName());
        assertNull(request.getDateOfBirth());
        assertNull(request.getGender());
        assertNull(request.getHomeClub());
        assertNull(request.getSapsaNumber());
        assertNull(request.getCompetitorNumber());
        assertNull(request.getIdNumber());
        assertNull(request.getCellphoneNumber());
        assertNull(request.getPaidUpSapsa());
        assertNull(request.getPaidUpNgpsa());
        assertNull(request.getPaidUpClub());
        assertEquals(List.of(), request.getEmailAddresses());
    }

    // readCompetitors()
    @Test
    void testReadCompetitors_whenValidCsv_thenReturnsCompetitorRequestForCSVList() {
        // Arrange
        String csvData = """
                FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                Jane,Doe,Ann,Janie,1990-01-01,Female,Test Club,12345,7001,HPSC-001,9001015800083,0821234567,jane.doe@example.com
                John,Smith,,,,,,,,HPSC-002,,,
                """;

        // Act
        List<CompetitorRequest> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertEquals(2, rows.size());

        CompetitorRequest first = rows.getFirst();
        assertEquals("Jane", first.getFirstName());
        assertEquals("Doe", first.getLastName());
        assertEquals("Ann", first.getMiddleNames());
        assertEquals("Janie", first.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), first.getDateOfBirth());
        assertEquals("Female", first.getGender());
        assertEquals("Test Club", first.getHomeClub());
        assertEquals(12345, first.getSapsaNumber());
        assertEquals("7001", first.getCompetitorNumber());
        assertEquals("HPSC-001", first.getClubNumber());
        assertEquals("9001015800083", first.getIdNumber());
        assertEquals("0821234567", first.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com"), first.getEmailAddresses());

        CompetitorRequest second = rows.get(1);
        assertEquals("John", second.getFirstName());
        assertEquals("Smith", second.getLastName());
        assertEquals("HPSC-002", second.getClubNumber());
    }

    @Test
    void testReadCompetitors_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        // Arrange
        String csvData = """
                ClubNumber,LastName,FirstName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
                HPSC-001,Doe,Jane,,,,,,,,,,
                """;

        // Act
        List<CompetitorRequest> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

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
                FirstName,LastName,NickName
                Jane,Doe,Janie
                """;

        // Act
        List<CompetitorRequest> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Jane", rows.getFirst().getFirstName());
        assertNull(rows.getFirst().getMiddleNames());
    }

    @Test
    void testReadCompetitors_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyList() {
        // Arrange
        String csvData =
                "FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub\n";

        // Act
        List<CompetitorRequest> rows = assertDoesNotThrow(() -> ipscCompetitorServiceImpl.readCompetitors(csvData));

        // Assert
        assertTrue(rows.isEmpty());
    }

    @Test
    void testReadCompetitors_whenHeaderIsMissingRequiredColumn_thenThrowsValidationException() {
        // Arrange
        String csvData = "FirstName,NickName\nJane,Janie\n";

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
        competitor.setNickName("Janie");
        competitor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        competitor.setGender(Gender.Female);
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber(7001);
        competitor.setClubNumber("HPSC-001");
        competitor.setIdNumber("9001015800083");
        competitor.setCellphoneNumber("0821234567");
        competitor.setPaidUpSapsa(true);
        competitor.setPaidUpNgpsa(true);
        competitor.setPaidUpClub(false);
        competitor.setIsVerified(true);
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        CompetitorResponse response = ipscCompetitorServiceImpl.toResponse(competitor);

        // Assert
        assertEquals(1L, response.getCompetitorId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Ann", response.getMiddleNames());
        assertEquals("Janie", response.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), response.getDateOfBirth());
        assertEquals(Gender.Female, response.getGender());
        assertEquals(12345, response.getSapsaNumber());
        assertEquals(7001, response.getCompetitorNumber());
        assertEquals("HPSC-001", response.getClubNumber());
        assertEquals("9001015800083", response.getIdNumber());
        assertEquals("0821234567", response.getCellphoneNumber());
        assertEquals(Boolean.TRUE, response.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, response.getPaidUpNgpsa());
        assertEquals(Boolean.FALSE, response.getPaidUpClub());
        assertEquals(Boolean.TRUE, response.getIsVerified());
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
