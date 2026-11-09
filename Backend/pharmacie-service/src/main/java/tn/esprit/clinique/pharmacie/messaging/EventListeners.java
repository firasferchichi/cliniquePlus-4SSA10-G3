package tn.esprit.clinique.pharmacie.messaging;

import tn.esprit.clinique.pharmacie.event.OrdonnanceCreeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Événements consommés par pharmacie-service. */
@Component
@Slf4j
public class EventListeners {

    /** Préparer la délivrance ; informer le patient (événement publié par consultation-service). */
    @RabbitListener(queues = "pharmacie-service.ordonnance.creee")
    public void onOrdonnanceCreee(OrdonnanceCreeeEvent event) {
        log.info("Événement ordonnance.creee reçu : {}", event);
        // TODO logique métier (sprint développement) — traitement idempotent
    }
}
