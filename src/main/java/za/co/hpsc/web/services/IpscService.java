package za.co.hpsc.web.services;

import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;

public interface IpscService {
    MatchResultLogResponseHolder processWinMssCabFile(String cabFileContent);
}
