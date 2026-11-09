package tn.esprit.clinique.rendezvous.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_statut")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutRdv ancienStatut;

    @Enumerated(EnumType.STRING)
    private StatutRdv nouveauStatut;

    private LocalDateTime dateChangement;

    private String auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id")
    private RendezVous rendezVous;
}
