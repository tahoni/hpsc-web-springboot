package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.GenderConverter;
import za.co.hpsc.web.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "competitor")
public class Competitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_names")
    private String middleNames;

    private String nickname;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_club_id")
    private Club homeClub;

    @Column(name = "sapsa_number")
    private Integer sapsaNumber;

    @Column(name = "competitor_number")
    private String competitorNumber;

    @Column(name = "club_number", nullable = false, unique = true)
    private String clubNumber;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "cellphone_number")
    private String cellphoneNumber;

    @ElementCollection
    @CollectionTable(name = "competitor_email", joinColumns = @JoinColumn(name = "competitor_id"))
    @Column(name = "email_address", nullable = false)
    private List<String> emailAddresses = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
