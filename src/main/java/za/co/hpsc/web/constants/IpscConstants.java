package za.co.hpsc.web.constants;

import java.util.List;

public class IpscConstants {
    public static final String IPSC_INPUT_DATE_TIME_FORMAT = DateConstants.T_SEPARATED_DATE_TIME_FORMAT;

    public static final List<String> RELEVANT_CAB_FILES = List.of(
            "THEMATCH.XML",
            "STAGE.XML",
            "TAG.XML",
            "ENROLLED.XML",
            "MEMBER.XML",
            "SQUAD.XML",
            "SCORE.XML"
    );
}
