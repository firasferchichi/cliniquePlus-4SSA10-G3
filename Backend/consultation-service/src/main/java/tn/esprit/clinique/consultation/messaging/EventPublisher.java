package tn.esprit.clinique.consultation.messaging;

import tn.esprit.clinique.consultation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.consultation.event.OrdonnanceCreeeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publication des événements produits par consultation-service. */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publierConsultationTerminee(ConsultationTermineeEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "consultation.terminee", event);
        log.info("Événement consultation.terminee publié : {}", event);
    }

    public void publierOrdonnanceCreee(OrdonnanceCreeeEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "ordonnance.creee", event);
        log.info("Événement ordonnance.creee publié : {}", event);
    }
}
