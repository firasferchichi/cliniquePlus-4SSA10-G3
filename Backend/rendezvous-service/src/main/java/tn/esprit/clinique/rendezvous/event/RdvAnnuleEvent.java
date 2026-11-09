package tn.esprit.clinique.rendezvous.event;

import java.time.LocalDateTime;

/**
 * Événement « rdv.annule » — Prévenir le patient.
 * Producteur : rendezvous-service ; consommateurs : notification-service.
 */
public record RdvAnnuleEvent(Long rdvId, Long patientId, LocalDateTime dateHeure, String motif) {}
