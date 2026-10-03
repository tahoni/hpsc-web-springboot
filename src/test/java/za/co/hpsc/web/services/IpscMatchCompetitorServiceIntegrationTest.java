package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponseHolder;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscMatchCompetitorService} - exercised through the
 * interface type, with a real Spring-wired {@code MatchCompetitorServiceImpl} bean backed by the
 * H2 {@code test} profile database.
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscMatchCompetitorServiceIntegrationTest {

    @Autowired
    private IpscMatchCompetitorService ipscMatchCompetitorService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    // createMatchCompetitor()
    @Test
    void testCreateMatchCompetitor_whenRequestIsValid_thenPersistsAndReturnsIt() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-001");
        IpscMatch match = createMatch();

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.createMatchCompetitor(
                validRequest(competitor.getId(), match.getId()));

        // Assert
        assertNotNull(response.getMatchCompetitorId());
        assertEquals(competitor.getId(), response.getCompetitorId());
        assertEquals(match.getId(), response.getMatchId());
        assertEquals(List.of(CompetitorCategory.JUNIOR), response.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
        assertTrue(matchCompetitorRepository.existsById(response.getMatchCompetitorId()));
    }

    @Test
    void testCreateMatchCompetitor_whenSeveralCategories_thenPersistsAllWithoutDuplicates() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-002");
        IpscMatch match = createMatch();
        MatchCompetitorRequest request = validRequest(competitor.getId(), match.getId());
        request.setCompetitorCategory(List.of("Junior", "Lady", "Junior"));

        // Act
        MatchCompetitorResponse created = ipscMatchCompetitorService.createMatchCompetitor(request);
        MatchCompetitorResponse fetched = ipscMatchCompetitorService.getMatchCompetitor(created.getMatchCompetitorId());

        // Assert
        assertEquals(List.of(CompetitorCategory.JUNIOR, CompetitorCategory.LADY), fetched.getCompetitorCategory());
    }

    @Test
    void testCreateMatchCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        IpscMatch match = createMatch();

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest(999_999L, match.getId())));
    }

    @Test
    void testCreateMatchCompetitor_whenEntryAlreadyExists_thenThrowsValidationException() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-002");
        IpscMatch match = createMatch();
        ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId())));
    }

    @Test
    void testCreateMatchCompetitor_whenSameCompetitorAndMatchButOtherFirearmType_thenCreatesIt() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-003");
        IpscMatch match = createMatch();
        ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()));
        MatchCompetitorRequest rifle = validRequest(competitor.getId(), match.getId());
        rifle.setFirearmType("Rifle");

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.createMatchCompetitor(rifle);

        // Assert
        assertEquals(FirearmType.RIFLE, response.getFirearmType());
    }

    // createMatchCompetitors()
    @Test
    void testCreateMatchCompetitors_whenRowsAreValid_thenPersistsEachInOrder() throws Exception {
        // Arrange
        Competitor first = createCompetitor("HPSC-MC-010");
        Competitor second = createCompetitor("HPSC-MC-011");
        IpscMatch match = createMatch();
        String csvData = String.format("""
                CompetitorId,MatchId,CompetitorCategory,FirearmType,Division
                %d,%d,Junior;Lady,Handgun,Open Division
                %d,%d,Senior,Handgun,Production Division
                """, first.getId(), match.getId(), second.getId(), match.getId());

        // Act
        MatchCompetitorResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(csvData);

        // Assert
        assertEquals(2, holder.getMatchCompetitors().size());
        assertEquals(first.getId(), holder.getMatchCompetitors().get(0).getCompetitorId());
        assertEquals(second.getId(), holder.getMatchCompetitors().get(1).getCompetitorId());
        assertEquals(List.of(CompetitorCategory.JUNIOR, CompetitorCategory.LADY),
                holder.getMatchCompetitors().get(0).getCompetitorCategory());
        assertEquals(2, matchCompetitorRepository.count());
    }

    @Test
    void testCreateMatchCompetitors_whenALaterRowIsInvalid_thenPersistsNone() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-012");
        IpscMatch match = createMatch();
        String csvData = String.format("""
                CompetitorId,MatchId,CompetitorCategory,FirearmType,Division
                %d,%d,Junior,Handgun,Open Division
                %d,%d,Junior,Handgun,Open Division
                """, competitor.getId(), match.getId(), competitor.getId() + 1000, match.getId());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.createMatchCompetitors(csvData));
        assertEquals(0, matchCompetitorRepository.count());
    }

    @Test
    void testCreateMatchCompetitors_whenRowDuplicatesAnExistingEntry_thenThrowsValidationException() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-013");
        IpscMatch match = createMatch();
        ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()));
        String csvData = String.format("""
                CompetitorId,MatchId,CompetitorCategory,FirearmType,Division
                %d,%d,Junior,Handgun,Open Division
                """, competitor.getId(), match.getId());

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitors(csvData));
    }

    // updateMatchCompetitor()
    @Test
    void testUpdateMatchCompetitor_whenRequestIsValid_thenReplacesFields() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-004");
        IpscMatch match = createMatch();
        Long id = ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()))
                .getMatchCompetitorId();
        MatchCompetitorRequest replacement = validRequest(competitor.getId(), match.getId());
        replacement.setDivision("Standard Division");
        replacement.setPoints(new BigDecimal("12.5"));

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.updateMatchCompetitor(id, replacement);

        // Assert
        assertEquals(id, response.getMatchCompetitorId());
        assertEquals(Division.STANDARD, response.getDivision());
        assertEquals(0, new BigDecimal("12.5").compareTo(response.getPoints()));
    }

    @Test
    void testUpdateMatchCompetitor_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-005");
        IpscMatch match = createMatch();

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.updateMatchCompetitor(
                999_999L, validRequest(competitor.getId(), match.getId())));
    }

    // patchMatchCompetitor()
    @Test
    void testPatchMatchCompetitor_whenOnlySomeFieldsProvided_thenOnlyThoseChange() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-006");
        IpscMatch match = createMatch();
        Long id = ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()))
                .getMatchCompetitorId();
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setIsVisitor(true);
        patch.setOverallRanking(new BigDecimal("7"));

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(id, patch);

        // Assert
        assertEquals(Boolean.TRUE, response.getIsVisitor());
        assertEquals(0, new BigDecimal("7").compareTo(response.getOverallRanking()));
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
    }

    // getMatchCompetitor() / getAllMatchCompetitors()
    @Test
    void testGetMatchCompetitor_whenItExists_thenReturnsIt() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-007");
        IpscMatch match = createMatch();
        Long id = ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()))
                .getMatchCompetitorId();

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.getMatchCompetitor(id);

        // Assert
        assertEquals(id, response.getMatchCompetitorId());
        assertEquals(competitor.getId(), response.getCompetitorId());
        assertEquals(match.getId(), response.getMatchId());
    }

    @Test
    void testGetMatchCompetitor_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.getMatchCompetitor(999_999L));
    }

    @Test
    void testGetAllMatchCompetitors_whenSomeExist_thenIncludesThem() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-008");
        IpscMatch match = createMatch();
        Long id = ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()))
                .getMatchCompetitorId();

        // Act & Assert
        assertTrue(ipscMatchCompetitorService.getAllMatchCompetitors().stream()
                .anyMatch(response -> id.equals(response.getMatchCompetitorId())));
    }

    // deleteMatchCompetitor()
    @Test
    void testDeleteMatchCompetitor_whenItExists_thenRemovesIt() {
        // Arrange
        Competitor competitor = createCompetitor("HPSC-MC-009");
        IpscMatch match = createMatch();
        Long id = ipscMatchCompetitorService.createMatchCompetitor(validRequest(competitor.getId(), match.getId()))
                .getMatchCompetitorId();

        // Act
        ipscMatchCompetitorService.deleteMatchCompetitor(id);

        // Assert
        assertFalse(matchCompetitorRepository.existsById(id));
    }

    @Test
    void testDeleteMatchCompetitor_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.deleteMatchCompetitor(999_999L));
    }

    // Helpers
    private Competitor createCompetitor(String clubNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setClubNumber(clubNumber);
        return competitorRepository.save(competitor);
    }

    private IpscMatch createMatch() {
        IpscMatch match = new IpscMatch();
        match.setName("Club Championship");
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        return ipscMatchRepository.save(match);
    }

    private MatchCompetitorRequest validRequest(Long competitorId, Long matchId) {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(competitorId);
        request.setMatchId(matchId);
        request.setCompetitorCategory(List.of("Junior"));
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        return request;
    }
}
