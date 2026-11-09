package tn.esprit.clinique.personnel.repository;

import tn.esprit.clinique.personnel.domain.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
}
