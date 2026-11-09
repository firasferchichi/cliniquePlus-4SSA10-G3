package tn.esprit.clinique.facturation.service;

import org.junit.jupiter.api.Test;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.repository.FactureRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FacturePdfServiceTest {

    @Test
    void generatesPaginatedPdfWithFrenchAccents() throws Exception {
        Facture facture = new Facture();
        facture.setId(5L);
        facture.setNumero("FAC-test");
        facture.setPatientId(12L);
        facture.setConsultationId("consult-1");
        facture.setDateEmission(LocalDateTime.now());
        facture.setStatut(StatutFacture.EMISE);
        for (int index = 0; index < 100; index++) {
            facture.ajouterLigne(new LigneFacture(TypeActe.CONSULTATION,
                    "Consultation médicale française numéro " + index, 1, new BigDecimal("45.00"), null));
        }
        FactureRepository repository = mock(FactureRepository.class);
        when(repository.findById(5L)).thenReturn(Optional.of(facture));

        byte[] pdf = new FacturePdfService(repository).generer(5L);

        assertTrue(new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF"));
        try (var document = Loader.loadPDF(pdf)) {
            assertTrue(document.getNumberOfPages() > 1);
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("médicale française"));
        }
    }
}
