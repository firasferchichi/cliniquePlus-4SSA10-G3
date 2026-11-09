package tn.esprit.clinique.consultation.client.dto;

import java.util.List;

/** Vue réduite reçue de patient-service (aucune entité partagée entre services). */
public record DossierResumeDTO(Long patientId, String groupeSanguin, List<String> allergies, boolean partiel) {}
