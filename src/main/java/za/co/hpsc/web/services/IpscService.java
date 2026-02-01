package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

public interface IpscService {
    IpscResponseHolder importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException;
}
