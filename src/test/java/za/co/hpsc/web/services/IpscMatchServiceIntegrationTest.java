package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import za.co.hpsc.web.HpscWebApplication;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.impl.MatchEntityServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(classes = {HpscWebApplication.class, IpscMatchServiceIntegrationTest.IpscMatchServiceTestConfig.class})
public class IpscMatchServiceIntegrationTest {

    @Autowired
    private IpscMatchService ipscMatchService;

    @Autowired
    private TestTransformationService transformationService;

    @Autowired
    private TestDomainService domainService;

    @Autowired
    private TestTransactionService transactionService;

    @Autowired
    private TestMatchEntityService matchEntityService;

    @BeforeEach
    public void setUp() {
        transformationService.reset();
        domainService.reset();
        transactionService.reset();
        matchEntityService.reset();
    }

    @Test
    public void testInsertMatch_whenMatchResponseContainsTransformableEntries_thenPersistsEachMapping() throws FatalException {
        MatchResponse request = new MatchResponse(100, "Integration Match",
                LocalDateTime.of(2026, 4, 29, 10, 0), 1, 5, 1);

        transformationService.ipscResponsesToReturn = List.of(new IpscResponse(), new IpscResponse());
        transformationService.matchResultsToReturn = List.of(
                Optional.of(new MatchResultsDto()),
                Optional.of(new MatchResultsDto())
        );
        domainService.dtoMappingsToReturn = List.of(
                Optional.of(new DtoMapping()),
                Optional.of(new DtoMapping())
        );

        ipscMatchService.insertMatch(request);

        assertEquals(1, transformationService.mapMatchOnlyCalls.get());
        assertEquals(2, transformationService.initMatchResultsCalls.get());
        assertEquals(2, domainService.initMatchEntitiesCalls.get());
        assertEquals(2, transactionService.saveMatchResultsCalls.get());
    }

    @Test
    public void testInsertMatch_whenNoMatchResultsProduced_thenSkipsDomainAndTransaction() throws FatalException {
        MatchResponse request = new MatchResponse(101, "No Result Match",
                LocalDateTime.of(2026, 4, 29, 11, 0), 1, 5, 1);

        transformationService.ipscResponsesToReturn = List.of(new IpscResponse(), new IpscResponse());
        transformationService.matchResultsToReturn = List.of(Optional.empty(), Optional.empty());

        ipscMatchService.insertMatch(request);

        assertEquals(1, transformationService.mapMatchOnlyCalls.get());
        assertEquals(2, transformationService.initMatchResultsCalls.get());
        assertEquals(0, domainService.initMatchEntitiesCalls.get());
        assertEquals(0, transactionService.saveMatchResultsCalls.get());
    }

    @Test
    public void testUpdateMatch_whenMatchDoesNotExist_thenExitsWithoutPersisting() throws FatalException {
        Long matchId = 500L;
        MatchResponse request = new MatchResponse(500, "Missing Match",
                LocalDateTime.of(2026, 4, 29, 12, 0), 1, 5, 1);

        matchEntityService.findByIdResult = Optional.empty();

        ipscMatchService.updateMatch(matchId, request);

        assertTrue(matchEntityService.findByIdCalls.get() >= 1);
        assertEquals(0, transformationService.mapMatchOnlyCalls.get());
        assertEquals(0, transactionService.saveMatchResultsCalls.get());
    }

    @Test
    public void testUpdateMatch_whenMatchExists_thenMergesAndPersists() throws FatalException {
        Long matchId = 501L;
        MatchResponse request = new MatchResponse(501, "Updated Match",
                LocalDateTime.of(2026, 4, 29, 13, 0), 1, 5, 1);

        IpscMatch existing = new IpscMatch();
        existing.setId(matchId);
        existing.setName("Persisted Match");
        existing.setScheduledDate(LocalDateTime.of(2026, 4, 1, 9, 0));
        matchEntityService.findByIdResult = Optional.of(existing);

        transformationService.ipscResponsesToReturn = List.of(new IpscResponse());
        transformationService.matchResultsToReturn = List.of(Optional.of(new MatchResultsDto()));
        domainService.dtoMappingsToReturn = List.of(Optional.of(new DtoMapping()));

        ipscMatchService.updateMatch(matchId, request);

        assertTrue(matchEntityService.findByIdCalls.get() >= 1);
        assertEquals(1, transformationService.mapMatchOnlyCalls.get());
        assertEquals(1, domainService.initMatchEntitiesCalls.get());
        assertEquals(1, transactionService.saveMatchResultsCalls.get());
    }

    @Test
    public void testModifyMatch_whenMatchExists_thenMergesAndPersists() throws FatalException {
        Long matchId = 502L;
        MatchResponse request = new MatchResponse(502, "Patched Match",
                LocalDateTime.of(2026, 4, 29, 14, 0), 1, 6, 2);

        IpscMatch existing = new IpscMatch();
        existing.setId(matchId);
        existing.setName("Persisted Patch Target");
        existing.setScheduledDate(LocalDateTime.of(2026, 4, 10, 9, 0));
        matchEntityService.findByIdResult = Optional.of(existing);

        transformationService.ipscResponsesToReturn = List.of(new IpscResponse());
        transformationService.matchResultsToReturn = List.of(Optional.of(new MatchResultsDto()));
        domainService.dtoMappingsToReturn = List.of(Optional.of(new DtoMapping()));

        ipscMatchService.modifyMatch(matchId, request);

        assertTrue(matchEntityService.findByIdCalls.get() >= 1);
        assertEquals(1, transformationService.mapMatchOnlyCalls.get());
        assertEquals(1, domainService.initMatchEntitiesCalls.get());
        assertEquals(1, transactionService.saveMatchResultsCalls.get());
    }

    @Test
    public void testGetMatches_whenSearchRequestProvided_thenReturnsEmptyList() {
        List<MatchResponse> results = ipscMatchService.getMatches(new MatchSearchRequest());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetMatch_whenIdProvided_thenReturnsEmptyOptional() {
        Optional<MatchResponse> result = ipscMatchService.getMatch(123L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @TestConfiguration
    static class IpscMatchServiceTestConfig {
        @Bean
        @Primary
        public TransformationService transformationServiceBean(TestTransformationService delegate) {
            return delegate;
        }

        @Bean
        @Primary
        public DomainService domainServiceBean(TestDomainService delegate) {
            return delegate;
        }

        @Bean
        @Primary
        public TransactionService transactionServiceBean(TestTransactionService delegate) {
            return delegate;
        }

        @Bean
        @Primary
        public MatchEntityServiceImpl matchEntityServiceBean(TestMatchEntityService delegate) {
            return delegate;
        }

        @Bean
        public TestTransformationService testTransformationService() {
            return new TestTransformationService();
        }

        @Bean
        public TestDomainService testDomainService() {
            return new TestDomainService();
        }

        @Bean
        public TestTransactionService testTransactionService() {
            return new TestTransactionService();
        }

        @Bean
        public TestMatchEntityService testMatchEntityService() {
            return new TestMatchEntityService();
        }
    }

    static class TestTransformationService implements TransformationService {
        private final AtomicInteger mapMatchOnlyCalls = new AtomicInteger();
        private final AtomicInteger initMatchResultsCalls = new AtomicInteger();

        private List<IpscResponse> ipscResponsesToReturn = List.of();
        private List<Optional<MatchResultsDto>> matchResultsToReturn = List.of();
        private int matchResultsCursor = 0;

        private void reset() {
            mapMatchOnlyCalls.set(0);
            initMatchResultsCalls.set(0);
            ipscResponsesToReturn = List.of();
            matchResultsToReturn = List.of();
            matchResultsCursor = 0;
        }

        @Override
        public IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder) {
            return new IpscResponseHolder(List.of());
        }

        @Override
        public IpscResponseHolder mapMatchOnly(MatchResponse matchResponse) {
            mapMatchOnlyCalls.incrementAndGet();
            return new IpscResponseHolder(new ArrayList<>(ipscResponsesToReturn));
        }

        @Override
        public IpscMatchRecordHolder generateIpscMatchRecordHolder(List<za.co.hpsc.web.models.ipsc.holders.data.MatchHolder> ipscMatchHolderList) {
            return new IpscMatchRecordHolder(List.of());
        }

        @Override
        public Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse) {
            initMatchResultsCalls.incrementAndGet();
            if (matchResultsCursor < matchResultsToReturn.size()) {
                Optional<MatchResultsDto> result = matchResultsToReturn.get(matchResultsCursor);
                matchResultsCursor += 1;
                return result;
            }
            return Optional.empty();
        }
    }

    static class TestDomainService implements DomainService {
        private final AtomicInteger initMatchEntitiesCalls = new AtomicInteger();

        private List<Optional<DtoMapping>> dtoMappingsToReturn = List.of();
        private int mappingCursor = 0;

        private void reset() {
            initMatchEntitiesCalls.set(0);
            dtoMappingsToReturn = List.of();
            mappingCursor = 0;
        }

        @Override
        public Optional<DtoMapping> initMatchEntities(MatchResultsDto matchResults,
                                                      String filterClubAbbreviation,
                                                      String matchClubAbbreviation) {
            initMatchEntitiesCalls.incrementAndGet();
            if (mappingCursor < dtoMappingsToReturn.size()) {
                Optional<DtoMapping> result = dtoMappingsToReturn.get(mappingCursor);
                mappingCursor += 1;
                return result;
            }
            return Optional.empty();
        }
    }

    static class TestTransactionService implements TransactionService {
        private final AtomicInteger saveMatchResultsCalls = new AtomicInteger();

        private void reset() {
            saveMatchResultsCalls.set(0);
        }

        @Override
        public Optional<MatchHolder> saveMatchResults(DtoMapping dtoMapping) {
            saveMatchResultsCalls.incrementAndGet();
            return Optional.empty();
        }
    }

    static class TestMatchEntityService extends MatchEntityServiceImpl {
        private final AtomicInteger findByIdCalls = new AtomicInteger();

        private Optional<IpscMatch> findByIdResult = Optional.empty();

        private TestMatchEntityService() {
            super(null);
        }

        private void reset() {
            findByIdCalls.set(0);
            findByIdResult = Optional.empty();
        }

        @Override
        public Optional<IpscMatch> findMatchById(Long matchId) {
            findByIdCalls.incrementAndGet();
            return findByIdResult;
        }
    }
}


