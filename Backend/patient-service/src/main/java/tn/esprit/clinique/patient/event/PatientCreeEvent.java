package tn.esprit.clinique.patient.event;

/**
 * Événement « patient.cree » — Message de bienvenue.
 * Producteur : patient-service ; consommateurs : notification-service.
 */
public record PatientCreeEvent(Long patientId, String nom, String email) {}
