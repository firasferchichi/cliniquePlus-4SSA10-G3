package tn.esprit.clinique.facturation.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.event.MedicamentsDelivresEvent;
import tn.esprit.clinique.facturation.service.FactureService;

@Component
@RequiredArgsConstructor
public class FacturationEventConsumer {

    private final FactureService factureService;

    @RabbitListener(queues = RabbitConfig.CONSULTATION_QUEUE)
    public void consultationTerminee(ConsultationTermineeEvent event) {
        factureService.creerDepuisConsultation(event);
    }

    @RabbitListener(queues = RabbitConfig.MEDICAMENTS_QUEUE)
    public void medicamentsDelivres(MedicamentsDelivresEvent event) {
        factureService.ajouterMedicaments(event);
    }
}
