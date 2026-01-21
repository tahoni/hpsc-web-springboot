package za.co.hpsc.web.models.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Placing {
    private int place;
    private String name;

    public Placing() {
        this.place = 0;
        this.name = "";
    }
}
