package tn.esprit.clinique.facturation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
@Getter
@Setter
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 50)
    private String numero;

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @NotNull
    @Column(nullable = false, unique = true, length = 100)
    private String consultationId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateEmission;

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO.setScale(2);

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal partMutuelle = BigDecimal.ZERO.setScale(2);

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal partPatient = BigDecimal.ZERO.setScale(2);

    @Min(0)
    @Max(100)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int tauxMutuelle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutFacture statut;

    @Version
    private long version;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LigneFacture> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paiement> paiements = new ArrayList<>();

    public void ajouterLigne(LigneFacture ligne) {
        ligne.setFacture(this);
        lignes.add(ligne);
        recalculerMontantTotal();
    }

    public void ajouterPaiement(Paiement paiement) {
        paiement.setFacture(this);
        paiements.add(paiement);
    }

    public void recalculerMontantTotal() {
        montantTotal = lignes.stream()
                .map(LigneFacture::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal montantPaye() {
        return paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal soldeRestant() {
        return montantTotal.subtract(montantPaye()).setScale(2, RoundingMode.HALF_UP);
    }

    public void repartir(int tauxMutuelle) {
        if (tauxMutuelle < 0 || tauxMutuelle > 100) {
            throw new IllegalArgumentException("Coverage rate must be between 0 and 100");
        }
        this.tauxMutuelle = tauxMutuelle;
        partMutuelle = montantTotal.multiply(BigDecimal.valueOf(tauxMutuelle))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        partPatient = montantTotal.subtract(partMutuelle).setScale(2, RoundingMode.HALF_UP);
    }
}
