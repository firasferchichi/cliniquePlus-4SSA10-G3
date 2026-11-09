package tn.esprit.clinique.consultation.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/** Sous-document embarqué (pas de collection propre). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance {

    private String id;

    private LocalDateTime dateCreation;

    private List<LignePrescription> lignes;
}
