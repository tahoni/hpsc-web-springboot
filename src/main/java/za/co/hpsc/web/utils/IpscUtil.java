package za.co.hpsc.web.utils;

import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;

// TODO: add Javadoc
public final class IpscUtil {
    public static String clubTostring(String name, String abbreviation) {
        StringBuilder sb = new StringBuilder();

        // Appends the club name if it is not null and not blank
        if ((name != null && (!name.isBlank()))) {
            sb.append(name.trim());
            sb.append(" ");
        }

        // Appends the club abbreviation in parentheses if it is not null, not blank,
        // and different from the name
        if ((abbreviation != null) && (!abbreviation.isBlank()) &&
                (!abbreviation.equalsIgnoreCase(name))) {
            sb.append("(");
            sb.append(abbreviation.trim());
            sb.append(")");
        }

        // Trim all leading and trailing whitespace and remove parentheses
        // if they are the leading and trailing characters
        String result = sb.toString().trim();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1).trim();
        }

        return result.trim();

    }

    public static String matchToString(String name, String clubName, String clubAbbreviation) {
        String clubNameTrimmed = clubTostring(clubName, clubAbbreviation);
        String matchNameTrimmed = ValueUtil.nullAsEmptyString(name).trim();

        StringBuilder sb = new StringBuilder();

        // Appends the match name if it is not null and not blank
        if (!matchNameTrimmed.isEmpty()) {
            sb.append(matchNameTrimmed);
        }

        // Appends the club name if it is not null and not blank, separating it from the name with an @ sign
        if (!clubNameTrimmed.isEmpty()) {
            sb.append(" @ ").append(clubNameTrimmed);
        }

        return sb.toString().trim();
    }

    public static String matchToString(String name, ClubDto club) {
        if (club != null) {
            return matchToString(name, club.getName(), club.getAbbreviation());
        } else {
            return ValueUtil.nullAsEmptyString(name).trim();
        }
    }
}
