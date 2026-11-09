package tn.esprit.clinique.pharmacie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Pharmacie — Catalogue de médicaments, stock par lots (péremption), délivrance des ordonnances.
 */
@SpringBootApplication
@EnableFeignClients
public class PharmacieServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacieServiceApplication.class, args);
    }
}
