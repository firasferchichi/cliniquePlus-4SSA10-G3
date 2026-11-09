package tn.esprit.clinique.facturation.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PatientServiceFallback implements PatientServiceClient {

    @Override
    public PatientCoverageResponse getCouverture(Long patientId) {
        log.warn("patient-service unavailable while retrieving coverage for patient {}", patientId);
        throw new CoverageUnavailableException("Patient coverage is temporarily unavailable");
    }
}
