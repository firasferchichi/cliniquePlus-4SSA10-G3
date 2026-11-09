package tn.esprit.clinique.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Patient — Fiche administrative du patient, couverture sociale (CNAM / assurance), antécédents médicaux.
 */
@SpringBootApplication
@EnableFeignClients
public class PatientServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }
}
