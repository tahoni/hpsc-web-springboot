package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

public interface TransactionService {
    void saveMatchResults(IpscResponseHolder ipscResponseHolder);

    void saveMatchLogs();
}
