package za.co.hpsc.web.services.impl;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TransactionServiceImpl}'s impl-only protected helper methods
 * ({@code loadAssociations}) - not declared on
 * {@link za.co.hpsc.web.services.TransactionService}. The interface's save/delete contract is
 * covered by {@link za.co.hpsc.web.services.TransactionServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private TransactionServiceImpl transactionServiceImpl;

    @BeforeEach
    void setUp() {
        transactionServiceImpl = new TransactionServiceImpl(competitorRepository, ipscMatchRepository,
                matchCompetitorRepository, transactionManager);
    }

    // loadAssociations(Competitor)
    @Test
    void testLoadAssociations_whenCompetitorHasPlainAssociations_thenReturnsSameCompetitorUnchanged() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        Competitor result = transactionServiceImpl.loadAssociations(competitor);

        // Assert
        assertSame(competitor, result);
        assertTrue(Hibernate.isInitialized(result.getEmailAddresses()));
        assertEquals(List.of("jane.doe@example.com"), result.getEmailAddresses());
    }

    // loadAssociations(IpscMatch)
    @Test
    void testLoadAssociations_whenMatchHasNoClub_thenReturnsSameMatchUnchanged() {
        // Arrange
        IpscMatch match = new IpscMatch();

        // Act
        IpscMatch result = transactionServiceImpl.loadAssociations(match);

        // Assert
        assertSame(match, result);
        assertNull(result.getClub());
    }
}
