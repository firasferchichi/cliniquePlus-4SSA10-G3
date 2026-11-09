package tn.esprit.clinique.patient.repository;

import tn.esprit.clinique.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
