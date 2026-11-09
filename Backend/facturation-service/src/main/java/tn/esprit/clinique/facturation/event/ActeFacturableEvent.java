package tn.esprit.clinique.facturation.event;

import tn.esprit.clinique.facturation.domain.TypeActe;

public record ActeFacturableEvent(TypeActe typeActe, String code, String libelle, Integer quantite) {
}
