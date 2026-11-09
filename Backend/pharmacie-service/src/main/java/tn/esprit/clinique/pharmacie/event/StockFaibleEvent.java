package tn.esprit.clinique.pharmacie.event;

/**
 * Événement « stock.faible » — Alerter pharmacien / administrateur.
 * Producteur : pharmacie-service ; consommateurs : notification-service.
 */
public record StockFaibleEvent(Long medicamentId, String nom, Integer quantiteRestante, Integer seuil) {}
