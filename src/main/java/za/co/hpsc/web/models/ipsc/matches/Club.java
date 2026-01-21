package za.co.hpsc.web.models.ipsc.matches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

@Getter
@Setter
@AllArgsConstructor
public class Club {
    private String name;
    private String location;

    public Club() {
        this.name = "";
        this.location = "";
    }

    @Override
    public String toString() {
        return ValueUtil.nullAsEmptyString(this.name);
    }
}
