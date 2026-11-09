package tn.esprit.clinique.facturation.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PatientCoverageResponse(Integer tauxPriseEnCharge) {
}
