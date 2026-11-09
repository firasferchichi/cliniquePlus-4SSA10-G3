package tn.esprit.clinique.consultation.domain;

import lombok.*;

/** Sous-document embarqué (pas de collection propre). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostic {

    private String codeCim10;

    private String libelle;
}
