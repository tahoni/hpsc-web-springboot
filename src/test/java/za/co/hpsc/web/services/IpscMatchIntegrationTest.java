package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.impl.IpscMatchServiceImpl;
import za.co.hpsc.web.services.impl.MatchEntityServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;
import za.co.hpsc.web.services.impl.TransformationServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class IpscMatchIntegrationTest {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @MockitoBean
    private CompetitorRepository competitorRepository;
    @MockitoBean
    private ClubRepository clubRepository;
    @MockitoBean
    private IpscMatchRepository ipscMatchRepository;

    @MockitoBean
    private MatchEntityService matchEntityService;
    @MockitoBean
    private ClubEntityService clubEntityService;

    @Autowired
    private TransformationService transformationService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private IpscMatchService ipscMatchService;

    @Test
    void testModifyMatch_withExistingMatchAndPartialUpdate_thenKeepsPersistedValuesForNullFields() throws Exception {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(22L, "Existing Match", "Existing Club", FirearmType.RIFLE,
                2026, 6, 1, 9, 0);

        Mockito.when(matchEntityService.findMatchById(22L)).thenReturn(Optional.of(existingMatch));

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(null);
        incoming.setMatchDate(null);
        incoming.setClub("Updated Club");
        incoming.setFirearm(null);
        incoming.setSquadCount(12);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.modifyMatch(22L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(22L, result.get().getMatchId());
        assertEquals("Existing Match", result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 1, 9, 0), result.get().getMatchDate());
        assertEquals("Updated Club", result.get().getClub());
        assertEquals("Rifle", result.get().getFirearm());
        assertEquals(0, result.get().getSquadCount());
    }

    @Test
    void testUpdateMatch_withExistingMatchAndFullUpdate_thenOverwritesPersistedValues() throws Exception {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(21L, "Existing Match", "Existing Club", FirearmType.HANDGUN,
                2026, 5, 1, 10, 0);

        Mockito.when(matchEntityService.findMatchById(21L)).thenReturn(Optional.of(existingMatch));
//        Mockito.when(clubEntityService.findClubByNameOrAbbreviation("Existing Club")).thenReturn(Optional.of(club));
//        Mockito.when(ipscMatchRepository.findById(21L)).thenReturn(Optional.of(existingMatch));
//        Mockito.when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
//        Mockito.when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(null);
//        Mockito.when(clubRepository.save(any(Club.class))).thenReturn(club);
//        Mockito.when(domainService.initMatchOnlyEntities(any(MatchOnlyDto.class))).thenReturn(Optional.empty());

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(null);
        incoming.setMatchDate(LocalDateTime.of(2026, 8, 15, 10, 0));
        incoming.setClub(null);
        incoming.setFirearm("Shotgun");
        incoming.setSquadCount(null);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.updateMatch(21L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(21L, result.get().getMatchId());
        assertNull(result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 8, 15, 10, 0), result.get().getMatchDate());
        assertNull(result.get().getClub());
        assertEquals("Shotgun", result.get().getFirearm());
        assertEquals(0, result.get().getSquadCount());
    }

    @Test
    void testInsertMatch_withMappedAndInitialisedEntities_thenSavesAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(7L, "Inserted Match", "HPSC", "Handgun", 6);

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(7L);
        MatchOnlyResultsDto matchOnlyResultsDto = new MatchOnlyResultsDto(matchOnlyDto, null);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getMatchId());
        assertEquals("Inserted Match", result.get().getMatchName());
        assertEquals("HPSC", result.get().getClub());
        assertEquals("Handgun", result.get().getFirearm());
        assertEquals(0, result.get().getSquadCount());
    }

    @Test
    void testInsertMatch_withMapMatchOnlyEmpty_thenSkipsDomainAndTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(8L, "No Mapping Match", "HPSC", "Rifle", 4);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(8L, result.get().getMatchId());
    }

    @Test
    void testInsertMatch_withDomainInitialisationEmpty_thenSkipsTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(9L, "No Domain Match", "HPSC", "Shotgun", 2);
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(9L);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(9L, result.get().getMatchId());
    }

    private MatchOnlyRequest buildRequest(Long id, String name, String club, String firearm, Integer squadCount) {
        MatchOnlyRequest request = new MatchOnlyRequest();
        request.setMatchId(id);
        request.setMatchName(name);
        request.setMatchDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        request.setClub(club);
        request.setFirearm(firearm);
        request.setSquadCount(squadCount);
        return request;
    }

    private IpscMatch buildIpscMatch(Long id, String name, String clubName, FirearmType firearmType,
                                     int year, int month, int day, int hour, int minute) {
        Club club = new Club();
        club.setName(clubName);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setId(id);
        ipscMatch.setName(name);
        ipscMatch.setClub(club);
        ipscMatch.setMatchFirearmType(firearmType);
        ipscMatch.setScheduledDate(LocalDateTime.of(year, month, day, hour, minute));
        return ipscMatch;
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        public TransformationService transformationService(ClubEntityService clubEntityService,
                                                           MatchEntityService matchEntityService,
                                                           MatchStageEntityService matchStageEntityService,
                                                           CompetitorEntityService competitorEntityService,
                                                           MatchCompetitorEntityService matchCompetitorEntityService,
                                                           MatchStageCompetitorEntityService matchStageCompetitorEntityService) {
            return new TransformationServiceImpl(clubEntityService, matchEntityService,
                    matchStageEntityService, competitorEntityService,
                    matchCompetitorEntityService, matchStageCompetitorEntityService);
        }

        @Bean
        public TransactionService transactionService(ClubRepository clubRepository,
                                                     IpscMatchRepository ipscMatchRepository,
                                                     IpscMatchStageRepository ipscMatchStageRepository,
                                                     MatchCompetitorRepository matchCompetitorRepository,
                                                     MatchStageCompetitorRepository matchStageCompetitorRepository) {
            return new TransactionServiceImpl(platformTransactionManager, clubRepository,
                    competitorRepository, ipscMatchRepository, ipscMatchStageRepository,
                    matchCompetitorRepository, matchStageCompetitorRepository);
        }

        @Bean
        public MatchEntityService matchEntityService(IpscMatchRepository ipscMatchRepository) {
            return new MatchEntityServiceImpl(ipscMatchRepository);
        }

        @Bean
        public IpscMatchService ipscMatchService(TransformationService transformationService,
                                                 DomainService domainService,
                                                 TransactionService transactionService,
                                                 MatchEntityService matchEntityService) {
            return new IpscMatchServiceImpl(transformationService, domainService, transactionService, matchEntityService);
        }
    }
}
