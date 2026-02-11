package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.records.IpscMatchResponseHolder;

import java.util.List;

public interface IpscMatchResponseService {
    IpscMatchResponseHolder generateIpscMatchResultResponse(List<IpscMatch> ipscMatchEntityList);
}
