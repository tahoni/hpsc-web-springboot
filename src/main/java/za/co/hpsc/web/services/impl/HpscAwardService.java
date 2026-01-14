package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.AwardCeremonyResponse;
import za.co.hpsc.web.models.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.AwardRequest;
import za.co.hpsc.web.models.AwardRequestForCSV;
import za.co.hpsc.web.services.AwardService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Javadoc
@Service
public class HpscAwardService implements AwardService {
    @Override
    public AwardCeremonyResponseHolder processCsv(String csvData) throws ValidationException, FatalException {
        if (csvData == null || csvData.isBlank()) {
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<AwardRequest> awardRequestList = readAwards(csvData);
        List<AwardCeremonyResponse> awardCeremonyResponseList = mapAwards(awardRequestList);
        return new AwardCeremonyResponseHolder(awardCeremonyResponseList);
    }

    /**
     * Reads award data from a CSV-formatted string and converts it into a list of {@link AwardRequest} objects.
     * <p>
     * The method uses Jackson's CSV parsing functionality with a predefined schema to map CSV data
     * to {@link AwardRequest} instances. Additional validation is performed to ensure data integrity.
     *
     * @param csvData the CSV data containing award information; must not be null or blank
     * @return a list of {@link AwardRequest} objects parsed from the provided CSV data
     * @throws ValidationException if the CSV data format is invalid or contains mismatched input
     * @throws FatalException      if an I/O error occurs while processing the CSV data
     */
    protected List<AwardRequest> readAwards(@NotNull @NotBlank String csvData)
            throws ValidationException, FatalException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(AwardRequestForCSV.class)
                .withArrayElementSeparator("|")
                .withColumnReordering(true)
                .withHeader();
        csvMapper.addMixIn(AwardRequest.class, AwardRequestForCSV.class);

        try (MappingIterator<AwardRequest> requestMappingIterator =
                     csvMapper.readerFor(AwardRequest.class)
                             .with(csvSchema)
                             .readValues(csvData)) {
            return requestMappingIterator.readAll();
        } catch (MismatchedInputException | IllegalArgumentException | CsvReadException e) {
            throw new ValidationException("Invalid CSV data format.", e);
        } catch (IOException e) {
            throw new FatalException("Error reading CSV data.", e);
        }
    }

    /**
     * Maps a list of award requests into a list of award ceremony responses, grouping requests by
     * their ceremony title. Each group represents a ceremony and includes all associated requests.
     *
     * @param awardRequestList the list of award requests to be grouped and mapped into responses;
     *                         must not be null
     * @return a list of {@link AwardCeremonyResponse} objects, each representing a group of award
     * requests associated with the same ceremony title
     * @throws ValidationException if the provided list is null
     */
    protected List<AwardCeremonyResponse> mapAwards(@NotNull List<AwardRequest> awardRequestList) {
        if (awardRequestList == null) {
            throw new ValidationException("Image request list cannot be null.");
        }

        List<AwardCeremonyResponse> responses = new ArrayList<>();
        List<AwardRequest> currentGroup = new ArrayList<>();
        String currentCeremonyTitle = null;

        for (AwardRequest request : awardRequestList) {
            String ceremonyTitle = request.getCeremonyTitle();

            if (currentCeremonyTitle != null && !currentCeremonyTitle.equalsIgnoreCase(ceremonyTitle)) {
                responses.add(new AwardCeremonyResponse(currentGroup));
                currentGroup = new ArrayList<>();
            }

            currentGroup.add(request);
            currentCeremonyTitle = ceremonyTitle;
        }

        if (!currentGroup.isEmpty()) {
            responses.add(new AwardCeremonyResponse(currentGroup));
        }

        return responses;
    }
}
