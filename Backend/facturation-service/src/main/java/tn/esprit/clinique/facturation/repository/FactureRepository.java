package tn.esprit.clinique.facturation.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.clinique.facturation.domain.Facture;
import tn.esprit.clinique.facturation.domain.StatutFacture;

import java.util.List;
import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    boolean existsByConsultationId(String consultationId);

    Optional<Facture> findByConsultationId(String consultationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Facture f where f.consultationId = :consultationId")
    Optional<Facture> findByConsultationIdForUpdate(@Param("consultationId") String consultationId);

    List<Facture> findByPatientId(Long patientId);

    List<Facture> findByStatut(StatutFacture statut);

    List<Facture> findByPatientIdAndStatut(Long patientId, StatutFacture statut);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Facture f where f.id = :id")
    Optional<Facture> findByIdForUpdate(@Param("id") Long id);
}
