package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.RendezVousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Comportement en cas d'indisponibilité de rendezvous-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class RendezVousClientFallback implements RendezVousClient {

    @Override
    public RendezVousDTO getRendezVous(Long id) {
        log.warn("rendezvous-service indisponible — fallback activé");
        // Mode dégradé : RDV non vérifié (urgence), la consultation sera marquée « à vérifier »
        return new RendezVousDTO(id, null, null, null, "NON_VERIFIE");
    }
}
