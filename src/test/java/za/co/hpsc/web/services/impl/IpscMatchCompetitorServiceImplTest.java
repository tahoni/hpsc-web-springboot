package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscMatchCompetitorServiceImpl}'s impl-only protected helper methods
 * ({@code applyFields}, {@code findCompetitorOrThrow}, {@code findMatchCompetitorOrThrow},
 * {@code findMatchOrThrow}, {@code resolveMatchClub}, {@code resolveCompetitorCategory},
 * {@code resolveFirearmType}, {@code resolveDivision}, {@code resolvePowerFactor}, {@code toResponse},
 * {@code validateForCreate}) - not declared on {@link IpscMatchCompetitorService}.
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
    private ClubService clubService = new ClubServiceImpl();

    @InjectMocks
    private IpscMatchCompetitorServiceImpl matchCompetitorServiceImpl;

    // applyFields()
    @Test
    void testApplyFields_whenAllFieldsPresent_thenCopiesAndResolvesThemOntoEntity() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setMatchClub("HPSC");
        request.setCompetitorCategory("Lady");
        request.setFirearmType("PCC");
        request.setDivision("Production Division");
        request.setPowerFactor("Minor");
        request.setPoints(new BigDecimal("50"));
        request.setPercentage(new BigDecimal("98.25"));
        request.setTime(new BigDecimal("41.5"));
        request.setPercentageOfPossiblePoints(new BigDecimal("93.75"));
        request.setAlpha(30);
        request.setCharlie(4);
        request.setDelta(1);
        request.setMisses(2);
        request.setNoPenaltyMisses(1);
        request.setNoShoots(0);
        request.setProceduralErrors(3);
        request.setAdditionalPenalties(5);
        request.setOverallRanking(new BigDecimal("3"));
        request.setClubRanking(new BigDecimal("2"));
        request.setIsVisitor(true);
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitorServiceImpl.applyFields(matchCompetitor, request);

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
        assertSame(match, matchCompetitor.getMatch());
        assertEquals(ClubIdentifier.HPSC, matchCompetitor.getMatchClub());
        assertEquals(CompetitorCategory.LADY, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MINOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("50"), matchCompetitor.getPoints());
        assertEquals(new BigDecimal("98.25"), matchCompetitor.getPercentage());
        assertEquals(new BigDecimal("41.5"), matchCompetitor.getTime());
        assertEquals(new BigDecimal("93.75"), matchCompetitor.getPercentageOfPossiblePoints());
        assertEquals(30, matchCompetitor.getAlpha());
        assertEquals(4, matchCompetitor.getCharlie());
        assertEquals(1, matchCompetitor.getDelta());
        assertEquals(2, matchCompetitor.getMisses());
        assertEquals(1, matchCompetitor.getNoPenaltyMisses());
        assertEquals(0, matchCompetitor.getNoShoots());
        assertEquals(3, matchCompetitor.getProceduralErrors());
        assertEquals(5, matchCompetitor.getAdditionalPenalties());
        assertEquals(new BigDecimal("3"), matchCompetitor.getOverallRanking());
        assertEquals(new BigDecimal("2"), matchCompetitor.getClubRanking());
        assertEquals(Boolean.TRUE, matchCompetitor.getIsVisitor());
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

    // resolveCompetitor()
    @Test
    void testResolveCompetitor_whenIdGiven_thenUsesIdAndIgnoresNumberAndName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorServiceImpl.resolveCompetitor(1L, "1", "Someone Else"));
        verifyNoInteractions(entityIpscCompetitorService);
    }

    @Test
    void testResolveCompetitor_whenNoIdGiven_thenDelegatesTheNumberAndNameToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(4L);
        when(entityIpscCompetitorService.findCompetitor("123", "Jane Doe")).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorServiceImpl.resolveCompetitor(null, "123", "Jane Doe"));
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    void testResolveCompetitor_whenOnlyNameGiven_thenDelegatesTheNameToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(3L);
        when(entityIpscCompetitorService.findCompetitor(null, "Jane Doe")).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorServiceImpl.resolveCompetitor(null, null, "Jane Doe"));
    }

    @Test
    void testResolveCompetitor_whenOnlyNumberGiven_thenDelegatesTheNumberToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(5L);
        when(entityIpscCompetitorService.findCompetitor("123", null)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorServiceImpl.resolveCompetitor(null, "123", null));
    }

    @Test
    void testResolveCompetitor_whenTheEntityServiceFindsNoCompetitor_thenThrowsNonFatalException() {
        // Arrange
        when(entityIpscCompetitorService.findCompetitor("123", "Jane Doe"))
                .thenThrow(new NonFatalException("No competitors found"));

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> matchCompetitorServiceImpl.resolveCompetitor(null, "123", "Jane Doe"));
    }

    @Test
    void testResolveCompetitor_whenTheEntityServiceFindsSeveralCompetitors_thenThrowsValidationException() {
        // Arrange
        when(entityIpscCompetitorService.findCompetitor("123", "Jane Doe"))
                .thenThrow(new ValidationException("Two or more competitors found"));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorServiceImpl.resolveCompetitor(null, "123", "Jane Doe"));
    }

    @Test
    void testResolveCompetitor_whenTheEntityServiceRejectsBlankInput_thenThrowsValidationException() {
        // Arrange
        when(entityIpscCompetitorService.findCompetitor("", "  "))
                .thenThrow(new ValidationException("Full name or competitor number is required"));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorServiceImpl.resolveCompetitor(null, "  ", "  "));
    }

    // findCompetitorOrThrow(Long)
    @Test
    void testFindCompetitorOrThrow_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorServiceImpl.findCompetitorOrThrow(1L));
    }

    // findMatchOrThrow()
    @Test
    void testFindMatchOrThrow_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorServiceImpl.findMatchOrThrow(2L));
    }

    // resolveMatchClub()
    @Test
    void testResolveMatchClub_whenNullOrBlank_thenReturnsNull() {
        // Act & Assert
        assertNull(matchCompetitorServiceImpl.resolveMatchClub(null));
        assertNull(matchCompetitorServiceImpl.resolveMatchClub("  "));
    }

    @Test
    void testResolveMatchClub_whenAbbreviationMatches_thenReturnsClub() {
        // Act & Assert
        assertEquals(ClubIdentifier.HPSC, matchCompetitorServiceImpl.resolveMatchClub("HPSC"));
    }

    @Test
    void testResolveMatchClub_whenNameMatches_thenReturnsClub() {
        // Act & Assert
        assertEquals(ClubIdentifier.HPSC,
                matchCompetitorServiceImpl.resolveMatchClub("Hartbeespoortdam Practical Shooting Club"));
    }

    @Test
    void testResolveMatchClub_whenUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveMatchClub("Nope"));
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

    // resolveCompetitorCategory()
    @Test
    void testResolveCompetitorCategory_whenKnown_thenReturnsCategory() {
        // Act & Assert
        assertEquals(CompetitorCategory.SUPER_SENIOR, matchCompetitorServiceImpl.resolveCompetitorCategory("Super Senior"));
    }

    @Test
    void testResolveCompetitorCategory_whenUnknownOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveCompetitorCategory("Nope"));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveCompetitorCategory(null));
    }

    // resolveFirearmType()
    @Test
    void testResolveFirearmType_whenKnown_thenReturnsFirearmType() {
        // Act & Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitorServiceImpl.resolveFirearmType("Shotgun"));
    }

    @Test
    void testResolveFirearmType_whenUnknownOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveFirearmType("Nope"));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveFirearmType(null));
    }

    // resolveDivision()
    @Test
    void testResolveDivision_whenKnown_thenReturnsDivision() {
        // Act & Assert
        assertEquals(Division.CLASSIC, matchCompetitorServiceImpl.resolveDivision("Classic Division"));
    }

    @Test
    void testResolveDivision_whenUnknownOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveDivision("Nope"));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveDivision(null));
    }

    // resolvePowerFactor()
    @Test
    void testResolvePowerFactor_whenNullOrBlank_thenReturnsNull() {
        // Act & Assert
        assertNull(matchCompetitorServiceImpl.resolvePowerFactor(null));
        assertNull(matchCompetitorServiceImpl.resolvePowerFactor("  "));
    }

    @Test
    void testResolvePowerFactor_whenKnown_thenReturnsPowerFactor() {
        // Act & Assert
        assertEquals(PowerFactor.MAJOR, matchCompetitorServiceImpl.resolvePowerFactor("Major"));
    }

    @Test
    void testResolvePowerFactor_whenUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolvePowerFactor("Nope"));
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
        MatchCompetitorRequest noFirearm = completeRequest();
        noFirearm.setFirearmType(null);
        MatchCompetitorRequest noDivision = completeRequest();
        noDivision.setDivision(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noCompetitor));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noMatch));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noCategory));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noFirearm));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.validateForCreate(noDivision));
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
        assertEquals("Jane Doe", response.getCompetitorName());
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
        assertNull(response.getCompetitorName());
        assertNull(response.getCompetitorNumber());
    }

    // toResponse()
    @Test
    void testToResponse_whenAllFieldsPresent_thenMapsAllFields() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
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

    // Helpers
    private MatchCompetitorRequest completeRequest() {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
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
