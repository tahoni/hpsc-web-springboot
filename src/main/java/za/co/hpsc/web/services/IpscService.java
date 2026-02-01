package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;

public interface IpscService {
    ControllerResponse importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException;
}
