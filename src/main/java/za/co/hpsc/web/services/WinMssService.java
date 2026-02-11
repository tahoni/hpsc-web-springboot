package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.records.IpscMatchResponseHolder;

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
     * Imports and processes the content of a WinMSS.cab file.
     *
     * @param cabFileContent the content of the WinMSS.cab file to be imported. It must be provided
     *                       as a non-null string containing the data in a valid JSON format.
     * @return a {@link IpscMatchResponseHolder} object containing the parsed match results
     * extracted from the CAB file.
     * @throws ValidationException if the provided CAB file content fails validation, indicating
     *                             that the input data is incomplete, malformed, or otherwise invalid.
     * @throws FatalException      if a critical error occurs during the processing of the CAB file,
     *                             rendering the operation unable to complete.
     */
    IpscMatchResponseHolder importWinMssCabFile(String cabFileContent)
            throws ValidationException, FatalException;
}
