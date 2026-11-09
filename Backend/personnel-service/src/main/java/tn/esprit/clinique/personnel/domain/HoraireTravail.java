package tn.esprit.clinique.personnel.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horaire_travail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoraireTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private DayOfWeek jour;

    private LocalTime heureDebut;

    private LocalTime heureFin;

    private Integer dureeCreneauMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id")
    private MembrePersonnel membre;
}
