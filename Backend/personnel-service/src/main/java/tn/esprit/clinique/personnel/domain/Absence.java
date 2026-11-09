package tn.esprit.clinique.personnel.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "absence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private MotifAbsence motif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id")
    private MembrePersonnel membre;
}
