package za.co.hpsc.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

public interface IpscService {
    IpscResponseHolder importWinMssCabFile(String cabFileContent) throws JsonProcessingException;
}
