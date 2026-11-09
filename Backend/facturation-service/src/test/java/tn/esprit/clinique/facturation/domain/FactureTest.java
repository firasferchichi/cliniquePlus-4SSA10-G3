package tn.esprit.clinique.facturation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactureTest {

    @Test
    void calculatesTotalFromQuantityAndUnitPricesWithoutFloatingPointArithmetic() {
        Facture facture = new Facture();
        facture.ajouterLigne(new LigneFacture(TypeActe.EXAMEN, "Exam A", 3, new BigDecimal("12.35"), null));
        facture.ajouterLigne(new LigneFacture(TypeActe.MEDICAMENT, "Medicine", 2,
                new BigDecimal("0.10"), null));

        assertEquals(new BigDecimal("37.25"), facture.getMontantTotal());
    }

    @Test
    void splitsCoverageUsingTwoDecimalHalfUpRounding() {
        Facture facture = new Facture();
        facture.ajouterLigne(new LigneFacture(TypeActe.CONSULTATION, "Consultation", 1,
                new BigDecimal("10.05"), null));

        facture.repartir(33);

        assertEquals(new BigDecimal("3.32"), facture.getPartMutuelle());
        assertEquals(new BigDecimal("6.73"), facture.getPartPatient());
    }
}
