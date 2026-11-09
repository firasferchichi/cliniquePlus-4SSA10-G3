package tn.esprit.clinique.consultation.client.dto;

/** Vue réduite reçue de pharmacie-service (aucune entité partagée entre services). */
public record MedicamentDTO(Long id, String nom, String dosage) {}
