package tn.esprit.clinique.pharmacie.repository;

import tn.esprit.clinique.pharmacie.domain.Delivrance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelivranceRepository extends JpaRepository<Delivrance, Long> {
}
