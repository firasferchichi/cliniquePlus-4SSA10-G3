package tn.esprit.clinique.rendezvous.event;

import java.time.LocalDate;

/**
 * Événement « personnel.indisponible » — Annuler / replanifier les RDV impactés et prévenir les patients.
 * Producteur : personnel-service ; consommateurs : rendezvous-service, notification-service.
 */
public record MedecinIndisponibleEvent(Long medecinId, LocalDate dateDebut, LocalDate dateFin, String motif) {}
