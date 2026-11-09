package tn.esprit.clinique.pharmacie.client.dto;

import java.util.List;

/** Vue réduite reçue de consultation-service (aucune entité partagée entre services). */
public record OrdonnanceDTO(String id, Long patientId, List<LigneDTO> lignes) {}
