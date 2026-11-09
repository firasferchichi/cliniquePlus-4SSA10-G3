package tn.esprit.clinique.consultation.domain;

import lombok.*;

/** Sous-document embarqué (pas de collection propre). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamenDemande {

    private String type;

    private String resultat;
}
