package tn.esprit.clinique.facturation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.clinique.facturation.domain.StatutFacture;
import tn.esprit.clinique.facturation.dto.FactureResponse;
import tn.esprit.clinique.facturation.exception.GlobalExceptionHandler;
import tn.esprit.clinique.facturation.exception.ResourceNotFoundException;
import tn.esprit.clinique.facturation.service.FacturePdfService;
import tn.esprit.clinique.facturation.service.FactureService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

class FactureControllerTest {

    private FactureService factureService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        factureService = mock(FactureService.class);
        FacturePdfService pdfService = mock(FacturePdfService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FactureController(factureService, pdfService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsFilteredInvoiceList() throws Exception {
        when(factureService.lister(12L, StatutFacture.EMISE)).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/factures")
                        .queryParam("patientId", "12")
                        .queryParam("statut", "EMISE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value("FAC-test"));
    }

    @Test
    void mapsMissingInvoiceToNotFound() throws Exception {
        when(factureService.obtenir(88L)).thenThrow(new ResourceNotFoundException("Invoice was not found"));

        mockMvc.perform(get("/api/factures/88"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void acceptsPaymentAndReturnsCreatedStatus() throws Exception {
        when(factureService.enregistrerPaiement(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any())).thenReturn(response());

        mockMvc.perform(post("/api/factures/1/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"montant": 25.00, "mode": "CARTE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void reportsMalformedJsonWithCentralizedErrorResponse() throws Exception {
        mockMvc.perform(post("/api/factures/1/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed or invalid JSON request"));
    }

    @Test
    void rejectsInvalidPaymentRequest() throws Exception {
        mockMvc.perform(post("/api/factures/1/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"montant": 0, "mode": "CARTE"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsPdfContentTypeAndBody() throws Exception {
        FacturePdfService pdfService = mock(FacturePdfService.class);
        byte[] pdf = "%PDF-test".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(pdfService.generer(1L)).thenReturn(pdf);
        mockMvc = MockMvcBuilders.standaloneSetup(new FactureController(factureService, pdfService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/factures/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().bytes(pdf));
        verify(pdfService).generer(1L);
    }

    private FactureResponse response() {
        return new FactureResponse(1L, "FAC-test", 12L, "consult-1", LocalDateTime.now(),
                new BigDecimal("100.00"), new BigDecimal("30.00"), new BigDecimal("70.00"),
                BigDecimal.ZERO.setScale(2), new BigDecimal("100.00"), StatutFacture.EMISE,
                List.of(), List.of());
    }
}
