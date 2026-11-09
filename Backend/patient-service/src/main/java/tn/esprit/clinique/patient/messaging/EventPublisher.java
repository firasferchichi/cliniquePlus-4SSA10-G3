package tn.esprit.clinique.patient.messaging;

import tn.esprit.clinique.patient.event.PatientCreeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publication des événements produits par patient-service. */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publierPatientCree(PatientCreeEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "patient.cree", event);
        log.info("Événement patient.cree publié : {}", event);
    }
}
