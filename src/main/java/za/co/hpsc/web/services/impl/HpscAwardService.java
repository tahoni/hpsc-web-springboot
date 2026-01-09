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
import za.co.hpsc.web.models.*;
import za.co.hpsc.web.services.AwardService;

import java.io.IOException;
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

        return awardRequestList.stream()
                .map(awardRequest -> new AwardResponse[awardRequest])
                .toList();
    }
}
