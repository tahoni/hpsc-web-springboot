package za.co.hpsc.web.utils;

import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;

public class IpscUtil {
    public static String matchToString(String name, String clubName) {
        String clubString = ((clubName != null) ? clubName.trim() : null);
        String nameString = ((name != null) ? name.trim() : null);

        // Returns name, optionally with club if available
        String result = "";
        if ((clubString != null) && (!clubString.isBlank())) {
            result = nameString + " @ " + clubString;
        } else if (nameString != null) {
            result = nameString;
        }
        return result.trim();
    }

    public static String matchToString(String name, ClubDto club) {
        return matchToString(name, ((club != null) ? club.getAbbreviation() : null));
    }
}
