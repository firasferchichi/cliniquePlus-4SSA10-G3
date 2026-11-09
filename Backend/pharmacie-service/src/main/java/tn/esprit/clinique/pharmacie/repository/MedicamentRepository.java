package tn.esprit.clinique.pharmacie.repository;

import tn.esprit.clinique.pharmacie.domain.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
}
