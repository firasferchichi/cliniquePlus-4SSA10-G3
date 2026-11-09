package tn.esprit.clinique.facturation.messaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitConfigTest {

    private final RabbitConfig rabbitConfig = new RabbitConfig();

    @Test
    void inputQueuesAreDurableAndDeadLetterAfterRejectedDelivery() {
        var consultationQueue = rabbitConfig.consultationTermineeQueue();
        var medicationQueue = rabbitConfig.medicamentsDelivresQueue();

        assertTrue(consultationQueue.isDurable());
        assertEquals(RabbitConfig.DEAD_LETTER_EXCHANGE,
                consultationQueue.getArguments().get("x-dead-letter-exchange"));
        assertTrue(consultationQueue.getArguments().get("x-dead-letter-routing-key").toString()
                .startsWith("consultation.terminee"));
        assertTrue(medicationQueue.isDurable());
        assertEquals(RabbitConfig.DEAD_LETTER_EXCHANGE,
                medicationQueue.getArguments().get("x-dead-letter-exchange"));
        assertTrue(medicationQueue.getArguments().get("x-dead-letter-routing-key").toString()
                .startsWith("medicaments.delivres"));
    }

    @Test
    void deadLetterQueuesAreDurable() {
        assertTrue(rabbitConfig.consultationTermineeDeadLetterQueue().isDurable());
        assertTrue(rabbitConfig.medicamentsDelivresDeadLetterQueue().isDurable());
    }
}
