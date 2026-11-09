package tn.esprit.clinique.facturation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", fallback = PatientServiceFallback.class)
public interface PatientServiceClient {

    @GetMapping("/api/patients/{id}/couverture")
    PatientCoverageResponse getCouverture(@PathVariable("id") Long patientId);
}
