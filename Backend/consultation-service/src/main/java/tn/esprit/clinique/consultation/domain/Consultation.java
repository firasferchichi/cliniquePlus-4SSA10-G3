package tn.esprit.clinique.consultation.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {

    @Id
    private String id;

    @Indexed
    private Long patientId;

    private Long medecinId;

    private Long rendezVousId;

    private LocalDateTime date;

    private String motif;

    private Map<String, Object> constantes;

    private String examenClinique;

    private List<Diagnostic> diagnostics;

    private List<ExamenDemande> examens;

    private Ordonnance ordonnance;

    private StatutConsultation statut;
}
