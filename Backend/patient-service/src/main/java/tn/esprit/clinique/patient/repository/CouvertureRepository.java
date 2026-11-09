package tn.esprit.clinique.patient.repository;

import tn.esprit.clinique.patient.domain.Couverture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouvertureRepository extends JpaRepository<Couverture, Long> {
}
