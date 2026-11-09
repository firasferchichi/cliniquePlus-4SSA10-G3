package tn.esprit.clinique.facturation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.dto.FactureResponse;
import tn.esprit.clinique.facturation.dto.PaiementRequest;
import tn.esprit.clinique.facturation.service.FacturePdfService;
import tn.esprit.clinique.facturation.service.FactureService;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final FacturePdfService facturePdfService;

    @GetMapping
    public List<FactureResponse> lister(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) StatutFacture statut) {
        return factureService.lister(patientId, statut);
    }

    @GetMapping("/{id}")
    public FactureResponse obtenir(@PathVariable Long id) {
        return factureService.obtenir(id);
    }

    @PostMapping("/{id}/paiements")
    public ResponseEntity<FactureResponse> payer(@PathVariable Long id,
                                                  @Valid @RequestBody PaiementRequest request) {
        return ResponseEntity.status(201).body(factureService.enregistrerPaiement(id, request));
    }

    @PatchMapping("/{id}/annuler")
    public FactureResponse annuler(@PathVariable Long id) {
        return factureService.annuler(id);
    }

    @PostMapping("/{id}/recalculer-couverture")
    public FactureResponse recalculerCouverture(@PathVariable Long id) {
        return factureService.recalculerCouverture(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> telechargerPdf(@PathVariable Long id) {
        byte[] pdf = facturePdfService.generer(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("facture-" + id + ".pdf").build().toString())
                .body(pdf);
    }
}
