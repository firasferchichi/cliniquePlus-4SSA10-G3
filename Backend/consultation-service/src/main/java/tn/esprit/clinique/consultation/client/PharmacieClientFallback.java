package tn.esprit.clinique.consultation.client;

import tn.esprit.clinique.consultation.client.dto.MedicamentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/** Comportement en cas d'indisponibilité de pharmacie-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class PharmacieClientFallback implements PharmacieClient {

    @Override
    public List<MedicamentDTO> rechercher(String nom) {
        log.warn("pharmacie-service indisponible — fallback activé");
        // Mode dégradé : pas d'autocomplétion, saisie libre côté front
        return List.of();
    }
}
