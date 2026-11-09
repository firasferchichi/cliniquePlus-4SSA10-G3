package tn.esprit.clinique.rendezvous.client;

import tn.esprit.clinique.rendezvous.client.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Appel synchrone vers patient-service (résolu via Eureka, load-balancé).
 * Objectif : Vérifier que le patient existe et n'est pas archivé.
 * Panne : Circuit ouvert → 503, la réservation est refusée (pas de RDV pour un patient non vérifié).
 */
@FeignClient(name = "patient-service", fallback = PatientClientFallback.class)
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientDTO getPatient(@PathVariable("id") Long id);
}
