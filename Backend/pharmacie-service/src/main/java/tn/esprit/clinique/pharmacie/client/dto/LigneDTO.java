package tn.esprit.clinique.pharmacie.client.dto;

/** Vue réduite reçue de consultation-service (aucune entité partagée entre services). */
public record LigneDTO(Long medicamentId, String nomMedicament, Integer quantite) {}
