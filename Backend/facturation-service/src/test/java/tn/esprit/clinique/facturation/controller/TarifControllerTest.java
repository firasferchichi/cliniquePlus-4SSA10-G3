package tn.esprit.clinique.facturation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.clinique.facturation.domain.TypeActe;
import tn.esprit.clinique.facturation.dto.TarifResponse;
import tn.esprit.clinique.facturation.exception.GlobalExceptionHandler;
import tn.esprit.clinique.facturation.service.TarifService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TarifControllerTest {

    private TarifService tarifService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tarifService = mock(TarifService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TarifController(tarifService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsTariffList() throws Exception {
        when(tarifService.lister()).thenReturn(List.of(
                new TarifResponse(1L, TypeActe.CONSULTATION, "GEN", new BigDecimal("25.00"))));

        mockMvc.perform(get("/api/tarifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("GEN"))
                .andExpect(jsonPath("$[0].montant").value(25.0));
    }

    @Test
    void acceptsTariffUpsert() throws Exception {
        when(tarifService.mettreAJour(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of(
                new TarifResponse(1L, TypeActe.CONSULTATION, "GEN", new BigDecimal("25.00"))));

        mockMvc.perform(put("/api/tarifs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"typeActe":"CONSULTATION","code":"GEN","montant":25.00}]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void rejectsInvalidTariffRequest() throws Exception {
        mockMvc.perform(put("/api/tarifs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"typeActe":"CONSULTATION","code":" ","montant":-1}]
                                """))
                .andExpect(status().isBadRequest());
    }
}
