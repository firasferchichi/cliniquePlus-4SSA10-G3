package tn.esprit.clinique.pharmacie.client;

import tn.esprit.clinique.pharmacie.client.dto.OrdonnanceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Comportement en cas d'indisponibilité de consultation-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class ConsultationClientFallback implements ConsultationClient {

    @Override
    public OrdonnanceDTO getOrdonnance(String id) {
        log.warn("consultation-service indisponible — fallback activé");
        // La délivrance reste EN_ATTENTE et sera retentée
        throw new ServiceIndisponibleException("consultation-service");
    }
}
