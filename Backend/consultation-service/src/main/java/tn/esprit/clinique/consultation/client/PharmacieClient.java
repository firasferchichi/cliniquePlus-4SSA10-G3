package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.MedicamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Appel synchrone vers pharmacie-service (résolu via Eureka, load-balancé).
 * Objectif : Autocomplétion du catalogue lors de la prescription.
 * Panne : Saisie libre du médicament, validée plus tard par le pharmacien.
 */
@FeignClient(name = "pharmacie-service", fallback = PharmacieClientFallback.class)
public interface PharmacieClient {

    @GetMapping("/api/medicaments")
    List<MedicamentDTO> rechercher(@RequestParam("nom") String nom);
}
