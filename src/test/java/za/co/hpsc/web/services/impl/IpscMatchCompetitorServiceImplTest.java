package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.mappers.MatchCompetitorMapper;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.ClubService;
import za.co.hpsc.web.services.EntityIpscCompetitorService;
import za.co.hpsc.web.services.IpscMatchCompetitorService;
import za.co.hpsc.web.services.IpscMatchCompetitorServiceTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscMatchCompetitorServiceImpl}'s impl-only protected helper methods
 * ({@code findMatchCompetitorOrThrow}, {@code isForClub}, {@code resolveCompetitorHomeClub},
 * {@code toFailedResponse}, {@code toResponse}, {@code validateForCreate}) - not declared on
 * {@link IpscMatchCompetitorService}. The field-copying and lookup helpers are covered by
 * {@code za.co.hpsc.web.mappers.MatchCompetitorMapperTest}.
 * The interface's create/update/patch/get/delete contract is covered by
 * {@link IpscMatchCompetitorServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscMatchCompetitorServiceImplTest {

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private EntityIpscCompetitorService entityIpscCompetitorService;

    @Spy
    private final ClubService clubService = new ClubServiceImpl();

    private IpscMatchCompetitorServiceImpl matchCompetitorServiceImpl;

    @BeforeEach
    void setUp() {
        matchCompetitorServiceImpl = new IpscMatchCompetitorServiceImpl(matchCompetitorRepository,
                new MatchCompetitorMapper(competitorRepository, ipscMatchRepository, entityIpscCompetitorService),
                clubService, null);
    }

    // findMatchCompetitorOrThrow()
    @Test
    void testFindMatchCompetitorOrThrow_whenItExists_thenReturnsIt() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor));

        // Act & Assert
        assertSame(matchCompetitor, matchCompetitorServiceImpl.findMatchCompetitorOrThrow(5L));
    }

    @Test
    void testFindMatchCompetitorOrThrow_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorServiceImpl.findMatchCompetitorOrThrow(5L));
    }

    // resolveCompetitorHomeClub()
    @Test
    void testResolveCompetitorHomeClub_whenCompetitorHasHomeClub_thenReturnsItsIdentifier() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        request.setCompetitorName("Jane Doe");
        when(entityIpscCompetitorService.findCompetitor("7001", "Jane Doe"))
                .thenReturn(Optional.of(competitorWithHomeClub(ClubIdentifier.HPSC)));

        // Act & Assert
        assertEquals(ClubIdentifier.HPSC, matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
    }

    @Test
    void testResolveCompetitorHomeClub_whenRequestHasCompetitorId_thenResolvesById() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        when(competitorRepository.findById(1L))
                .thenReturn(Optional.of(competitorWithHomeClub(ClubIdentifier.SOSC)));

        // Act & Assert
        assertEquals(ClubIdentifier.SOSC, matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
        verifyNoInteractions(entityIpscCompetitorService);
    }

    @Test
    void testResolveCompetitorHomeClub_whenCompetitorHasNoHomeClub_thenReturnsNull() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        when(entityIpscCompetitorService.findCompetitor("7001", null))
                .thenReturn(Optional.of(new Competitor()));

        // Act & Assert
        assertNull(matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
    }

    @Test
    void testResolveCompetitorHomeClub_whenHomeClubHasNoIdentifier_thenReturnsNull() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        Competitor competitor = new Competitor();
        competitor.setHomeClub(new Club());
        when(entityIpscCompetitorService.findCompetitor("7001", null)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertNull(matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
    }

    @Test
    void testResolveCompetitorHomeClub_whenNoCompetitorIsFound_thenThrowsNonFatalException() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        when(entityIpscCompetitorService.findCompetitor("7001", null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
    }

    @Test
    void testResolveCompetitorHomeClub_whenCompetitorIsAmbiguous_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        when(entityIpscCompetitorService.findCompetitor("7001", null))
                .thenThrow(new ValidationException("More than one competitor"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveCompetitorHomeClub(request));
    }

    // isForClub()
    @Test
    void testIsForClub_whenTargetClubIsNull_thenReturnsTrue() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("SOSC");

        // Act & Assert
        assertTrue(matchCompetitorServiceImpl.isForClub(request, null));
        assertTrue(matchCompetitorServiceImpl.isForClub(new MatchCompetitorRequest(), null));
    }

    @Test
    void testIsForClub_whenRowClubMatchesByNameOrAbbreviation_thenReturnsTrue() {
        // Arrange
        MatchCompetitorRequest byAbbreviation = new MatchCompetitorRequest();
        byAbbreviation.setMatchClub("HPSC");
        MatchCompetitorRequest byName = new MatchCompetitorRequest();
        byName.setMatchClub(ClubIdentifier.HPSC.getName());

        // Act & Assert
        assertTrue(matchCompetitorServiceImpl.isForClub(byAbbreviation, ClubIdentifier.HPSC));
        assertTrue(matchCompetitorServiceImpl.isForClub(byName, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowClubIsDifferent_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("SOSC");

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowClubIsDifferentButCompetitorHomeClubMatches_thenReturnsTrue() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        request.setCompetitorName("Jane Doe");
        request.setMatchClub("SOSC");
        when(entityIpscCompetitorService.findCompetitor("7001", "Jane Doe"))
                .thenReturn(Optional.of(competitorWithHomeClub(ClubIdentifier.HPSC)));

        // Act & Assert
        assertTrue(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowHasNoClubAndCompetitorHomeClubIsDifferent_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        when(entityIpscCompetitorService.findCompetitor("7001", null))
                .thenReturn(Optional.of(competitorWithHomeClub(ClubIdentifier.SOSC)));

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowHasNoClubAndCompetitorHasNoHomeClub_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        when(entityIpscCompetitorService.findCompetitor("7001", null))
                .thenReturn(Optional.of(new Competitor()));

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowClubIsDifferentAndCompetitorCannotBeResolved_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("SOSC");
        when(entityIpscCompetitorService.findCompetitor(null, null))
                .thenThrow(new ValidationException("Competitor number or name is required"));

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowClubIsDifferentAndNoCompetitorIsFound_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorNumber("7001");
        request.setMatchClub("SOSC");
        when(entityIpscCompetitorService.findCompetitor("7001", null)).thenReturn(Optional.empty());

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowIdentifiesCompetitorByIdThatDoesNotExist_thenReturnsFalse() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertFalse(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenTargetClubIsNullAndRowClubIsUnknown_thenReturnsTrueWithoutInspectingTheRow() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("Not A Club");

        // Act
        boolean result = matchCompetitorServiceImpl.isForClub(request, null);

        // Assert
        assertTrue(result);
        verifyNoInteractions(entityIpscCompetitorService, competitorRepository);
    }

    @Test
    void testIsForClub_whenRowIdentifiesCompetitorById_thenUsesTheirHomeClub() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchClub("SOSC");
        Competitor competitor = competitorWithHomeClub(ClubIdentifier.HPSC);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertTrue(matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    @Test
    void testIsForClub_whenRowClubMatches_thenDoesNotLookUpTheCompetitor() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("HPSC");

        // Act
        boolean result = matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC);

        // Assert
        assertTrue(result);
        verifyNoInteractions(entityIpscCompetitorService);
    }

    @Test
    void testIsForClub_whenRowClubIsUnknown_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setMatchClub("Not A Club");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.isForClub(request, ClubIdentifier.HPSC));
    }

    // validateForCreate()
    @Test
    void testValidateForCreate_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(null));
    }

    @Test
    void testValidateForCreate_whenAllRequiredFieldsPresent_thenDoesNotThrow() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        request.setPowerFactor("Major");

        // Act & Assert
        assertDoesNotThrow(() -> matchCompetitorServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenEachRequiredFieldIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest noCompetitor = completeRequest();
        noCompetitor.setCompetitorId(null);
        MatchCompetitorRequest noMatch = completeRequest();
        noMatch.setMatchId(null);
        MatchCompetitorRequest noCategory = completeRequest();
        noCategory.setCompetitorCategory(null);
        MatchCompetitorRequest noDivision = completeRequest();
        noDivision.setDivision(null);
        MatchCompetitorRequest noPowerFactor = completeRequest();
        noPowerFactor.setPowerFactor(null);
        MatchCompetitorRequest blankPowerFactor = completeRequest();
        blankPowerFactor.setPowerFactor("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noCompetitor));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noMatch));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noCategory));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noDivision));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noPowerFactor));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(blankPowerFactor));
    }

    // toFailedResponse()
    @Test
    void testToFailedResponse_whenRequestPresent_thenCopiesOnlyTheIdentifyingFields() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setCompetitorName("Jane Doe");
        request.setCompetitorNumber(" 123 ");
        request.setMatchId(2L);
        request.setFirearmType("Not a firearm type");
        request.setPoints(new BigDecimal("10"));

        // Act
        MatchCompetitorResponse response = matchCompetitorServiceImpl.toFailedResponse(request);

        // Assert
        assertEquals(1L, response.getCompetitorId());
        assertEquals(List.of("Jane Doe"), response.getCompetitorNames());
        assertEquals(123, response.getCompetitorNumber());
        assertEquals(2L, response.getMatchId());
        assertNull(response.getMatchCompetitorId());
        assertNull(response.getFirearmType());
        assertNull(response.getPoints());
    }

    @Test
    void testToFailedResponse_whenRequestIsNull_thenReturnsEmptyResponse() {
        // Act
        MatchCompetitorResponse response = matchCompetitorServiceImpl.toFailedResponse(null);

        // Assert
        assertNotNull(response);
        assertNull(response.getCompetitorId());
        assertNull(response.getMatchId());
        assertTrue(response.getCompetitorNames().isEmpty());
        assertNull(response.getCompetitorNumber());
    }

    // toResponse()
    @Test
    void testToResponse_whenAllFieldsPresent_thenMapsAllFields() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setNickName("JD");
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(5L);
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setMatchClub(ClubIdentifier.HPSC);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.RIFLE);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPoints(new BigDecimal("10"));
        matchCompetitor.setPercentage(new BigDecimal("98.25"));
        matchCompetitor.setTime(new BigDecimal("41.5"));
        matchCompetitor.setPercentageOfPossiblePoints(new BigDecimal("93.75"));
        matchCompetitor.setAlpha(30);
        matchCompetitor.setCharlie(4);
        matchCompetitor.setDelta(1);
        matchCompetitor.setMisses(2);
        matchCompetitor.setNoPenaltyMisses(1);
        matchCompetitor.setNoShoots(0);
        matchCompetitor.setProceduralErrors(3);
        matchCompetitor.setAdditionalPenalties(5);
        matchCompetitor.setOverallRanking(new BigDecimal("2"));
        matchCompetitor.setClubRanking(new BigDecimal("1"));
        matchCompetitor.setIsVisitor(false);

        // Act
        MatchCompetitorResponse response = matchCompetitorServiceImpl.toResponse(matchCompetitor);

        // Assert
        assertEquals(5L, response.getMatchCompetitorId());
        assertEquals(1L, response.getCompetitorId());
        assertEquals(2L, response.getMatchId());
        assertEquals(List.of("Jane Doe", "JD Doe"), response.getCompetitorNames());
        assertEquals(ClubIdentifier.HPSC, response.getMatchClub());
        assertEquals(CompetitorCategory.JUNIOR, response.getCompetitorCategory());
        assertEquals(FirearmType.RIFLE, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(PowerFactor.MAJOR, response.getPowerFactor());
        assertEquals(new BigDecimal("10"), response.getPoints());
        assertEquals(new BigDecimal("98.25"), response.getPercentage());
        assertEquals(new BigDecimal("41.5"), response.getTime());
        assertEquals(new BigDecimal("93.75"), response.getPercentageOfPossiblePoints());
        assertEquals(30, response.getAlpha());
        assertEquals(4, response.getCharlie());
        assertEquals(1, response.getDelta());
        assertEquals(2, response.getMisses());
        assertEquals(1, response.getNoPenaltyMisses());
        assertEquals(0, response.getNoShoots());
        assertEquals(3, response.getProceduralErrors());
        assertEquals(5, response.getAdditionalPenalties());
        assertEquals(new BigDecimal("2"), response.getOverallRanking());
        assertEquals(new BigDecimal("1"), response.getClubRanking());
        assertEquals(Boolean.FALSE, response.getIsVisitor());
    }

    @Test
    void testToResponse_whenNickNameMatchesFirstName_thenCompetitorNamesAreUnique() {
        // Arrange
        MatchCompetitor matchCompetitor = matchCompetitorFor("Jane", "Doe", "Jane");

        // Act
        MatchCompetitorResponse response = matchCompetitorServiceImpl.toResponse(matchCompetitor);

        // Assert
        assertEquals(List.of("Jane Doe"), response.getCompetitorNames());
    }

    @Test
    void testToResponse_whenNickNameIsNull_thenCompetitorNamesHoldOnlyTheFirstAndLastName() {
        // Arrange
        MatchCompetitor matchCompetitor = matchCompetitorFor("Jane", "Doe", null);

        // Act
        MatchCompetitorResponse response = matchCompetitorServiceImpl.toResponse(matchCompetitor);

        // Assert
        assertEquals(List.of("Jane Doe"), response.getCompetitorNames());
    }

    // Helpers
    private MatchCompetitor matchCompetitorFor(String firstName, String lastName, String nickName) {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setNickName(nickName);
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        return matchCompetitor;
    }

    private MatchCompetitorRequest completeRequest() {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        request.setPowerFactor("Major");
        return request;
    }

    private Competitor competitorWithHomeClub(ClubIdentifier identifier) {
        Club club = new Club();
        club.setIdentifier(identifier);
        Competitor competitor = new Competitor();
        competitor.setHomeClub(club);
        return competitor;
    }
}
