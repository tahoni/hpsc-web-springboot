package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.TransactionService;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void saveMatchResults(IpscResponseHolder ipscResponseHolder) {

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        try {
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void saveMatchLogs() {
        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        try {
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }
}
