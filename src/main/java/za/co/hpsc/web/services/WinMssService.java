package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;

/**
 * Provides an interface for handling operations related to the WinMSS system of the IPSC.
 *
 * <p>
 * The WinMSS system typically involves importing and processing data from
 * CAB files, which contain information such as matches, scores, and other
 * relevant data used in specific domains. This service abstracts the core
 * functionality required to manage the WinMSS integration.
 * </p>
 */
public interface WinMssService {
    /**
     * Imports the content of a WinMSS.cab file into the system.
     *
     * @param cabFileContent the content of the WinMSS.cab file to be imported.
     * @return a {@link ControllerResponse} object containing the result of the operation,
     * including success status, messages, or errors if any occurred during the import process.
     * @throws ValidationException if the provided CAB file content is invalid or does not
     *                             meet the required criteria.
     * @throws FatalException      if a critical error occurs during the import process,
     *                             that prevents further execution.
     */
    ControllerResponse importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException;
}
