package tn.esprit.clinique.rendezvous.client.dto;

import java.time.LocalTime;

/** Vue réduite reçue de personnel-service (aucune entité partagée entre services). */
public record PlageTravailDTO(LocalTime heureDebut, LocalTime heureFin, Integer dureeCreneauMinutes) {}
