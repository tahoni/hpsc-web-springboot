package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import za.co.hpsc.web.services.IpscMatchCompetitorService;
import za.co.hpsc.web.services.IpscMatchCompetitorServiceTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        request.setCompetitorCategory(List.of("Lady"));
        request.setFirearmType("PCC");
        request.setDivision("Production Division");
        request.setPowerFactor("Minor");
        request.setPoints(new BigDecimal("50"));
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
        assertEquals(List.of(CompetitorCategory.LADY), matchCompetitor.getCompetitorCategories());
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MINOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("50"), matchCompetitor.getPoints());
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

    // findCompetitorOrThrow()
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

    // resolveCompetitorCategories()
    @Test
    void testResolveCompetitorCategories_whenSeveralKnown_thenReturnsThemInOrderWithoutDuplicates() {
        // Act
        List<CompetitorCategory> result = matchCompetitorServiceImpl.resolveCompetitorCategories(
                List.of("Lady", "Junior", "Lady"));

        // Assert
        assertEquals(List.of(CompetitorCategory.LADY, CompetitorCategory.JUNIOR), result);
    }

    @Test
    void testResolveCompetitorCategories_whenEmptyOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveCompetitorCategories(List.of()));
        assertThrows(ValidationException.class, () -> matchCompetitorServiceImpl.resolveCompetitorCategories(null));
    }

    @Test
    void testResolveCompetitorCategories_whenAnyUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorServiceImpl.resolveCompetitorCategories(List.of("Lady", "Nope")));
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
        request.setCompetitorCategory(List.of("Junior"));
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
        matchCompetitor.setCompetitorCategories(List.of(CompetitorCategory.JUNIOR));
        matchCompetitor.setFirearmType(FirearmType.RIFLE);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPoints(new BigDecimal("10"));
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
        assertEquals(List.of(CompetitorCategory.JUNIOR), response.getCompetitorCategory());
        assertEquals(FirearmType.RIFLE, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(PowerFactor.MAJOR, response.getPowerFactor());
        assertEquals(new BigDecimal("10"), response.getPoints());
        assertEquals(new BigDecimal("2"), response.getOverallRanking());
        assertEquals(new BigDecimal("1"), response.getClubRanking());
        assertEquals(Boolean.FALSE, response.getIsVisitor());
    }

    // Helpers
    private MatchCompetitorRequest completeRequest() {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory(List.of("Junior"));
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        return request;
    }
}
