package tn.esprit.clinique.consultation.event;

import java.util.List;

/**
 * Événement « ordonnance.creee » — Préparer la délivrance ; informer le patient.
 * Producteur : consultation-service ; consommateurs : pharmacie-service, notification-service.
 */
public record OrdonnanceCreeeEvent(String ordonnanceId, String consultationId, Long patientId, List<Ligne> lignes) {
    public record Ligne(Long medicamentId, String nomMedicament, Integer quantite) {}
}
