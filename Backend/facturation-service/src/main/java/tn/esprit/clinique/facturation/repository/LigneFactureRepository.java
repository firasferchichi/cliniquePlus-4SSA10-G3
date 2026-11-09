package tn.esprit.clinique.facturation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.clinique.facturation.domain.LigneFacture;

import java.util.Collection;
import java.util.List;

public interface LigneFactureRepository extends JpaRepository<LigneFacture, Long> {

    List<LigneFacture> findBySourceEventIdIn(Collection<String> sourceEventIds);
}
