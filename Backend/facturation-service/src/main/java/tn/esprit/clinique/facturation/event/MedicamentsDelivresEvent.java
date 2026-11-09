package tn.esprit.clinique.facturation.event;

import java.util.List;

public record MedicamentsDelivresEvent(
        String delivranceId,
        String consultationId,
        Long patientId,
        List<LigneMedicamentEvent> lignes) {
}
