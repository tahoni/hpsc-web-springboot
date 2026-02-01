package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.response.IpscResponse;

public interface TransactionService {
    void saveMatchResults(IpscResponse ipscResponse);

    void saveMatchLogs();
}
