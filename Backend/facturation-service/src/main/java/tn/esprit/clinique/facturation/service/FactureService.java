package tn.esprit.clinique.facturation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.clinique.facturation.client.CoverageLookup;
import tn.esprit.clinique.facturation.client.PatientCoverageGateway;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.ModePaiement;
import tn.esprit.clinique.facturation.domain.Paiement;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.dto.FactureResponse;
import tn.esprit.clinique.facturation.dto.PaiementRequest;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.event.FacturePayeeEvent;
import tn.esprit.clinique.facturation.event.FacturePayeeNotification;
import tn.esprit.clinique.facturation.event.LigneMedicamentEvent;
import tn.esprit.clinique.facturation.event.MedicamentsDelivresEvent;
import tn.esprit.clinique.facturation.exception.BusinessRuleException;
import tn.esprit.clinique.facturation.exception.ConflictException;
import tn.esprit.clinique.facturation.exception.ResourceNotFoundException;
import tn.esprit.clinique.facturation.repository.FactureRepository;
import tn.esprit.clinique.facturation.repository.LigneFactureRepository;
import tn.esprit.clinique.facturation.repository.TarifRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;
    private final LigneFactureRepository ligneFactureRepository;
    private final TarifRepository tarifRepository;
    private final PatientCoverageGateway coverageGateway;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultationInvoiceCreator consultationInvoiceCreator;

    @Transactional(readOnly = true)
    public List<FactureResponse> lister(Long patientId, StatutFacture statut) {
        List<Facture> factures;
        if (patientId != null && statut != null) {
            factures = factureRepository.findByPatientIdAndStatut(patientId, statut);
        } else if (patientId != null) {
            factures = factureRepository.findByPatientId(patientId);
        } else if (statut != null) {
            factures = factureRepository.findByStatut(statut);
        } else {
            factures = factureRepository.findAll();
        }
        return factures.stream().map(FactureResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FactureResponse obtenir(Long id) {
        return FactureResponse.from(trouver(id));
    }

    @Transactional
    public FactureResponse enregistrerPaiement(Long id, PaiementRequest request) {
        Facture facture = trouverPourModification(id);
        if (facture.getStatut() != StatutFacture.EMISE) {
            throw new ConflictException("Only an issued invoice can receive a payment");
        }
        if (request.montant().compareTo(facture.soldeRestant()) > 0) {
            throw new BusinessRuleException("Payment exceeds the remaining invoice balance");
        }
        Paiement paiement = new Paiement(request.montant(), request.mode(), LocalDateTime.now());
        facture.ajouterPaiement(paiement);
        if (facture.soldeRestant().signum() == 0) {
            facture.setStatut(StatutFacture.PAYEE);
            applicationEventPublisher.publishEvent(new FacturePayeeNotification(
                    new FacturePayeeEvent(facture.getId(), facture.getPatientId(),
                            facture.getMontantTotal(), paiement.getMode())));
        }
        return FactureResponse.from(facture);
    }

    @Transactional
    public FactureResponse annuler(Long id) {
        Facture facture = trouverPourModification(id);
        if (facture.getStatut() == StatutFacture.PAYEE
                || facture.getStatut() == StatutFacture.ANNULEE
                || facture.montantPaye().signum() > 0) {
            throw new ConflictException("A paid, partially paid, or already cancelled invoice cannot be cancelled");
        }
        facture.setStatut(StatutFacture.ANNULEE);
        return FactureResponse.from(facture);
    }

    @Transactional
    public FactureResponse recalculerCouverture(Long id) {
        Facture facture = trouverPourModification(id);
        if (facture.getStatut() != StatutFacture.BROUILLON) {
            throw new ConflictException("Only a draft invoice can have its coverage recalculated");
        }
        appliquerCouverture(facture, true);
        return FactureResponse.from(facture);
    }

    public FactureResponse creerDepuisConsultation(ConsultationTermineeEvent event) {
        try {
            return consultationInvoiceCreator.creer(event);
        } catch (DataIntegrityViolationException exception) {
            if (event == null || blank(event.consultationId())) {
                throw exception;
            }
            return consultationInvoiceCreator.recuperer(event.consultationId())
                    .orElseThrow(() -> exception);
        }
    }

    @Transactional
    public void ajouterMedicaments(MedicamentsDelivresEvent event) {
        if (event == null || blank(event.delivranceId()) || blank(event.consultationId())
                || event.patientId() == null || event.lignes() == null || event.lignes().isEmpty()) {
            throw new BusinessRuleException("medicaments.delivres event is incomplete");
        }
        Facture facture = factureRepository.findByConsultationIdForUpdate(event.consultationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No invoice exists for consultation " + event.consultationId()));
        if (!facture.getPatientId().equals(event.patientId())) {
            throw new BusinessRuleException("Medication event patient does not match the invoice patient");
        }
        verifierFactureOuverte(facture);

        List<String> sourceIds = new ArrayList<>();
        for (int index = 0; index < event.lignes().size(); index++) {
            sourceIds.add(event.delivranceId() + ":" + index);
        }
        var existingLines = ligneFactureRepository.findBySourceEventIdIn(sourceIds).stream()
                .collect(java.util.stream.Collectors.toMap(LigneFacture::getSourceEventId, ligne -> ligne));
        for (LigneFacture existingLine : existingLines.values()) {
            if (existingLine.getFacture() != null
                    && !existingLine.getFacture().getConsultationId().equals(event.consultationId())) {
                throw new BusinessRuleException("Medication delivery ID was already used for another consultation");
            }
        }
        if (existingLines.keySet().containsAll(sourceIds)) {
            return;
        }
        for (int index = 0; index < event.lignes().size(); index++) {
            String sourceId = sourceIds.get(index);
            if (existingLines.containsKey(sourceId)) {
                continue;
            }
            LigneMedicamentEvent ligne = event.lignes().get(index);
            ajouterLigneTarifee(facture, TypeActe.MEDICAMENT, ligne.code(), ligne.libelle(),
                    ligne.quantite(), sourceId);
        }
        facture.recalculerMontantTotal();
        appliquerCouverture(facture, false);
    }

    private void ajouterLigneTarifee(Facture facture, TypeActe typeActe, String code,
                                    String libelle, Integer quantite, String sourceEventId) {
        if (typeActe == null || blank(code) || blank(libelle) || quantite == null || quantite < 1) {
            throw new BusinessRuleException("Billable act details are incomplete or invalid");
        }
        Tarif tarif = tarifRepository.findByTypeActeAndCode(typeActe, code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No tariff configured for " + typeActe + " code " + code));
        facture.ajouterLigne(new LigneFacture(typeActe, libelle, quantite, tarif.getMontant(), sourceEventId));
    }

    private void appliquerCouverture(Facture facture, boolean draftOnUnavailable) {
        CoverageLookup coverage = coverageGateway.getCoverage(facture.getPatientId());
        if (coverage.available()) {
            facture.repartir(coverage.tauxPriseEnCharge());
            facture.setStatut(StatutFacture.EMISE);
        } else if (draftOnUnavailable) {
            facture.repartir(0);
            facture.setStatut(StatutFacture.BROUILLON);
        } else {
            facture.repartir(facture.getTauxMutuelle());
        }
    }

    private void verifierFactureOuverte(Facture facture) {
        if (facture.getStatut() == StatutFacture.ANNULEE || facture.getStatut() == StatutFacture.PAYEE) {
            throw new ConflictException("A paid or cancelled invoice cannot be modified");
        }
    }

    private Facture trouver(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice " + id + " was not found"));
    }

    private Facture trouverPourModification(Long id) {
        return factureRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice " + id + " was not found"));
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
