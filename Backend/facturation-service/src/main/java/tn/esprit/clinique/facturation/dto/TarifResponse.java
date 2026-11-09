package tn.esprit.clinique.facturation.dto;

import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.domain.TypeActe;

import java.math.BigDecimal;

public record TarifResponse(Long id, TypeActe typeActe, String code, BigDecimal montant) {

    public static TarifResponse from(Tarif tarif) {
        return new TarifResponse(tarif.getId(), tarif.getTypeActe(), tarif.getCode(), tarif.getMontant());
    }
}
