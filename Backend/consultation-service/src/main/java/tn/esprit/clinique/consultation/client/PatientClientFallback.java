package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.DossierResumeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/** Comportement en cas d'indisponibilité de patient-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class PatientClientFallback implements PatientClient {

    @Override
    public DossierResumeDTO getDossierResume(Long id) {
        log.warn("patient-service indisponible — fallback activé");
        // Mode dégradé : résumé partiel (à remplacer par le dernier résumé en cache)
        return new DossierResumeDTO(id, null, List.of(), true);
    }
}
