package tn.esprit.clinique.facturation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.clinique.facturation.dto.TarifRequest;
import tn.esprit.clinique.facturation.dto.TarifResponse;
import tn.esprit.clinique.facturation.service.TarifService;

import java.util.List;

@RestController
@RequestMapping("/api/tarifs")
@RequiredArgsConstructor
public class TarifController {

    private final TarifService tarifService;

    @GetMapping
    public List<TarifResponse> lister() {
        return tarifService.lister();
    }

    @PutMapping
    public ResponseEntity<List<TarifResponse>> mettreAJour(
            @Valid @RequestBody List<@Valid TarifRequest> requests) {
        return ResponseEntity.ok(tarifService.mettreAJour(requests));
    }
}
