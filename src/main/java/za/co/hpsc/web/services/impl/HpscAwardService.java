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
import za.co.hpsc.web.models.AwardRequest;
import za.co.hpsc.web.models.AwardRequestForCSV;
import za.co.hpsc.web.models.AwardResponseHolder;
import za.co.hpsc.web.services.AwardService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HpscAwardService implements AwardService {
    @Override
    public AwardResponseHolder processCsv(String csvData) throws ValidationException, FatalException {
        if (csvData == null || csvData.isBlank()) {
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<AwardRequest> awardRequestList = readAwards(csvData);
        List<AwardCeremonyResponse> awardCeremonyResponseList = mapAwards(awardRequestList);
        return new AwardResponseHolder(awardCeremonyResponseList);
    }

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

    protected List<AwardCeremonyResponse> mapAwards(@NotNull List<AwardRequest> awardRequestList) {
        if (awardRequestList == null) {
            throw new ValidationException("Image request list cannot be null.");
        }

        String previousCeremonyTitle = null;
        String currentCeremonyTitle = "";
        List<AwardCeremonyResponse> awardCeremonyResponseList = new ArrayList<>();
        List<AwardRequest> awardCeremonyAwardRequests = new ArrayList<>();
        for (AwardRequest awardRequest : awardRequestList) {
            if (!currentCeremonyTitle.equalsIgnoreCase(awardRequest.getTitle())) {
                currentCeremonyTitle = awardRequest.getTitle();

                if (previousCeremonyTitle != null) {
                    awardCeremonyResponseList.add(
                            new AwardCeremonyResponse(awardCeremonyAwardRequests)
                    );
                    awardCeremonyAwardRequests = new ArrayList<>();
                }

                previousCeremonyTitle = currentCeremonyTitle;
            }

            awardCeremonyAwardRequests.add(awardRequest);
        }

        return awardCeremonyResponseList;
    }
}
