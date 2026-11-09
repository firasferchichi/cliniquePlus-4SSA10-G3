package tn.esprit.clinique.rendezvous.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;

    private Long medecinId;

    private LocalDateTime dateHeure;

    private Integer dureeMinutes;

    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutRdv statut;

    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoriqueStatut> historique = new ArrayList<>();
}
