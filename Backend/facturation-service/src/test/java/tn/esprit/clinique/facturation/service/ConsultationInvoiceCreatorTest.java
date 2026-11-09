package tn.esprit.clinique.facturation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.clinique.facturation.client.CoverageLookup;
import tn.esprit.clinique.facturation.client.PatientCoverageGateway;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.event.ActeFacturableEvent;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.repository.FactureRepository;
import tn.esprit.clinique.facturation.repository.TarifRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class ConsultationInvoiceCreatorTest {

    @Mock
    private FactureRepository factureRepository;
    @Mock
    private TarifRepository tarifRepository;
    @Mock
    private PatientCoverageGateway coverageGateway;
    @InjectMocks
    private ConsultationInvoiceCreator creator;

    @Test
    void createsInvoiceAndCalculatesCoverageShares() {
        when(factureRepository.findByConsultationId("consult-1")).thenReturn(Optional.empty());
        when(tarifRepository.findByTypeActeAndCode(TypeActe.CONSULTATION, "GEN"))
                .thenReturn(Optional.of(new Tarif(TypeActe.CONSULTATION, "GEN", money("100.00"))));
        when(coverageGateway.getCoverage(12L)).thenReturn(CoverageLookup.available(30));
        when(factureRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = creator.creer(event("consult-1"));

        assertEquals(money("100.00"), response.montantTotal());
        assertEquals(money("30.00"), response.partMutuelle());
        assertEquals(money("70.00"), response.partPatient());
        assertEquals(StatutFacture.EMISE, response.statut());
        ArgumentCaptor<Facture> savedInvoice = ArgumentCaptor.forClass(Facture.class);
        verify(factureRepository).saveAndFlush(savedInvoice.capture());
        assertEquals(30, savedInvoice.getValue().getTauxMutuelle());
    }

    @Test
    void createsDraftWithZeroCoverageWhenPatientServiceIsUnavailable() {
        when(factureRepository.findByConsultationId("consult-2")).thenReturn(Optional.empty());
        when(tarifRepository.findByTypeActeAndCode(TypeActe.CONSULTATION, "GEN"))
                .thenReturn(Optional.of(new Tarif(TypeActe.CONSULTATION, "GEN", money("80.00"))));
        when(coverageGateway.getCoverage(12L)).thenReturn(CoverageLookup.unavailable());
        when(factureRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = creator.creer(event("consult-2"));

        assertEquals(StatutFacture.BROUILLON, response.statut());
        assertEquals(money("0.00"), response.partMutuelle());
        assertEquals(money("80.00"), response.partPatient());
        verify(coverageGateway).getCoverage(12L);
    }

    @Test
    void existingConsultationInvoiceIsReturnedWithoutRecreation() {
        Facture existingInvoice = new Facture();
        existingInvoice.setStatut(StatutFacture.BROUILLON);
        when(factureRepository.findByConsultationId("consult-3"))
                .thenReturn(Optional.of(existingInvoice));

        var response = creator.creer(event("consult-3"));

        assertEquals(StatutFacture.BROUILLON, response.statut());
        verify(factureRepository, never()).saveAndFlush(any());
        verify(tarifRepository, never()).findByTypeActeAndCode(any(), any());
        verify(coverageGateway, never()).getCoverage(any());
    }

    private ConsultationTermineeEvent event(String consultationId) {
        return new ConsultationTermineeEvent(consultationId, 15L, 12L, 20L, "GEN",
                List.of(new ActeFacturableEvent(TypeActe.CONSULTATION, "GEN", "Consultation", 1)));
    }

    private BigDecimal money(String amount) {
        return new BigDecimal(amount);
    }
}
