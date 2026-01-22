package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    private String abbreviation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Match> matches;

    public Club(String name) {
        this.name = ValueUtil.nullAsEmptyString(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
