package tn.esprit.clinique.rendezvous.client.dto;

/** Vue réduite reçue de patient-service (aucune entité partagée entre services). */
public record PatientDTO(Long id, String nom, String prenom, boolean archive) {}
