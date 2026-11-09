package tn.esprit.clinique.personnel.repository;

import tn.esprit.clinique.personnel.domain.MembrePersonnel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembrePersonnelRepository extends JpaRepository<MembrePersonnel, Long> {
}
