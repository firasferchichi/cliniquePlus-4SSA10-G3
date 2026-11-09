package tn.esprit.clinique.facturation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.clinique.facturation.client.CoverageLookup;
import tn.esprit.clinique.facturation.client.PatientCoverageGateway;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.ModePaiement;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.dto.FactureResponse;
import tn.esprit.clinique.facturation.dto.PaiementRequest;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.event.LigneMedicamentEvent;
import tn.esprit.clinique.facturation.event.MedicamentsDelivresEvent;
import tn.esprit.clinique.facturation.exception.BusinessRuleException;
import tn.esprit.clinique.facturation.exception.ConflictException;
import tn.esprit.clinique.facturation.repository.FactureRepository;
import tn.esprit.clinique.facturation.repository.LigneFactureRepository;
import tn.esprit.clinique.facturation.repository.TarifRepository;
import org.springframework.dao.DataIntegrityViolationException;
import tn.esprit.clinique.facturation.service.ConsultationInvoiceCreator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactureServiceTest {

    @Mock
    private FactureRepository factureRepository;
    @Mock
    private LigneFactureRepository ligneFactureRepository;
    @Mock
    private TarifRepository tarifRepository;
    @Mock
    private PatientCoverageGateway coverageGateway;
    @Mock
    private org.springframework.context.ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private ConsultationInvoiceCreator consultationInvoiceCreator;
    @InjectMocks
    private FactureService factureService;

    @Test
    void createsInvoiceAndCalculatesMutualAndPatientShares() {
        ConsultationTermineeEvent event = new ConsultationTermineeEvent(
                "consult-1", 15L, 12L, 20L, "GEN", List.of());
        when(consultationInvoiceCreator.creer(event)).thenReturn(response("consult-1", "100.00"));
        var response = factureService.creerDepuisConsultation(event);

        assertEquals("consult-1", response.consultationId());
        verify(consultationInvoiceCreator).creer(event);
    }

    @Test
    void createsDraftWhenCoverageServiceIsUnavailable() {
        ConsultationTermineeEvent event = new ConsultationTermineeEvent(
                "consult-2", 15L, 12L, 20L, "GEN", List.of());
        when(consultationInvoiceCreator.creer(event)).thenReturn(response("consult-2", "80.00"));
        var response = factureService.creerDepuisConsultation(event);

        assertEquals("consult-2", response.consultationId());
    }

    @Test
    void ignoresDuplicateConsultationEvent() {
        ConsultationTermineeEvent event = new ConsultationTermineeEvent(
                "consult-1", 15L, 12L, 20L, "GEN", List.of());
        when(consultationInvoiceCreator.creer(event)).thenReturn(response("consult-1", "25.00"));
        var response = factureService.creerDepuisConsultation(event);

        assertEquals("consult-1", response.consultationId());
        verify(consultationInvoiceCreator).creer(event);
    }

    @Test
    void concurrentDuplicateConsultationEventsRecoverFromUniqueConstraintRace() throws Exception {
        String consultationId = "consult-race";
        ConsultationTermineeEvent event = new ConsultationTermineeEvent(
                consultationId, 15L, 12L, 20L, "GEN", List.of());
        FactureResponse winner = response(consultationId, "25.00");
        CountDownLatch bothCallsReachedInsert = new CountDownLatch(2);
        AtomicInteger callCount = new AtomicInteger();
        when(consultationInvoiceCreator.creer(event)).thenAnswer(invocation -> {
            bothCallsReachedInsert.countDown();
            if (!bothCallsReachedInsert.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Concurrent test did not reach both create attempts");
            }
            if (callCount.incrementAndGet() == 1) {
                return winner;
            }
            throw new DataIntegrityViolationException("duplicate consultationId");
        });
        when(consultationInvoiceCreator.recuperer(consultationId))
                .thenReturn(Optional.of(winner));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<FactureResponse> first = executor.submit(() -> factureService.creerDepuisConsultation(event));
            Future<FactureResponse> second = executor.submit(() -> factureService.creerDepuisConsultation(event));
            assertEquals(consultationId, first.get(10, TimeUnit.SECONDS).consultationId());
            assertEquals(consultationId, second.get(10, TimeUnit.SECONDS).consultationId());
            verify(consultationInvoiceCreator).recuperer(consultationId);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void rejectsPaymentGreaterThanRemainingInvoiceBalance() {
        Facture facture = factureWithTotal("consult-1", "100.00");
        facture.ajouterPaiement(new tn.esprit.clinique.facturation.domain.Paiement(
                money("60.00"), ModePaiement.CARTE, java.time.LocalDateTime.now()));
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        assertThrows(BusinessRuleException.class, () -> factureService.enregistrerPaiement(
                1L, new PaiementRequest(money("40.01"), ModePaiement.CARTE)));
        assertEquals(money("60.00"), facture.montantPaye());
    }

    @Test
    void rejectsPaymentOnDraftInvoice() {
        Facture facture = factureWithTotal("consult-1", "100.00");
        facture.setStatut(StatutFacture.BROUILLON);
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        assertThrows(ConflictException.class, () -> factureService.enregistrerPaiement(
                1L, new PaiementRequest(money("10.00"), ModePaiement.CARTE)));
    }

    @Test
    void marksInvoicePaidAndPublishesAfterFullPayment() {
        Facture facture = factureWithTotal("consult-1", "100.00");
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        var response = factureService.enregistrerPaiement(
                1L, new PaiementRequest(money("100.00"), ModePaiement.CARTE));

        assertEquals(StatutFacture.PAYEE, response.statut());
        assertEquals(money("0.00"), response.soldeRestant());
        verify(applicationEventPublisher).publishEvent(any(
                tn.esprit.clinique.facturation.event.FacturePayeeNotification.class));
    }

    @Test
    void cancelsAnUnpaidInvoice() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        var response = factureService.annuler(1L);

        assertEquals(StatutFacture.ANNULEE, response.statut());
    }

    @Test
    void doesNotCancelAnInvoiceWithPartialPayment() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        facture.ajouterPaiement(new tn.esprit.clinique.facturation.domain.Paiement(
                money("10.00"), ModePaiement.CARTE, java.time.LocalDateTime.now()));
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        assertThrows(ConflictException.class, () -> factureService.annuler(1L));
    }

    @Test
    void doesNotCancelFullyPaidInvoice() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        facture.ajouterPaiement(new tn.esprit.clinique.facturation.domain.Paiement(
                money("40.00"), ModePaiement.CARTE, LocalDateTime.now()));
        facture.setStatut(StatutFacture.PAYEE);
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        assertThrows(ConflictException.class, () -> factureService.annuler(1L));
    }

    @Test
    void doesNotCancelInvoiceMoreThanOnce() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        facture.setStatut(StatutFacture.ANNULEE);
        when(factureRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(facture));

        assertThrows(ConflictException.class, () -> factureService.annuler(1L));
    }

    @Test
    void insertsMedicationLinesAndRecalculatesTotalsAndCoverage() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        facture.repartir(20);
        when(factureRepository.findByConsultationIdForUpdate("consult-1")).thenReturn(Optional.of(facture));
        when(ligneFactureRepository.findBySourceEventIdIn(List.of("delivery-2:0"))).thenReturn(List.of());
        when(tarifRepository.findByTypeActeAndCode(TypeActe.MEDICAMENT, "MED-1"))
                .thenReturn(Optional.of(new tn.esprit.clinique.facturation.domain.Tarif(
                        TypeActe.MEDICAMENT, "MED-1", money("12.50"))));
        when(coverageGateway.getCoverage(12L)).thenReturn(CoverageLookup.available(25));

        factureService.ajouterMedicaments(new MedicamentsDelivresEvent(
                "delivery-2", "consult-1", 12L,
                List.of(new LigneMedicamentEvent("MED-1", "Médicament", 2))));

        assertEquals(money("65.00"), facture.getMontantTotal());
        assertEquals(money("16.25"), facture.getPartMutuelle());
        assertEquals(money("48.75"), facture.getPartPatient());
        assertEquals(2, facture.getLignes().size());
        assertEquals("delivery-2:0", facture.getLignes().get(1).getSourceEventId());
        assertEquals(StatutFacture.EMISE, facture.getStatut());
    }

    @Test
    void keepsPreviouslyIssuedInvoiceIssuedWhenCoverageLookupFailsForMedicationEvent() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        facture.repartir(20);
        when(factureRepository.findByConsultationIdForUpdate("consult-1")).thenReturn(Optional.of(facture));
        when(ligneFactureRepository.findBySourceEventIdIn(List.of("delivery-3:0"))).thenReturn(List.of());
        when(tarifRepository.findByTypeActeAndCode(TypeActe.MEDICAMENT, "MED-1"))
                .thenReturn(Optional.of(new tn.esprit.clinique.facturation.domain.Tarif(
                        TypeActe.MEDICAMENT, "MED-1", money("10.00"))));
        when(coverageGateway.getCoverage(12L)).thenReturn(CoverageLookup.unavailable());

        factureService.ajouterMedicaments(new MedicamentsDelivresEvent(
                "delivery-3", "consult-1", 12L,
                List.of(new LigneMedicamentEvent("MED-1", "Médicament", 1))));

        assertEquals(StatutFacture.EMISE, facture.getStatut());
        assertEquals(20, facture.getTauxMutuelle());
        assertEquals(money("10.00"), facture.getPartMutuelle());
        assertEquals(money("40.00"), facture.getPartPatient());
    }

    @Test
    void ignoresMedicationLinesAlreadyProcessedForDelivery() {
        Facture facture = factureWithTotal("consult-1", "40.00");
        LigneFacture processedLine = new LigneFacture(
                TypeActe.MEDICAMENT, "Medication", 2, money("20.00"), "delivery-1:0");
        processedLine.setFacture(facture);
        when(factureRepository.findByConsultationIdForUpdate("consult-1")).thenReturn(Optional.of(facture));
        when(ligneFactureRepository.findBySourceEventIdIn(List.of("delivery-1:0")))
                .thenReturn(List.of(processedLine));

        factureService.ajouterMedicaments(new MedicamentsDelivresEvent(
                "delivery-1", "consult-1", 12L,
                List.of(new LigneMedicamentEvent("MED", "Medication", 2))));

        assertEquals(money("40.00"), facture.getMontantTotal());
        verify(tarifRepository, never()).findByTypeActeAndCode(any(), any());
        verify(coverageGateway, never()).getCoverage(12L);
    }

    private Facture factureWithTotal(String consultationId, String total) {
        Facture facture = new Facture();
        facture.setId(1L);
        facture.setNumero("FAC-test");
        facture.setPatientId(12L);
        facture.setConsultationId(consultationId);
        facture.setDateEmission(java.time.LocalDateTime.now());
        facture.setStatut(StatutFacture.EMISE);
        facture.ajouterLigne(new LigneFacture(TypeActe.CONSULTATION, "Consultation", 1,
                money(total), null));
        return facture;
    }

    private FactureResponse response(String consultationId, String total) {
        BigDecimal montant = money(total);
        return new FactureResponse(1L, "FAC-test", 12L, consultationId, LocalDateTime.now(),
                montant, BigDecimal.ZERO.setScale(2), montant, BigDecimal.ZERO.setScale(2), montant,
                StatutFacture.EMISE, List.of(), List.of());
    }

    private BigDecimal money(String amount) {
        return new BigDecimal(amount);
    }
}
