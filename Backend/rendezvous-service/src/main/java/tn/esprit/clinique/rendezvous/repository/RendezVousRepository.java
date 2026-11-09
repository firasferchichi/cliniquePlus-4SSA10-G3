package tn.esprit.clinique.rendezvous.repository;

import tn.esprit.clinique.rendezvous.domain.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
}
