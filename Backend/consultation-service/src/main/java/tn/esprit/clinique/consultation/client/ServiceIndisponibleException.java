package tn.esprit.clinique.consultation.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Levée par un fallback quand un service appelé est indisponible (circuit ouvert). */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceIndisponibleException extends RuntimeException {
    public ServiceIndisponibleException(String service) {
        super("Service indisponible : " + service + ". Réessayez plus tard.");
    }
}
