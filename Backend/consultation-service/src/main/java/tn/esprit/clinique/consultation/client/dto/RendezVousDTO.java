package tn.esprit.clinique.consultation.client.dto;

import java.time.LocalDateTime;

/** Vue réduite reçue de rendezvous-service (aucune entité partagée entre services). */
public record RendezVousDTO(Long id, Long patientId, Long medecinId, LocalDateTime dateHeure, String statut) {}
