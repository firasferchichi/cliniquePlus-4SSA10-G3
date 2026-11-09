package tn.esprit.clinique.consultation.repository;

import tn.esprit.clinique.consultation.domain.Consultation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsultationRepository extends MongoRepository<Consultation, String> {
}
