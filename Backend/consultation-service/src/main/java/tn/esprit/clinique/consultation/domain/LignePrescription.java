package tn.esprit.clinique.consultation.domain;

import lombok.*;

/** Sous-document embarqué (pas de collection propre). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LignePrescription {

    private Long medicamentId;

    private String nomMedicament;

    private String posologie;

    private Integer dureeJours;

    private Integer quantite;
}
