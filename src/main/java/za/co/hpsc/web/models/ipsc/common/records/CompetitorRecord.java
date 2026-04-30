package za.co.hpsc.web.models.ipsc.common.records;

public record CompetitorRecord(
        String firstName,
        String lastName,
        String middleNames,
        String dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,

        String clubName,
        String competitorCategory,

        CompetitorResultRecord results
) {
}
