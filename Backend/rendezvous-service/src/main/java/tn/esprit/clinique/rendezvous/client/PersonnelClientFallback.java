package tn.esprit.clinique.rendezvous.client;

import tn.esprit.clinique.rendezvous.client.dto.PlageTravailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

/** Comportement en cas d'indisponibilité de personnel-service (Resilience4j circuit breaker). */
@Component
@Slf4j
public class PersonnelClientFallback implements PersonnelClient {

    @Override
    public List<PlageTravailDTO> getDisponibilites(Long medecinId, LocalDate date) {
        log.warn("personnel-service indisponible — fallback activé");
        throw new ServiceIndisponibleException("personnel-service");
    }
}
