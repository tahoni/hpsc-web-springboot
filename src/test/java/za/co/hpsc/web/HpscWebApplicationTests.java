package za.co.hpsc.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.TransactionService;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class HpscWebApplicationTests {

    @MockitoBean
    private TestEntityManager testEntityManager;
    @MockitoBean
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private MatchRepository matchRepository;
    @MockitoBean
    private CompetitorRepository competitorRepository;
    @MockitoBean
    private MatchStageRepository matchStageRepository;
    @MockitoBean
    private MatchCompetitorRepository matchCompetitorRepository;
    @MockitoBean
    private MatchStageCompetitorRepository matchStageCompetitorRepository;

    @SuppressWarnings("EmptyMethod")
    @Test
    void contextLoads() {
    }

}
