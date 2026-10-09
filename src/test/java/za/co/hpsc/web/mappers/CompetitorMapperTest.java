package za.co.hpsc.web.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.impl.IpscEntityClubServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CompetitorMapper}, with {@link ClubRepository} mocked.
 */
@ExtendWith(MockitoExtension.class)
public class CompetitorMapperTest {

    @Mock
    private ClubRepository clubRepository;

    private CompetitorMapper competitorMapper;

    @BeforeEach
    void setUp() {
        competitorMapper = new CompetitorMapper(new IpscEntityClubServiceImpl(clubRepository));
    }

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
        request.setNickName("Janie");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.Female.toString());
        request.setHomeClub("Test Club");
        request.setSapsaNumber(12345);
        request.setCompetitorNumber("7001");
        request.setClubNumber("HPSC-001");
        request.setIdNumber("9001015800083");
        request.setCellphoneNumber("0821234567");
        request.setPaidUpSapsa(true);
        request.setPaidUpClub(true);
        request.setIsVerified(true);
        request.setEmailAddresses(List.of("jane.doe@example.com"));

        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals("Ann", competitor.getMiddleNames());
        assertEquals("Janie", competitor.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), competitor.getDateOfBirth());
        assertEquals(Gender.Female, competitor.getGender());
        assertSame(club, competitor.getHomeClub());
        assertEquals(12345, competitor.getSapsaNumber());
        assertEquals(7001, competitor.getCompetitorNumber());
        assertEquals("HPSC-001", competitor.getClubNumber());
        assertEquals("9001015800083", competitor.getIdNumber());
        assertEquals("0821234567", competitor.getCellphoneNumber());
        assertEquals(Boolean.TRUE, competitor.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpClub());
        assertEquals(Boolean.TRUE, competitor.getIsVerified());
        assertEquals(List.of("jane.doe@example.com"), competitor.getEmailAddresses());
    }

    @Test
    void testApplyFields_whenNickNameIsNull_thenNickNameDefaultsToTheFirstName() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertEquals("Jane", competitor.getNickName());
    }

    @Test
    void testApplyFields_whenNickNameIsEmptyOrBlank_thenNickNameDefaultsToTheFirstName() {
        // Arrange
        CompetitorRequest emptyNickName = new CompetitorRequest();
        emptyNickName.setFirstName("Jane");
        emptyNickName.setLastName("Doe");
        emptyNickName.setNickName("");
        CompetitorRequest blankNickName = new CompetitorRequest();
        blankNickName.setFirstName("John");
        blankNickName.setLastName("Doe");
        blankNickName.setNickName("   ");
        Competitor first = new Competitor();
        Competitor second = new Competitor();

        // Act
        competitorMapper.applyFields(first, emptyNickName);
        competitorMapper.applyFields(second, blankNickName);

        // Assert
        assertEquals("Jane", first.getNickName());
        assertEquals("John", second.getNickName());
    }

    @Test
    void testApplyFields_whenNickNameIsNull_thenReplacesAnExistingNickName() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();
        competitor.setNickName("Janie");

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertEquals("Jane", competitor.getNickName());
    }

    @Test
    void testApplyFields_whenNickNameIsSupplied_thenKeepsItInsteadOfTheFirstName() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setNickName("Janie");
        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertEquals("Janie", competitor.getNickName());
    }

    @Test
    void testApplyFields_whenPaidUpNgpsaIsSupplied_thenSetsItIndependentlyOfTheOtherPaidUpFlags() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPaidUpSapsa(false);
        request.setPaidUpNgpsa(true);
        request.setPaidUpClub(false);
        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertEquals(Boolean.FALSE, competitor.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpNgpsa());
        assertEquals(Boolean.FALSE, competitor.getPaidUpClub());
    }

    @Test
    void testApplyFields_whenPaidUpNgpsaIsNull_thenClearsExistingValue() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();
        competitor.setPaidUpNgpsa(true);

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertNull(competitor.getPaidUpNgpsa());
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
        competitor.setIsVerified(true);

        // Act
        competitorMapper.applyFields(competitor, request);

        // Assert
        assertNull(competitor.getPaidUpSapsa());
        assertNull(competitor.getPaidUpClub());
        assertNull(competitor.getIsVerified());
    }

    @Test
    void testApplyFields_whenEmailAddressesIsNull_thenCompetitorEmailAddressesIsEmpty() {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyFields(competitor, request);

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
        competitorMapper.applyFields(competitor, request);

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
        competitorMapper.applyFields(competitor, request);

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
                () -> competitorMapper.applyFields(new Competitor(), request));
    }

    // applyPatchFields()
    @Test
    void testApplyPatchFields_whenRequestHasAllFields_thenCopiesAllFieldsOntoCompetitor() {
        // Arrange
        Club club = hpscClub();
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setMiddleNames("Ann");
        request.setNickName("Janie");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.Female.toString());
        request.setHomeClub("Test Club");
        request.setSapsaNumber(12345);
        request.setCompetitorNumber("7001");
        request.setClubNumber("HPSC-001");
        request.setIdNumber("9001015800083");
        request.setCellphoneNumber("0821234567");
        request.setEmailAddresses(List.of("jane.doe@example.com"));
        request.setPaidUpSapsa(true);
        request.setPaidUpClub(true);
        request.setIsVerified(true);
        Competitor competitor = new Competitor();

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals("Ann", competitor.getMiddleNames());
        assertEquals("Janie", competitor.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), competitor.getDateOfBirth());
        assertEquals(Gender.Female, competitor.getGender());
        assertSame(club, competitor.getHomeClub());
        assertEquals(12345, competitor.getSapsaNumber());
        assertEquals(7001, competitor.getCompetitorNumber());
        assertEquals("HPSC-001", competitor.getClubNumber());
        assertEquals("9001015800083", competitor.getIdNumber());
        assertEquals("0821234567", competitor.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com"), competitor.getEmailAddresses());
        assertEquals(Boolean.TRUE, competitor.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpClub());
        assertEquals(Boolean.TRUE, competitor.getIsVerified());
    }

    @Test
    void testApplyPatchFields_whenRequestIsEmpty_thenLeavesEveryFieldUnchanged() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();

        // Act
        competitorMapper.applyPatchFields(competitor, new CompetitorPatchRequest());

        // Assert
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals("Janie", competitor.getNickName());
        assertEquals(Gender.Female, competitor.getGender());
        assertEquals(7001, competitor.getCompetitorNumber());
        assertEquals("HPSC-001", competitor.getClubNumber());
        assertEquals(List.of("jane.doe@example.com"), competitor.getEmailAddresses());
        assertEquals(Boolean.TRUE, competitor.getPaidUpSapsa());
        assertNotNull(competitor.getHomeClub());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testApplyPatchFields_whenPaidUpNgpsaIsSupplied_thenChangesOnlyThatFlag() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        competitor.setPaidUpNgpsa(false);
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setPaidUpNgpsa(true);

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals(Boolean.TRUE, competitor.getPaidUpNgpsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpSapsa());
        assertEquals(Boolean.TRUE, competitor.getPaidUpClub());
    }

    @Test
    void testApplyPatchFields_whenPaidUpNgpsaIsOmitted_thenLeavesItUnchanged() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        competitor.setPaidUpNgpsa(true);
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setLastName("Smith");

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals(Boolean.TRUE, competitor.getPaidUpNgpsa());
        assertEquals("Smith", competitor.getLastName());
    }

    @Test
    void testApplyPatchFields_whenOnlySomeFieldsAreSupplied_thenChangesOnlyThose() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setLastName("Smith");
        request.setPaidUpSapsa(false);

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals("Smith", competitor.getLastName());
        assertEquals(Boolean.FALSE, competitor.getPaidUpSapsa());
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("HPSC-001", competitor.getClubNumber());
        assertEquals(Boolean.TRUE, competitor.getPaidUpClub());
    }

    @Test
    void testApplyPatchFields_whenEmailAddressesAreSupplied_thenReplacesTheExistingOnes() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setEmailAddresses(List.of("new@example.com"));

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals(List.of("new@example.com"), competitor.getEmailAddresses());
    }

    @Test
    void testApplyPatchFields_whenCompetitorNumberIsBlank_thenKeepsTheExistingNumber() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setCompetitorNumber("  ");

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals(7001, competitor.getCompetitorNumber());
    }

    @Test
    void testApplyPatchFields_whenCompetitorNumberIsNotAWholeNumber_thenThrowsValidationException() {
        // Arrange
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setCompetitorNumber("C-1");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> competitorMapper.applyPatchFields(new Competitor(), request));
    }

    @Test
    void testApplyPatchFields_whenOnlyClubNumberIsSuppliedForAnHpscMember_thenReplacesTheClubNumber() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setClubNumber("HPSC-002");

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertEquals("HPSC-002", competitor.getClubNumber());
    }

    @Test
    void testApplyPatchFields_whenOnlyClubNumberIsSuppliedForANonMember_thenClubNumberStaysNull() {
        // Arrange
        Competitor competitor = new Competitor();
        Club otherClub = new Club();
        otherClub.setIdentifier(ClubIdentifier.SOSC);
        competitor.setHomeClub(otherClub);
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setClubNumber("HPSC-002");

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertNull(competitor.getClubNumber());
    }

    @Test
    void testApplyPatchFields_whenHomeClubChangesToAnotherClub_thenClubNumberIsCleared() {
        // Arrange
        Competitor competitor = existingHpscCompetitor();
        Club otherClub = new Club();
        otherClub.setName("Other Club");
        otherClub.setIdentifier(ClubIdentifier.SOSC);
        when(clubRepository.findByName("Other Club")).thenReturn(Optional.of(otherClub));
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setHomeClub("Other Club");

        // Act
        competitorMapper.applyPatchFields(competitor, request);

        // Assert
        assertSame(otherClub, competitor.getHomeClub());
        assertNull(competitor.getClubNumber());
    }

    @Test
    void testApplyPatchFields_whenHomeClubChangesToHpscWithoutAClubNumber_thenThrowsValidationException() {
        // Arrange
        Competitor competitor = new Competitor();
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(hpscClub()));
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setHomeClub("Test Club");

        // Act & Assert
        assertThrows(ValidationException.class, () -> competitorMapper.applyPatchFields(competitor, request));
    }

    @Test
    void testApplyPatchFields_whenHomeClubIsUnknown_thenThrowsValidationException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation("No Such Club")).thenReturn(Optional.empty());
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setHomeClub("No Such Club");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> competitorMapper.applyPatchFields(new Competitor(), request));
    }

    @Test
    void testApplyPatchFields_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        request.setGender("Not A Gender");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> competitorMapper.applyPatchFields(new Competitor(), request));
    }

    // isMemberOfHomeClub()
    @Test
    void testIsMemberOfHomeClub_whenClubIsNull_thenReturnsFalse() {
        assertFalse(competitorMapper.isMemberOfHomeClub(null));
    }

    @Test
    void testIsMemberOfHomeClub_whenClubHasNoIdentifier_thenReturnsFalse() {
        // Arrange - shouldn't occur via a real persisted Club, whose identifier column is non-null
        Club club = new Club();

        // Act & Assert
        assertFalse(competitorMapper.isMemberOfHomeClub(club));
    }

    @Test
    void testIsMemberOfHomeClub_whenClubIdentifierDoesNotMatch_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);

        // Act & Assert
        assertFalse(competitorMapper.isMemberOfHomeClub(club));
    }

    @Test
    void testIsMemberOfHomeClub_whenClubIdentifierMatches_thenReturnsTrue() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertTrue(competitorMapper.isMemberOfHomeClub(club));
    }

    // isMemberOfHomeClub(Club, ClubIdentifier)
    @Test
    void testIsMemberOfHomeClubWithIdentifier_whenClubHasThatIdentifier_thenReturnsTrue() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);

        // Act & Assert
        assertTrue(competitorMapper.isMemberOfHomeClub(club, ClubIdentifier.SOSC));
    }

    @Test
    void testIsMemberOfHomeClubWithIdentifier_whenClubHasDifferentIdentifier_thenReturnsFalse() {
        // Arrange - the default home club is no longer special once another is passed in
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(competitorMapper.isMemberOfHomeClub(club, ClubIdentifier.SOSC));
    }

    @Test
    void testIsMemberOfHomeClubWithIdentifier_whenIdentifierIsNull_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(competitorMapper.isMemberOfHomeClub(club, null));
    }

    @Test
    void testIsMemberOfHomeClubWithIdentifier_whenClubIsNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(competitorMapper.isMemberOfHomeClub(null, ClubIdentifier.HPSC));
    }

    @Test
    void testIsMemberOfHomeClub_whenNoIdentifierIsGiven_thenDefaultsToTheHomeClubIdentifier() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertTrue(competitorMapper.isMemberOfHomeClub(club));
        assertEquals(ClubIdentifier.HPSC, IpscConstants.HOME_CLUB_IDENTIFIER);
    }

    // parseCompetitorNumber()
    @Test
    void testParseCompetitorNumber_whenNumberHasSurroundingSpaces_thenReturnsTheWholeNumber() {
        assertEquals(123, competitorMapper.parseCompetitorNumber(" 123 "));
    }

    @Test
    void testParseCompetitorNumber_whenNullOrBlank_thenReturnsNull() {
        assertNull(competitorMapper.parseCompetitorNumber(null));
        assertNull(competitorMapper.parseCompetitorNumber("  "));
    }

    @Test
    void testParseCompetitorNumber_whenNotAWholeNumber_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> competitorMapper.parseCompetitorNumber("C-1"));
        assertThrows(ValidationException.class, () -> competitorMapper.parseCompetitorNumber("12.5"));
    }

    // resolveClubNumber()
    @Test
    void testResolveClubNumber_whenHomeClubIsNull_thenReturnsNull() {
        assertNull(competitorMapper.resolveClubNumber(null, "HPSC-001"));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsNotHpsc_thenReturnsNull() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.SOSC);

        // Act & Assert
        assertNull(competitorMapper.resolveClubNumber(club, "HPSC-001"));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsNull_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertThrows(ValidationException.class, () -> competitorMapper.resolveClubNumber(club, null));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsBlank_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertThrows(ValidationException.class, () -> competitorMapper.resolveClubNumber(club, "  "));
    }

    @Test
    void testResolveClubNumber_whenHomeClubIsHpscAndClubNumberIsValid_thenReturnsClubNumber() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);

        // Act & Assert
        assertEquals("HPSC-001", competitorMapper.resolveClubNumber(club, "HPSC-001"));
    }

    // resolveCompetitorNumber()
    @Test
    void testResolveCompetitorNumber_whenBothAreNull_thenReturnsNull() {
        assertNull(competitorMapper.resolveCompetitorNumber(null, null));
    }

    @Test
    void testResolveCompetitorNumber_whenOnlyCompetitorNumberIsSupplied_thenReturnsCompetitorNumber() {
        assertEquals(123, competitorMapper.resolveCompetitorNumber("123", null));
    }

    @Test
    void testResolveCompetitorNumber_whenOnlySapsaNumberIsSupplied_thenReturnsSapsaNumber() {
        assertEquals(4567, competitorMapper.resolveCompetitorNumber(null, 4567));
    }

    @Test
    void testResolveCompetitorNumber_whenBothAreSupplied_thenCompetitorNumberTakesPrecedence() {
        assertEquals(123, competitorMapper.resolveCompetitorNumber("123", 4567));
    }

    @Test
    void testResolveCompetitorNumber_whenCompetitorNumberIsBlank_thenFallsBackToSapsaNumber() {
        assertEquals(4567, competitorMapper.resolveCompetitorNumber("  ", 4567));
    }

    @Test
    void testResolveCompetitorNumber_whenCompetitorNumberIsBlankAndSapsaNumberIsNull_thenReturnsNull() {
        assertNull(competitorMapper.resolveCompetitorNumber("  ", null));
    }

    @Test
    void testResolveCompetitorNumber_whenCompetitorNumberIsNotAWholeNumber_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> competitorMapper.resolveCompetitorNumber("C-1", 4567));
    }

    // resolveGender()
    @Test
    void testResolveGender_whenGenderIsNull_thenReturnsNull() {
        assertNull(competitorMapper.resolveGender(null));
    }

    @Test
    void testResolveGender_whenGenderIsBlank_thenReturnsNull() {
        assertNull(competitorMapper.resolveGender("  "));
    }

    @Test
    void testResolveGender_whenGenderIsUnrecognised_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> competitorMapper.resolveGender("Not A Gender"));
    }

    @Test
    void testResolveGender_whenGenderIsValid_thenReturnsMatchingGender() {
        assertEquals(Gender.Female, competitorMapper.resolveGender(Gender.Female.toString()));
    }

    // resolveHomeClub()
    @Test
    void testResolveHomeClub_whenClubNameIsNull_thenReturnsNull() {
        assertNull(competitorMapper.resolveHomeClub(null));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testResolveHomeClub_whenClubNameIsBlank_thenReturnsNull() {
        assertNull(competitorMapper.resolveHomeClub("  "));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testResolveHomeClub_whenClubNameMatchesNeitherNameNorAbbreviation_thenThrowsValidationException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        when(clubRepository.findByAbbreviation("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> competitorMapper.resolveHomeClub("No Such Club"));
    }

    @Test
    void testResolveHomeClub_whenClubNameMatchesExistingClub_thenReturnsClub() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> competitorMapper.resolveHomeClub("Test Club"));

        // Assert
        assertSame(club, resolved);
    }

    @Test
    void testResolveHomeClub_whenClubNameMatchesOnlyAnAbbreviation_thenReturnsClub() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        when(clubRepository.findByAbbreviation("TC")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> competitorMapper.resolveHomeClub("TC"));

        // Assert
        assertSame(club, resolved);
        verify(clubRepository, never()).findByName("TC");
    }

    // Helpers
    private Club hpscClub() {
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        return club;
    }

    private Competitor existingHpscCompetitor() {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setNickName("Janie");
        competitor.setGender(Gender.Female);
        competitor.setHomeClub(hpscClub());
        competitor.setCompetitorNumber(7001);
        competitor.setClubNumber("HPSC-001");
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));
        competitor.setPaidUpSapsa(true);
        competitor.setPaidUpClub(true);
        return competitor;
    }
}
