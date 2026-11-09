package tn.esprit.clinique.facturation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.clinique.facturation.client.PatientCoverageGateway;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.dto.FactureResponse;
import tn.esprit.clinique.facturation.event.ActeFacturableEvent;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.exception.BusinessRuleException;
import tn.esprit.clinique.facturation.exception.ResourceNotFoundException;
import tn.esprit.clinique.facturation.repository.FactureRepository;
import tn.esprit.clinique.facturation.repository.TarifRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationInvoiceCreator {

    private final FactureRepository factureRepository;
    private final TarifRepository tarifRepository;
    private final PatientCoverageGateway coverageGateway;

    @Transactional
    public FactureResponse creer(ConsultationTermineeEvent event) {
        verifierEvent(event);
        var existing = factureRepository.findByConsultationId(event.consultationId());
        if (existing.isPresent()) {
            return FactureResponse.from(existing.get());
        }
        if (event.actes() == null || event.actes().isEmpty()) {
            throw new BusinessRuleException("A completed consultation must contain at least one billable act");
        }

        Facture facture = new Facture();
        facture.setNumero("FAC-" + UUID.randomUUID());
        facture.setPatientId(event.patientId());
        facture.setConsultationId(event.consultationId());
        facture.setDateEmission(LocalDateTime.now());
        facture.setStatut(StatutFacture.BROUILLON);

        for (ActeFacturableEvent acte : event.actes()) {
            if (acte == null || acte.typeActe() == null || blank(acte.code()) || blank(acte.libelle())
                    || acte.quantite() == null || acte.quantite() < 1) {
                throw new BusinessRuleException("Billable act details are incomplete or invalid");
            }
            Tarif tarif = tarifRepository.findByTypeActeAndCode(acte.typeActe(), acte.code())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No tariff configured for " + acte.typeActe() + " code " + acte.code()));
            facture.ajouterLigne(new LigneFacture(acte.typeActe(), acte.libelle(),
                    acte.quantite(), tarif.getMontant(), null));
        }

        var coverage = coverageGateway.getCoverage(facture.getPatientId());
        if (coverage.available()) {
            facture.repartir(coverage.tauxPriseEnCharge());
            facture.setStatut(StatutFacture.EMISE);
        } else {
            facture.repartir(0);
            facture.setStatut(StatutFacture.BROUILLON);
        }
        return FactureResponse.from(factureRepository.saveAndFlush(facture));
    }

    @Transactional(readOnly = true)
    public Optional<FactureResponse> recuperer(String consultationId) {
        return factureRepository.findByConsultationId(consultationId).map(FactureResponse::from);
    }

    private void verifierEvent(ConsultationTermineeEvent event) {
        if (event == null || blank(event.consultationId()) || event.patientId() == null) {
            throw new BusinessRuleException("consultation.terminee event is incomplete");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
