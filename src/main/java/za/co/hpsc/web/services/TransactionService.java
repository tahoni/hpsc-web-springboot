package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.response.IpscResponse;

// TODO: Javadoc
public interface TransactionService {
    void saveMatchResults(IpscResponse ipscResponse);

    void saveMatchLogs();
}
