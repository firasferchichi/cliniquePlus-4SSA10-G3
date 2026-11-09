package tn.esprit.clinique.pharmacie.client;

import tn.esprit.clinique.pharmacie.client.dto.OrdonnanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Appel synchrone vers consultation-service (résolu via Eureka, load-balancé).
 * Objectif : Relire l'ordonnance au moment de la délivrance.
 * Panne : Délivrance mise en attente (statut EN_ATTENTE), nouvelle tentative.
 */
@FeignClient(name = "consultation-service", fallback = ConsultationClientFallback.class)
public interface ConsultationClient {

    @GetMapping("/api/ordonnances/{id}")
    OrdonnanceDTO getOrdonnance(@PathVariable("id") String id);
}
