package tn.esprit.clinique.facturation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.domain.TypeActe;

import java.util.Optional;

public interface TarifRepository extends JpaRepository<Tarif, Long> {

    Optional<Tarif> findByTypeActeAndCode(TypeActe typeActe, String code);
}
