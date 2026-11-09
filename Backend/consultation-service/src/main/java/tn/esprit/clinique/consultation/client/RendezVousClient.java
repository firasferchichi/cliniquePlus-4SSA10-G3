package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.RendezVousDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Appel synchrone vers rendezvous-service (résolu via Eureka, load-balancé).
 * Objectif : Rattacher la consultation à un RDV confirmé.
 * Panne : Consultation ouverte sans RDV vérifié (cas d'urgence), marquée à vérifier.
 */
@FeignClient(name = "rendezvous-service", fallback = RendezVousClientFallback.class)
public interface RendezVousClient {

    @GetMapping("/api/rendez-vous/{id}")
    RendezVousDTO getRendezVous(@PathVariable("id") Long id);
}
