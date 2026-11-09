package tn.esprit.clinique.rendezvous.messaging;

import tn.esprit.clinique.rendezvous.event.RdvCreeEvent;
import tn.esprit.clinique.rendezvous.event.RdvAnnuleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publication des événements produits par rendezvous-service. */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publierRdvCree(RdvCreeEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "rdv.cree", event);
        log.info("Événement rdv.cree publié : {}", event);
    }

    public void publierRdvAnnule(RdvAnnuleEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "rdv.annule", event);
        log.info("Événement rdv.annule publié : {}", event);
    }
}
