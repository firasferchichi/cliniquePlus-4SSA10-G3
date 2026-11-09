package tn.esprit.clinique.facturation.event;

import tn.esprit.clinique.facturation.domain.ModePaiement;

import java.math.BigDecimal;

public record FacturePayeeEvent(Long factureId, Long patientId, BigDecimal montant, ModePaiement mode) {
}
