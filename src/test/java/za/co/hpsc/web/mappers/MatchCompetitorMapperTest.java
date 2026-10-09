package za.co.hpsc.web.mappers;

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
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.IpscEntityCompetitorService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MatchCompetitorMapper}, with its repositories and {@link IpscEntityCompetitorService}
 * mocked.
 */
@ExtendWith(MockitoExtension.class)
class MatchCompetitorMapperTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscEntityCompetitorService ipscEntityCompetitorService;

    @InjectMocks
    private MatchCompetitorMapper matchCompetitorMapper;

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
        request.setDivision("PCC Irons");
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
        matchCompetitorMapper.applyFields(matchCompetitor, request);

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
        assertSame(match, matchCompetitor.getMatch());
        assertEquals(ClubIdentifier.HPSC, matchCompetitor.getMatchClub());
        assertEquals(CompetitorCategory.LADY, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
        assertEquals(Division.PCC_IRON, matchCompetitor.getDivision());
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

    // applyPatchFields()
    @Test
    void testApplyPatchFields_whenRequestHasAllFields_thenCopiesAndResolvesThemOntoEntity() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setMatchClub("HPSC");
        request.setCompetitorCategory("Lady");
        request.setFirearmType("PCC");
        request.setDivision("PCC Irons");
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
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
        assertSame(match, matchCompetitor.getMatch());
        assertEquals(ClubIdentifier.HPSC, matchCompetitor.getMatchClub());
        assertEquals(CompetitorCategory.LADY, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
        assertEquals(Division.PCC_IRON, matchCompetitor.getDivision());
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

    @Test
    void testApplyPatchFields_whenRequestIsEmpty_thenLeavesEveryFieldUnchanged() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        Competitor competitor = matchCompetitor.getCompetitor();

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, new MatchCompetitorPatchRequest());

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
        assertEquals(ClubIdentifier.SOSC, matchCompetitor.getMatchClub());
        assertEquals(CompetitorCategory.LADY, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MAJOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("50"), matchCompetitor.getPoints());
        assertEquals(7, matchCompetitor.getAlpha());
        assertEquals(Boolean.FALSE, matchCompetitor.getIsVisitor());
        verifyNoInteractions(competitorRepository, ipscMatchRepository, ipscEntityCompetitorService);
    }

    @Test
    void testApplyPatchFields_whenOnlySomeFieldsAreSupplied_thenChangesOnlyThose() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Open");
        request.setIsVisitor(true);

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(Division.OPEN, matchCompetitor.getDivision());
        assertEquals(Boolean.TRUE, matchCompetitor.getIsVisitor());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
        assertEquals(PowerFactor.MAJOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("50"), matchCompetitor.getPoints());
    }

    @Test
    void testApplyPatchFields_whenCompetitorNameHasPositionRoMarkerAndFullStops_thenLooksUpTheCleanedName() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        Competitor competitor = new Competitor();
        competitor.setId(3L);
        when(ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(null, "J Smith")).thenReturn(Optional.of(competitor));
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setCompetitorName("1 - J.  Smith (RO)");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
    }

    @Test
    void testApplyPatchFields_whenEnumeratedValuesAreBlank_thenLeavesThemUnchanged() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setCompetitorCategory("  ");
        request.setFirearmType("  ");
        request.setDivision("  ");
        request.setPowerFactor("  ");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(CompetitorCategory.LADY, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MAJOR, matchCompetitor.getPowerFactor());
    }

    @Test
    void testApplyPatchFields_whenCompetitorNumberAndNameAreSupplied_thenResolvesThemThroughTheEntityService() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        Competitor competitor = new Competitor();
        competitor.setId(9L);
        when(ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("123", "Jane Doe")).thenReturn(Optional.of(competitor));
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setCompetitorNumber("123");
        request.setCompetitorName("Jane Doe");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertSame(competitor, matchCompetitor.getCompetitor());
    }

    @Test
    void testApplyPatchFields_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.empty());
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setMatchId(2L);

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> matchCompetitorMapper.applyPatchFields(new MatchCompetitor(), request));
    }

    @Test
    void testApplyPatchFields_whenDivisionIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Not A Division");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorMapper.applyPatchFields(new MatchCompetitor(), request));
    }

    @Test
    void testApplyPatchFields_whenDivisionDoesNotMatchExistingFirearmType_thenThrowsValidationException() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Shotgun Open");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.applyPatchFields(matchCompetitor, request));
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeDoesNotMatchExistingDivision_thenThrowsValidationException() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Shotgun");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.applyPatchFields(matchCompetitor, request));
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeAndDivisionAreChangedTogether_thenAcceptsThem() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Shotgun");
        request.setDivision("Shotgun Open");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.SHOTGUN_OPEN, matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsNullAndDivisionIsPatched_thenInitialisesFirearmTypeFromDivision() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Shotgun Open");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(Division.SHOTGUN_OPEN, matchCompetitor.getDivision());
        assertEquals(FirearmType.SHOTGUN, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyPatchFields_whenEntityHasNeitherFirearmTypeNorDivisionAndNeitherIsPatched_thenLeavesThemNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setPoints(new BigDecimal("50"));

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(new BigDecimal("50"), matchCompetitor.getPoints());
        assertNull(matchCompetitor.getFirearmType());
        assertNull(matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenEntityHasDivisionButNoFirearmTypeAndNeitherIsPatched_thenLeavesFirearmTypeNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setDivision(Division.PRODUCTION);
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setPoints(new BigDecimal("50"));

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertNull(matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenEntityHasNoFirearmTypeAndDivisionIsBlank_thenLeavesBothUnchanged() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("  ");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertNull(matchCompetitor.getFirearmType());
        assertNull(matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsBlankAndDivisionIsPatchedOnEntityWithoutFirearmType_thenTakesItFromDivision() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setDivision(Division.PRODUCTION);
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("  ");
        request.setDivision("Rifle Open");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(Division.RIFLE_SEMI_AUTO_OPEN, matchCompetitor.getDivision());
        assertEquals(FirearmType.RIFLE, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyPatchFields_whenEntityHasFirearmTypeAndDivisionIsPatchedToSameFirearmType_thenKeepsFirearmType() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Classic");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(Division.CLASSIC, matchCompetitor.getDivision());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyFields_whenFirearmTypeIsBlank_thenInitialisesFirearmTypeFromDivision() {
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
        request.setCompetitorCategory("Lady");
        request.setFirearmType("  ");
        request.setDivision("Rifle Open");
        request.setPowerFactor("Minor");
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitorMapper.applyFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.RIFLE, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyFields_whenFirearmTypeIsNull_thenInitialisesFirearmTypeFromDivision() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = requestWith(null, "Shotgun Open");
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitorMapper.applyFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.SHOTGUN_OPEN, matchCompetitor.getDivision());
    }

    @Test
    void testApplyFields_whenFirearmTypeIsUnrecognised_thenInitialisesFirearmTypeFromDivision() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = requestWith("Nope", "PCC Irons");
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitorMapper.applyFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyFields_whenFirearmTypeAndDivisionMatch_thenSetsBoth() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = requestWith("Rifle", "Rifle Open");
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitorMapper.applyFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.RIFLE, matchCompetitor.getFirearmType());
        assertEquals(Division.RIFLE_SEMI_AUTO_OPEN, matchCompetitor.getDivision());
    }

    @Test
    void testApplyFields_whenDivisionDoesNotBelongToFirearmType_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = requestWith("Shotgun", "Rifle Open");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorMapper.applyFields(new MatchCompetitor(), request));
    }

    @Test
    void testApplyFields_whenDivisionIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = requestWith("Rifle", "Not A Division");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorMapper.applyFields(new MatchCompetitor(), request));
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsUnrecognised_thenFallsBackToExistingDivisionFirearmType() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Nope");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsUnrecognisedAndThereIsNoDivision_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Nope");

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> matchCompetitorMapper.applyPatchFields(new MatchCompetitor(), request));
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsUnrecognisedAndDivisionIsPatched_thenUsesPatchedDivisionFirearmType() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Nope");
        request.setDivision("Shotgun Open");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.SHOTGUN_OPEN, matchCompetitor.getDivision());
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeMatchesExistingDivision_thenAcceptsIt() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Handgun");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyPatchFields_whenDivisionBelongsToExistingFirearmType_thenAcceptsIt() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setDivision("Open");

        // Act
        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        // Assert
        assertEquals(Division.OPEN, matchCompetitor.getDivision());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeAndDivisionDoNotMatch_thenThrowsValidationException() {
        // Arrange
        MatchCompetitor matchCompetitor = existingMatchCompetitor();
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        request.setFirearmType("Rifle");
        request.setDivision("Shotgun Open");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.applyPatchFields(matchCompetitor, request));
    }

    // validateDivisionMatchesFirearmType()
    @Test
    void testValidateDivisionMatchesFirearmType_whenTheyMatch_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> matchCompetitorMapper
                .validateDivisionMatchesFirearmType(Division.SHOTGUN_OPEN, FirearmType.SHOTGUN));
    }

    @Test
    void testValidateDivisionMatchesFirearmType_whenTheyDiffer_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper
                .validateDivisionMatchesFirearmType(Division.OPEN, FirearmType.SHOTGUN));
    }

    @Test
    void testValidateDivisionMatchesFirearmType_whenEitherIsNull_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> matchCompetitorMapper.validateDivisionMatchesFirearmType(null, FirearmType.RIFLE));
        assertDoesNotThrow(() -> matchCompetitorMapper.validateDivisionMatchesFirearmType(Division.OPEN, null));
    }

    // findCompetitorOrThrow(Long)
    @Test
    void testFindCompetitorOrThrow_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorMapper.findCompetitorOrThrow(1L));
    }

    // findMatchOrThrow()
    @Test
    void testFindMatchOrThrow_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorMapper.findMatchOrThrow(2L));
    }

    // resolveCompetitor()
    @Test
    void testResolveCompetitor_whenIdGiven_thenUsesIdAndIgnoresNumberAndName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorMapper.resolveCompetitor(1L, "1", "Someone Else"));
        verifyNoInteractions(ipscEntityCompetitorService);
    }

    @Test
    void testResolveCompetitor_whenNoIdGiven_thenDelegatesTheNumberAndNameToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(4L);
        when(ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("123", "Jane Doe")).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorMapper.resolveCompetitor(null, "123", "Jane Doe"));
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    void testResolveCompetitor_whenOnlyNameGiven_thenDelegatesTheNameToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(3L);
        when(ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(null, "Jane Doe")).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorMapper.resolveCompetitor(null, null, "Jane Doe"));
    }

    @Test
    void testResolveCompetitor_whenOnlyNumberGiven_thenDelegatesTheNumberToTheEntityService() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(5L);
        when(ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("123", null)).thenReturn(Optional.of(competitor));

        // Act & Assert
        assertSame(competitor, matchCompetitorMapper.resolveCompetitor(null, "123", null));
    }

    // resolveCompetitorCategory()
    @Test
    void testResolveCompetitorCategory_whenKnown_thenReturnsCategory() {
        // Act & Assert
        assertEquals(CompetitorCategory.SUPER_SENIOR, matchCompetitorMapper.resolveCompetitorCategory("Super Senior"));
    }

    @Test
    void testResolveCompetitorCategory_whenUnknownOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveCompetitorCategory("Nope"));
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveCompetitorCategory(null));
    }

    // resolveDivision()
    @Test
    void testResolveDivision_whenKnown_thenReturnsDivision() {
        // Act & Assert
        assertEquals(Division.CLASSIC, matchCompetitorMapper.resolveDivision("Classic"));
    }

    @Test
    void testResolveDivision_whenUnknownOrNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveDivision("Nope"));
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveDivision(null));
    }

    // resolveFirearmType()
    @Test
    void testResolveFirearmType_whenKnownAndDivisionIsNull_thenReturnsFirearmType() {
        // Act & Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitorMapper.resolveFirearmType("Shotgun", null));
    }

    @Test
    void testResolveFirearmType_whenUnknownOrNullAndDivisionIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveFirearmType("Nope", null));
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveFirearmType(null, null));
    }

    @Test
    void testResolveFirearmType_whenKnownAndDivisionIsGiven_thenReturnsFirearmTypeByName() {
        // Act & Assert
        assertEquals(FirearmType.SHOTGUN, matchCompetitorMapper.resolveFirearmType("Shotgun", Division.OPEN));
    }

    @Test
    void testResolveFirearmType_whenUnknownOrNullAndDivisionIsGiven_thenReturnsFirearmTypeOfDivision() {
        // Act & Assert
        assertEquals(FirearmType.RIFLE, matchCompetitorMapper.resolveFirearmType("Nope", Division.RIFLE_STANDARD_MANUAL));
        assertEquals(FirearmType.PCC, matchCompetitorMapper.resolveFirearmType(null, Division.PCC_IRON));
    }

    // resolveMatchClub()
    @Test
    void testResolveMatchClub_whenNullOrBlank_thenReturnsNull() {
        // Act & Assert
        assertNull(matchCompetitorMapper.resolveMatchClub(null));
        assertNull(matchCompetitorMapper.resolveMatchClub("  "));
    }

    @Test
    void testResolveMatchClub_whenAbbreviationMatches_thenReturnsClub() {
        // Act & Assert
        assertEquals(ClubIdentifier.HPSC, matchCompetitorMapper.resolveMatchClub("HPSC"));
    }

    @Test
    void testResolveMatchClub_whenCodeMatches_thenReturnsClub() {
        // Act & Assert
        assertEquals(ClubIdentifier.HPSC, matchCompetitorMapper.resolveMatchClub("B HPSC"));
    }

    @Test
    void testResolveMatchClub_whenNameMatches_thenReturnsClub() {
        // Act & Assert
        assertEquals(ClubIdentifier.HPSC, matchCompetitorMapper.resolveMatchClub("Hartbeespoortdam Practical Shooting Club"));
    }

    @Test
    void testResolveMatchClub_whenUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolveMatchClub("Nope"));
    }

    // resolvePowerFactor()
    @Test
    void testResolvePowerFactor_whenNullOrBlank_thenReturnsNull() {
        // Act & Assert
        assertNull(matchCompetitorMapper.resolvePowerFactor(null));
        assertNull(matchCompetitorMapper.resolvePowerFactor("  "));
    }

    @Test
    void testResolvePowerFactor_whenKnown_thenReturnsPowerFactor() {
        // Act & Assert
        assertEquals(PowerFactor.MAJOR, matchCompetitorMapper.resolvePowerFactor("Major"));
    }

    @Test
    void testResolvePowerFactor_whenUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorMapper.resolvePowerFactor("Nope"));
    }

    // Helpers
    private void stubCompetitorAndMatch() {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        IpscMatch match = new IpscMatch();
        match.setId(2L);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match));
    }

    private MatchCompetitorRequest requestWith(String firearmType, String division) {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory("Lady");
        request.setFirearmType(firearmType);
        request.setDivision(division);
        request.setPowerFactor("Minor");
        return request;
    }

    private MatchCompetitor existingMatchCompetitor() {
        Competitor competitor = new Competitor();
        competitor.setId(1L);
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatchClub(ClubIdentifier.SOSC);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.LADY);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.PRODUCTION);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.setPoints(new BigDecimal("50"));
        matchCompetitor.setAlpha(7);
        matchCompetitor.setIsVisitor(false);
        return matchCompetitor;
    }
}
