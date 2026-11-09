package tn.esprit.clinique.facturation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.clinique.facturation.domain.Tarif;
import tn.esprit.clinique.facturation.dto.TarifRequest;
import tn.esprit.clinique.facturation.dto.TarifResponse;
import tn.esprit.clinique.facturation.exception.BusinessRuleException;
import tn.esprit.clinique.facturation.repository.TarifRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TarifService {

    private final TarifRepository tarifRepository;

    @Transactional(readOnly = true)
    public List<TarifResponse> lister() {
        return tarifRepository.findAll().stream().map(TarifResponse::from).toList();
    }

    @Transactional
    public List<TarifResponse> mettreAJour(List<TarifRequest> requests) {
        Set<String> keys = new HashSet<>();
        for (TarifRequest request : requests) {
            String key = request.typeActe() + ":" + request.code().trim();
            if (!keys.add(key)) {
                throw new BusinessRuleException("Duplicate tariff key in request: " + key);
            }
        }
        List<Tarif> tarifs = requests.stream().map(request ->
                tarifRepository.findByTypeActeAndCode(request.typeActe(), request.code().trim())
                        .map(tarif -> {
                            tarif.setMontant(request.montant());
                            return tarif;
                        })
                        .orElseGet(() -> new Tarif(request.typeActe(), request.code().trim(), request.montant()))
        ).toList();
        return tarifRepository.saveAll(tarifs).stream().map(TarifResponse::from).toList();
    }
}
