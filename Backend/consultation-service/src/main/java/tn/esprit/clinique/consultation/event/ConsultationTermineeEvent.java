package tn.esprit.clinique.consultation.event;

import java.util.List;

/**
 * Événement « consultation.terminee » — Créer la facture ; passer le RDV à TERMINE.
 * Producteur : consultation-service ; consommateurs : facturation-service, rendezvous-service.
 */
public record ConsultationTermineeEvent(String consultationId, Long rdvId, Long patientId, Long medecinId, String specialiteCode, List<String> actes) {}
