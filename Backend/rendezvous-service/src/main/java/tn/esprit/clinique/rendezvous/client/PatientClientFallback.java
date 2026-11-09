package tn.esprit.clinique.rendezvous.client;

import tn.esprit.clinique.rendezvous.client.dto.PatientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Comportement en cas d'indisponibilité de patient-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class PatientClientFallback implements PatientClient {

    @Override
    public PatientDTO getPatient(Long id) {
        log.warn("patient-service indisponible — fallback activé");
        throw new ServiceIndisponibleException("patient-service");
    }
}
