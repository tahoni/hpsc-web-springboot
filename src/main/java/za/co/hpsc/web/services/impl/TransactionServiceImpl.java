package za.co.hpsc.web.services.impl;

public class TransactionServiceImpl {
}

/*
        TransactionStatus transaction =
                transactionManager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));
        try {
            member.setPartyId(partyId);
            memberRepository.save(member);
            transactionManager.commit(transaction);
            return member;
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
 */