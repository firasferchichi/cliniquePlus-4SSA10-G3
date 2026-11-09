package tn.esprit.clinique.patient.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "couverture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Couverture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organisme;

    private String numeroAffilie;

    private Integer tauxPriseEnCharge;

    private LocalDate dateExpiration;
}
