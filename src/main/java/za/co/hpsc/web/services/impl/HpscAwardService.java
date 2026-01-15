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
     * Reads award data from a CSV-formatted string and converts it into a list of
     * {@link AwardRequest} objects.
     * <p>
     * <p>
     * The method uses a {@link CsvMapper} and a custom {@link CsvSchema} configuration
     * to read, map, and convert the input CSV data into instances of {@link AwardRequest}.
     * It ensures that CSV headers are correctly processed and supports reordering of columns.
     * </p>
     *
     * @param csvData the CSV data containing information about award requests.
     *                Each row in the CSV should represent an award request with fields
     *                such as title, file path, file name, and optional metadata.
     *                Must not be null or blank.
     * @return a list of {@link AwardRequest} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data format is invalid or contains mismatched input.
     * @throws FatalException      if an I/O error occurs while processing the CSV data.
     */
    protected List<AwardRequest> readAwards(@NotNull @NotBlank String csvData)
            throws ValidationException, FatalException {
        // Prepare the CSV mapper and schema
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(AwardRequestForCSV.class)
                .withArrayElementSeparator("|")
                .withColumnReordering(true)
                .withHeader();
        csvMapper.addMixIn(AwardRequest.class, AwardRequestForCSV.class);

        // Read the CSV data using the mapper and schema
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
     * Maps a list of {@link AwardRequest} objects into a list of {@link AwardCeremonyResponse}
     * objects, grouping requests by their ceremony title. Each group represents a ceremony
     * and includes all its associated awards.
     *
     * <p>
     * If the input list is null, an empty list is returned.
     * </p>
     *
     * @param awardRequestList the list of {@link AwardRequest} objects to be grouped
     *                         and mapped into responses. Must not be null.
     * @return a list of {@link AwardCeremonyResponse} objects, each representing a group
     * of awards associated with the same ceremony. It will never be null, but it may be empty.
     * @throws ValidationException if the input list is null.
     */
    protected List<AwardCeremonyResponse> mapAwards(@NotNull List<AwardRequest> awardRequestList) {
        if (awardRequestList == null) {
            throw new ValidationException("Image request list cannot be null.");
        }

        List<AwardCeremonyResponse> responses = new ArrayList<>();
        List<AwardRequest> currentGroup = new ArrayList<>();
        String currentCeremonyTitle = null;

        // Group requests by ceremony title and create a response for each group
        for (AwardRequest request : awardRequestList) {
            String ceremonyTitle = request.getCeremonyTitle();

            // Create a new response if the current ceremony title
            // is different from the previous one
            if (currentCeremonyTitle != null && !currentCeremonyTitle.equalsIgnoreCase(ceremonyTitle)) {
                responses.add(new AwardCeremonyResponse(currentGroup));
                currentGroup = new ArrayList<>();
            }

            // Add the current request to the current response
            currentGroup.add(request);
            currentCeremonyTitle = ceremonyTitle;
        }

        // Add the last response if it's not empty'
        if (!currentGroup.isEmpty()) {
            responses.add(new AwardCeremonyResponse(currentGroup));
        }

        return responses;
    }
}
