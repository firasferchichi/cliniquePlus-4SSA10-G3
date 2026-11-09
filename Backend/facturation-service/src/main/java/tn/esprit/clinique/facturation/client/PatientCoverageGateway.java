package tn.esprit.clinique.facturation.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientCoverageGateway {

    private final PatientServiceClient patientServiceClient;

    @CircuitBreaker(name = "patientCoverage", fallbackMethod = "coverageFallback")
    public CoverageLookup getCoverage(Long patientId) {
        PatientCoverageResponse response = patientServiceClient.getCouverture(patientId);
        if (response == null || response.tauxPriseEnCharge() == null
                || response.tauxPriseEnCharge() < 0 || response.tauxPriseEnCharge() > 100) {
            throw new IllegalStateException("patient-service returned an invalid coverage rate");
        }
        return CoverageLookup.available(response.tauxPriseEnCharge());
    }

    private CoverageLookup coverageFallback(Long patientId, Throwable cause) {
        log.warn("Coverage lookup failed for patient {}; using 0% and leaving the invoice as a draft",
                patientId, cause);
        return CoverageLookup.unavailable();
    }
}
