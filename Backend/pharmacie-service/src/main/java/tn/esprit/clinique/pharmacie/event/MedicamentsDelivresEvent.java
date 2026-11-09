package tn.esprit.clinique.pharmacie.event;

import java.math.BigDecimal;
import java.util.List;

/**
 * Événement « medicaments.delivres » — Ajouter les médicaments à la facture.
 * Producteur : pharmacie-service ; consommateurs : facturation-service.
 */
public record MedicamentsDelivresEvent(Long delivranceId, String consultationId, Long patientId, List<Ligne> lignes) {
    public record Ligne(Long medicamentId, String nom, Integer quantite, BigDecimal prixUnitaire) {}
}
