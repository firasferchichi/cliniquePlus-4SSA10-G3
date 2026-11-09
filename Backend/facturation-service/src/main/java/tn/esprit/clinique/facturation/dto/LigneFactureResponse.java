package tn.esprit.clinique.facturation.dto;

import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.TypeActe;

import java.math.BigDecimal;

public record LigneFactureResponse(
        Long id,
        TypeActe typeActe,
        String libelle,
        Integer quantite,
        BigDecimal prixUnitaire,
        BigDecimal montant) {

    public static LigneFactureResponse from(LigneFacture ligne) {
        return new LigneFactureResponse(ligne.getId(), ligne.getTypeActe(), ligne.getLibelle(),
                ligne.getQuantite(), ligne.getPrixUnitaire(), ligne.getMontant());
    }
}
