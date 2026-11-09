package tn.esprit.clinique.facturation.event;

import java.util.List;

public record ConsultationTermineeEvent(
        String consultationId,
        Long rdvId,
        Long patientId,
        Long medecinId,
        String specialiteCode,
        List<ActeFacturableEvent> actes) {
}
