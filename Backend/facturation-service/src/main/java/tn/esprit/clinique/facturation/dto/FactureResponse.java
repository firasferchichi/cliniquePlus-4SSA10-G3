package tn.esprit.clinique.facturation.dto;

import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FactureResponse(
        Long id,
        String numero,
        Long patientId,
        String consultationId,
        LocalDateTime dateEmission,
        BigDecimal montantTotal,
        BigDecimal partMutuelle,
        BigDecimal partPatient,
        BigDecimal montantPaye,
        BigDecimal soldeRestant,
        StatutFacture statut,
        List<LigneFactureResponse> lignes,
        List<PaiementResponse> paiements) {

    public static FactureResponse from(Facture facture) {
        return new FactureResponse(
                facture.getId(),
                facture.getNumero(),
                facture.getPatientId(),
                facture.getConsultationId(),
                facture.getDateEmission(),
                facture.getMontantTotal(),
                facture.getPartMutuelle(),
                facture.getPartPatient(),
                facture.montantPaye(),
                facture.soldeRestant(),
                facture.getStatut(),
                facture.getLignes().stream().map(LigneFactureResponse::from).toList(),
                facture.getPaiements().stream().map(PaiementResponse::from).toList());
    }
}
