package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.DossierResumeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Appel synchrone vers patient-service (résolu via Eureka, load-balancé).
 * Objectif : Afficher allergies / groupe sanguin au médecin.
 * Panne : Dernier résumé mis en cache utilisé, avec bandeau « données patient partielles ».
 */
@FeignClient(name = "patient-service", fallback = PatientClientFallback.class)
public interface PatientClient {

    @GetMapping("/api/patients/{id}/dossier-resume")
    DossierResumeDTO getDossierResume(@PathVariable("id") Long id);
}
