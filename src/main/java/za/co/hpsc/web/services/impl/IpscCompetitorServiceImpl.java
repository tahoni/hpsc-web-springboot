package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequestForCSV;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.IpscCompetitorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IpscCompetitorServiceImpl implements IpscCompetitorService {
    private final CompetitorRepository competitorRepository;
    private final ClubRepository clubRepository;

    public IpscCompetitorServiceImpl(CompetitorRepository competitorRepository, ClubRepository clubRepository) {
        this.competitorRepository = competitorRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    @Transactional
    public CompetitorResponse createCompetitor(CompetitorRequest request) {
        validateForCreate(request);

        Competitor competitor = new Competitor();
        applyFields(competitor, request);
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    @Transactional
    public CompetitorResponseHolder createCompetitors(String csvData)
            throws FatalException {

        if (csvData == null || csvData.isBlank()) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<CompetitorRequestForCSV> competitorRequestForCSVList = readCompetitors(csvData);

        List<CompetitorResponse> competitorResponseList = new ArrayList<>();
        for (CompetitorRequestForCSV competitorRequestForCSV : competitorRequestForCSVList) {
            competitorResponseList.add(createCompetitor(toRequest(competitorRequestForCSV)));
        }

        return new CompetitorResponseHolder(competitorResponseList);
    }

    /**
     * Reads competitor data from a CSV-formatted string and converts it into a list of
     * {@link CompetitorRequestForCSV} objects.
     *
     * @param csvData the CSV data containing competitor information, one competitor per row.
     *                Must not be null or blank.
     * @return a list of {@link CompetitorRequestForCSV} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<CompetitorRequestForCSV> readCompetitors(@NotNull @NotBlank String csvData)
            throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper
                .schemaFor(CompetitorRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();

        try (MappingIterator<CompetitorRequestForCSV> requestMappingIterator =
                     csvMapper.readerFor(CompetitorRequestForCSV.class)
                             .with(csvSchema)
                             .readValues(csvData)) {
            return requestMappingIterator.readAll();

        } catch (MismatchedInputException | IllegalArgumentException | CsvReadException e) {
            log.error("Error parsing CSV data: {}", e.getMessage(), e);
            throw new ValidationException("Invalid CSV data format: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error reading CSV data: {}", e.getMessage(), e);
            throw new FatalException("Error reading CSV data: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a {@link CompetitorRequestForCSV} row onto a {@link CompetitorRequest}.
     *
     * @param competitorRequestForCSV the CSV row to map; must not be null.
     * @return the equivalent {@link CompetitorRequest}, with a {@code null} {@code competitorId}.
     */
    protected CompetitorRequest toRequest(@NotNull CompetitorRequestForCSV competitorRequestForCSV) {
        return new CompetitorRequest(
                null,
                competitorRequestForCSV.getFirstName(),
                competitorRequestForCSV.getLastName(),
                competitorRequestForCSV.getMiddleNames(),
                competitorRequestForCSV.getNickname(),
                competitorRequestForCSV.getDateOfBirth(),
                competitorRequestForCSV.getGender(),
                competitorRequestForCSV.getHomeClub(),
                competitorRequestForCSV.getSapsaNumber(),
                competitorRequestForCSV.getCompetitorNumber(),
                competitorRequestForCSV.getClubNumber(),
                competitorRequestForCSV.getIdNumber(),
                competitorRequestForCSV.getCellphoneNumber(),
                splitEmailAddresses(competitorRequestForCSV.getEmailAddresses()));
    }

    /**
     * Splits a CSV cell of semicolon-separated email addresses into a list.
     *
     * @param rawEmailAddresses the raw CSV cell value (e.g. {@code "a@x.com;b@x.com"}); may be
     *                          null or blank, in which case an empty list is returned.
     * @return the individual, trimmed email addresses, excluding any blank entries.
     */
    protected List<String> splitEmailAddresses(String rawEmailAddresses) {
        if ((rawEmailAddresses == null) || rawEmailAddresses.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(rawEmailAddresses.split(SystemConstants.ARRAY_SEPARATOR))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompetitorResponse updateCompetitor(Long competitorId, CompetitorRequest request) {
        validateForCreate(request);
        Competitor competitor = findCompetitorOrThrow(competitorId);

        applyFields(competitor, request);
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    @Transactional
    public CompetitorResponse patchCompetitor(Long competitorId, CompetitorRequest request) {
        Competitor competitor = findCompetitorOrThrow(competitorId);

        if (request.getFirstName() != null) {
            competitor.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            competitor.setLastName(request.getLastName());
        }
        if (request.getMiddleNames() != null) {
            competitor.setMiddleNames(request.getMiddleNames());
        }
        if (request.getNickname() != null) {
            competitor.setNickname(request.getNickname());
        }
        if (request.getDateOfBirth() != null) {
            competitor.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            competitor.setGender(resolveGender(request.getGender()));
        }
        if (request.getHomeClub() != null) {
            competitor.setHomeClub(resolveHomeClub(request.getHomeClub()));
        }
        if (request.getSapsaNumber() != null) {
            competitor.setSapsaNumber(request.getSapsaNumber());
        }
        if (request.getCompetitorNumber() != null) {
            competitor.setCompetitorNumber(request.getCompetitorNumber());
        }
        if (request.getClubNumber() != null) {
            if (request.getClubNumber().isBlank()) {
                throw new ValidationException("Club number cannot be blank.");
            }
            competitor.setClubNumber(request.getClubNumber());
        }
        if (request.getIdNumber() != null) {
            competitor.setIdNumber(request.getIdNumber());
        }
        if (request.getCellphoneNumber() != null) {
            competitor.setCellphoneNumber(request.getCellphoneNumber());
        }
        if (request.getEmailAddresses() != null) {
            competitor.setEmailAddresses(new ArrayList<>(request.getEmailAddresses()));
        }
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    public CompetitorResponse getCompetitor(Long competitorId) {
        return toResponse(findCompetitorOrThrow(competitorId));
    }

    /**
     * Copies the fields of a {@link CompetitorRequest} onto a {@link Competitor}, resolving the
     * gender and named home club in the process.
     *
     * @param competitor the entity to populate; must not be null.
     * @param request    the request carrying the field values; must not be null.
     * @throws ValidationException if the request's gender doesn't match a known {@link Gender}.
     * @throws NonFatalException   if the request's home club name doesn't match an existing club.
     */
    protected void applyFields(@NotNull Competitor competitor, @NotNull CompetitorRequest request) {
        competitor.setFirstName(request.getFirstName());
        competitor.setLastName(request.getLastName());
        competitor.setMiddleNames(request.getMiddleNames());
        competitor.setNickname(request.getNickname());
        competitor.setDateOfBirth(request.getDateOfBirth());
        competitor.setGender(resolveGender(request.getGender()));
        competitor.setHomeClub(resolveHomeClub(request.getHomeClub()));
        competitor.setSapsaNumber(request.getSapsaNumber());
        competitor.setCompetitorNumber(request.getCompetitorNumber());
        competitor.setClubNumber(request.getClubNumber());
        competitor.setIdNumber(request.getIdNumber());
        competitor.setCellphoneNumber(request.getCellphoneNumber());
        competitor.setEmailAddresses(
                (request.getEmailAddresses() != null) ? new ArrayList<>(request.getEmailAddresses()) : new ArrayList<>());
    }

    /**
     * Retrieves an existing competitor or throws if none exists with the given ID.
     *
     * @param competitorId the identifier to look up.
     * @return the matching {@link Competitor}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    protected Competitor findCompetitorOrThrow(Long competitorId) {
        return competitorRepository.findById(competitorId)
                .orElseThrow(() -> new NonFatalException("No competitor found with ID " + competitorId));
    }

    /**
     * Resolves a competitor's gender by name.
     *
     * @param gender the gender name to look up; may be null or blank, in which case no gender
     *               is set.
     * @return the matching {@link Gender}, or {@code null} if {@code gender} wasn't supplied.
     * @throws ValidationException if {@code gender} was supplied but doesn't match a known gender.
     */
    protected Gender resolveGender(String gender) {
        if ((gender == null) || gender.isBlank()) {
            return null;
        }

        return Gender.fromName(gender)
                .orElseThrow(() -> new ValidationException("Unknown gender: " + gender));
    }

    /**
     * Resolves a competitor's home club by name.
     *
     * @param clubName the club name to look up; may be null or blank, in which case no home
     *                 club is set.
     * @return the matching {@link Club}, or {@code null} if {@code clubName} wasn't supplied.
     * @throws NonFatalException if {@code clubName} was supplied but doesn't match an existing club.
     */
    protected Club resolveHomeClub(String clubName) {
        if ((clubName == null) || clubName.isBlank()) {
            return null;
        }

        return clubRepository.findByName(clubName)
                .orElseThrow(() -> new NonFatalException("No club found with name " + clubName));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * competitor.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(CompetitorRequest request) {
        if (request == null) {
            throw new ValidationException("Competitor request cannot be null.");
        }
        if ((request.getFirstName() == null) || request.getFirstName().isBlank()) {
            throw new ValidationException("First name is required.");
        }
        if ((request.getLastName() == null) || request.getLastName().isBlank()) {
            throw new ValidationException("Last name is required.");
        }
        if ((request.getClubNumber() == null) || request.getClubNumber().isBlank()) {
            throw new ValidationException("Club number is required.");
        }
    }

    /**
     * Maps a persisted competitor to the response shape returned by the controller.
     *
     * @param competitor the competitor to map.
     * @return the mapped {@link CompetitorResponse}.
     */
    protected CompetitorResponse toResponse(Competitor competitor) {
        return new CompetitorResponse(
                competitor.getId(),
                competitor.getFirstName(),
                competitor.getLastName(),
                competitor.getMiddleNames(),
                competitor.getNickname(),
                competitor.getDateOfBirth(),
                competitor.getGender(),
                ((competitor.getHomeClub() != null) ? competitor.getHomeClub().getIdentifier() : null),
                competitor.getSapsaNumber(),
                competitor.getCompetitorNumber(),
                competitor.getClubNumber(),
                competitor.getIdNumber(),
                competitor.getCellphoneNumber(),
                competitor.getEmailAddresses());
    }
}
