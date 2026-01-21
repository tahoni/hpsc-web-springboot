package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // TODO: add size of column
    @NotNull
    private String name;

    @Lazy
    @OneToMany
    private List<Match> matches = new ArrayList<>();

    public Club(String name) {
        this.name = ValueUtil.nullAsEmptyString(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
