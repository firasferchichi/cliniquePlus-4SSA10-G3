package tn.esprit.clinique.rendezvous.event;

import java.time.LocalDateTime;

/**
 * Événement « rdv.cree » — Confirmation + planification du rappel J-1.
 * Producteur : rendezvous-service ; consommateurs : notification-service.
 */
public record RdvCreeEvent(Long rdvId, Long patientId, Long medecinId, LocalDateTime dateHeure) {}
