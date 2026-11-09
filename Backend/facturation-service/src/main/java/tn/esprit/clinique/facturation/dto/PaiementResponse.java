package tn.esprit.clinique.facturation.dto;

import tn.esprit.clinique.facturation.domain.ModePaiement;
import tn.esprit.clinique.facturation.domain.Paiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponse(Long id, BigDecimal montant, ModePaiement mode, LocalDateTime date) {

    public static PaiementResponse from(Paiement paiement) {
        return new PaiementResponse(paiement.getId(), paiement.getMontant(),
                paiement.getMode(), paiement.getDate());
    }
}
