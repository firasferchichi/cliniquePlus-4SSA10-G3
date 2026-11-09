package tn.esprit.clinique.facturation.event;

public record LigneMedicamentEvent(String code, String libelle, Integer quantite) {
}
