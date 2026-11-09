package tn.esprit.clinique.personnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Personnel médical — Médecins, infirmiers, secrétaires et pharmaciens ; spécialités, horaires de travail et absences.
 */
@SpringBootApplication
@EnableFeignClients
public class PersonnelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonnelServiceApplication.class, args);
    }
}
