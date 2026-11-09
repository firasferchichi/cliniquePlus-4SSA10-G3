package tn.esprit.clinique.pharmacie.messaging;

import tn.esprit.clinique.pharmacie.event.MedicamentsDelivresEvent;
import tn.esprit.clinique.pharmacie.event.StockFaibleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publication des événements produits par pharmacie-service. */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publierMedicamentsDelivres(MedicamentsDelivresEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "medicaments.delivres", event);
        log.info("Événement medicaments.delivres publié : {}", event);
    }

    public void publierStockFaible(StockFaibleEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "stock.faible", event);
        log.info("Événement stock.faible publié : {}", event);
    }
}
