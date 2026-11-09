package tn.esprit.clinique.facturation.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tn.esprit.clinique.facturation.event.FacturePayeeNotification;

@Component
@RequiredArgsConstructor
@Slf4j
public class FacturationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publierFacturePayee(FacturePayeeNotification notification) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "facture.payee", notification.event());
        log.info("Published facture.payee for invoice {}", notification.event().factureId());
    }
}
