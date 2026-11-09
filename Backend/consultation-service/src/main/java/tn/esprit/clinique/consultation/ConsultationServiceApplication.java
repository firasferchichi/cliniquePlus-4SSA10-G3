package tn.esprit.clinique.consultation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Consultation — Dossier médical : consultations, constantes, diagnostics, examens demandés et ordonnances.
 */
@SpringBootApplication
@EnableFeignClients
public class ConsultationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultationServiceApplication.class, args);
    }
}
