package tn.esprit.clinique.rendezvous.client;

import tn.esprit.clinique.rendezvous.client.dto.PlageTravailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Appel synchrone vers personnel-service (résolu via Eureka, load-balancé).
 * Objectif : Plages de travail du médecin pour calculer les créneaux.
 * Panne : Créneaux non calculables → 503 avec message « réessayer » ; les RDV existants restent consultables.
 */
@FeignClient(name = "personnel-service", fallback = PersonnelClientFallback.class)
public interface PersonnelClient {

    @GetMapping("/api/personnel/medecins/{id}/disponibilites")
    List<PlageTravailDTO> getDisponibilites(@PathVariable("id") Long medecinId, @RequestParam("date") LocalDate date);
}
