package tn.esprit.clinique.patient.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "antecedent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Antecedent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeAntecedent type;

    private String description;

    private LocalDate dateDebut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
