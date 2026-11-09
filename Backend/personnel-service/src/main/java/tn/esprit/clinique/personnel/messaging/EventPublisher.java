package tn.esprit.clinique.personnel.messaging;

import tn.esprit.clinique.personnel.event.MedecinIndisponibleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publication des événements produits par personnel-service. */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publierMedecinIndisponible(MedecinIndisponibleEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "personnel.indisponible", event);
        log.info("Événement personnel.indisponible publié : {}", event);
    }
}
