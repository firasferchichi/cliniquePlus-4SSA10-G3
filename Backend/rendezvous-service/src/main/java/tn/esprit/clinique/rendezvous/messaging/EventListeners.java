package tn.esprit.clinique.rendezvous.messaging;

import tn.esprit.clinique.rendezvous.event.MedecinIndisponibleEvent;
import tn.esprit.clinique.rendezvous.event.ConsultationTermineeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Événements consommés par rendezvous-service. */
@Component
@Slf4j
public class EventListeners {

    /** Annuler / replanifier les RDV impactés et prévenir les patients (événement publié par personnel-service). */
    @RabbitListener(queues = "rendezvous-service.personnel.indisponible")
    public void onMedecinIndisponible(MedecinIndisponibleEvent event) {
        log.info("Événement personnel.indisponible reçu : {}", event);
        // TODO logique métier (sprint développement) — traitement idempotent
    }

    /** Créer la facture ; passer le RDV à TERMINE (événement publié par consultation-service). */
    @RabbitListener(queues = "rendezvous-service.consultation.terminee")
    public void onConsultationTerminee(ConsultationTermineeEvent event) {
        log.info("Événement consultation.terminee reçu : {}", event);
        // TODO logique métier (sprint développement) — traitement idempotent
    }
}
