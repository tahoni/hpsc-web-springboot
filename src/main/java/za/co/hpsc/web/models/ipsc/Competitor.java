package za.co.hpsc.web.models.ipsc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.CompetitorCategory;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Competitor {
    private String club;
    private int sapsaNumber;
    private String competitorNumber;
    private CompetitorCategory category;
}
