package tn.esprit.clinique.rendezvous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Rendez-vous — Prise, confirmation, annulation et suivi des rendez-vous ; calcul des créneaux libres.
 */
@SpringBootApplication
@EnableFeignClients
public class RendezvousServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RendezvousServiceApplication.class, args);
    }
}
