package tn.esprit.clinique.facturation.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.LigneFacture;
import tn.esprit.clinique.facturation.domain.Paiement;
import tn.esprit.clinique.facturation.exception.ResourceNotFoundException;
import tn.esprit.clinique.facturation.repository.FactureRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturePdfService {

    private final FactureRepository factureRepository;

    public FacturePdfService(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generer(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice " + id + " was not found"));
        List<String> paragraphs = new ArrayList<>();
        paragraphs.add("CLINIQUE+ - FACTURE");
        paragraphs.add("Numero: " + facture.getNumero());
        paragraphs.add("Date: " + facture.getDateEmission());
        paragraphs.add("Patient: " + facture.getPatientId());
        paragraphs.add("Consultation: " + facture.getConsultationId());
        paragraphs.add("Statut: " + facture.getStatut());
        paragraphs.add("");
        paragraphs.add("LIGNES");
        for (LigneFacture ligne : facture.getLignes()) {
            paragraphs.add(ligne.getTypeActe() + " | " + ligne.getLibelle() + " | "
                    + ligne.getQuantite() + " x " + ligne.getPrixUnitaire()
                    + " = " + ligne.getMontant());
        }
        paragraphs.add("");
        paragraphs.add("Total: " + facture.getMontantTotal());
        paragraphs.add("Part mutuelle: " + facture.getPartMutuelle());
        paragraphs.add("Part patient: " + facture.getPartPatient());
        paragraphs.add("Deja paye: " + facture.montantPaye());
        paragraphs.add("Solde restant: " + facture.soldeRestant());
        for (Paiement paiement : facture.getPaiements()) {
            paragraphs.add("Paiement " + paiement.getDate() + " | " + paiement.getMode()
                    + " | " + paiement.getMontant());
        }
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            float margin = 45;
            float leading = 15;
            float maxWidth = PDRectangle.A4.getWidth() - 2 * margin;
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            try {
                content.beginText();
                content.setFont(font, 10);
                content.setLeading(leading);
                content.newLineAtOffset(margin, PDRectangle.A4.getHeight() - margin);
                float y = PDRectangle.A4.getHeight() - margin;
                for (String paragraph : paragraphs) {
                    for (String line : wrap(paragraph, font, maxWidth)) {
                        if (y - leading < margin) {
                            content.endText();
                            content.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            content = new PDPageContentStream(document, page);
                            content.beginText();
                            content.setFont(font, 10);
                            content.setLeading(leading);
                            content.newLineAtOffset(margin, PDRectangle.A4.getHeight() - margin);
                            y = PDRectangle.A4.getHeight() - margin;
                        }
                        content.showText(line);
                        content.newLine();
                        y -= leading;
                    }
                }
                content.endText();
            } finally {
                content.close();
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not generate invoice PDF", exception);
        }
    }

    private List<String> wrap(String paragraph, PDType1Font font, float maxWidth) throws IOException {
        if (paragraph.isBlank()) {
            return List.of("");
        }
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : paragraph.split("\\s+")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (!current.isEmpty() && font.getStringWidth(candidate) / 1000 * 10 > maxWidth) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if (font.getStringWidth(word) / 1000 * 10 > maxWidth) {
                throw new IllegalArgumentException("Invoice text contains an unbreakable value wider than the PDF page");
            }
            if (!current.isEmpty()) {
                current.append(' ');
            }
            current.append(word);
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }
}
