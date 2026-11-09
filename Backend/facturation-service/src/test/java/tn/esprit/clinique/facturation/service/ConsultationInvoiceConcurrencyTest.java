package tn.esprit.clinique.facturation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.clinique.facturation.client.CoverageLookup;
import tn.esprit.clinique.facturation.client.PatientCoverageGateway;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.event.ActeFacturableEvent;
import tn.esprit.clinique.facturation.event.ConsultationTermineeEvent;
import tn.esprit.clinique.facturation.repository.FactureRepository;
import tn.esprit.clinique.facturation.repository.LigneFactureRepository;
import tn.esprit.clinique.facturation.repository.TarifRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({FactureService.class, ConsultationInvoiceCreator.class})
class ConsultationInvoiceConcurrencyTest {

    @Autowired
    private FactureService factureService;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private TarifRepository tarifRepository;

    @Autowired
    private LigneFactureRepository ligneFactureRepository;

    @MockitoBean
    private PatientCoverageGateway coverageGateway;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void concurrentDuplicateEventsCreateOnlyOneInvoice() throws Exception {
        tarifRepository.saveAndFlush(new Tarif(TypeActe.CONSULTATION, "GEN", new BigDecimal("50.00")));
        CountDownLatch bothTransactionsHaveObservedNoInvoice = new CountDownLatch(2);
        when(coverageGateway.getCoverage(12L)).thenAnswer(invocation -> {
            bothTransactionsHaveObservedNoInvoice.countDown();
            if (!bothTransactionsHaveObservedNoInvoice.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Concurrent transactions did not reach coverage lookup");
            }
            return CoverageLookup.available(25);
        });
        ConsultationTermineeEvent event = new ConsultationTermineeEvent(
                "consult-concurrent", 15L, 12L, 20L, "GEN",
                List.of(new ActeFacturableEvent(TypeActe.CONSULTATION, "GEN", "Consultation", 1)));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> factureService.creerDepuisConsultation(event));
            Future<?> second = executor.submit(() -> factureService.creerDepuisConsultation(event));
            first.get(20, TimeUnit.SECONDS);
            second.get(20, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }

        var invoices = factureRepository.findAll();
        assertEquals(1, invoices.size());
        assertEquals("consult-concurrent", invoices.getFirst().getConsultationId());
        assertEquals(1, ligneFactureRepository.count());
    }
}
